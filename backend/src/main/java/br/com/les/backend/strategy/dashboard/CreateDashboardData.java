package br.com.les.backend.strategy.dashboard;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Strings;

import br.com.les.backend.dao.DashboardFilterDAO;
import br.com.les.backend.entity.BankedHours;
import br.com.les.backend.entity.MonthlyBalance;
import br.com.les.backend.entity.Parameter;
import br.com.les.backend.filter.DashboardFilter;
import br.com.les.backend.navigator.INavigationCase;
import br.com.les.backend.navigator.IStrategy;
import br.com.les.backend.repository.BankedHoursRepository;
import br.com.les.backend.repository.ParameterRepository;

@Configuration
public class CreateDashboardData implements IStrategy<DashboardFilter> {
	
	@Autowired BankedHoursRepository bankedHoursRepository;
	@Autowired ParameterRepository parameterRepository;
	@Autowired DashboardFilterDAO dashboardFilterDAO;
	
	private List<MonthlyBalance> m = new ArrayList<>();
	private List<DashboardFilter> dashboardFilterList;
	private DashboardFilter employeeDashboardData;
	private LocalDate limitDate;
	private long employeeId = 0;
	private boolean theLastBalanceForThisUserHasEvaluated = false;
	
	@Override
	public void process(DashboardFilter aEntity, INavigationCase<DashboardFilter> aCase) {
 
		dashboardFilterList = new ArrayList<>();
		searchEveryBalanceWithinParameterLimit(aCase);
		
		if(aCase.getResult().isSuccess()) {
			
			calculateTheLastMonthBalance();
			sortMonthlyBalanceList();
			logInConsole();
			m.forEach(monthlyBalance -> {
				
				if(employeeId != monthlyBalance.getEmployee().getId()) {
					theLastBalanceForThisUserHasEvaluated = false;
					employeeId = monthlyBalance.getEmployee().getId();
					employeeDashboardData = new DashboardFilter();
					dashboardFilterList.add(employeeDashboardData);
				}
				
				if(monthlyBalance.getAbscenseHours() != 0 || monthlyBalance.getAbscenseMinutes() != 0 
				|| monthlyBalance.getOvertimeHours() != 0 || monthlyBalance.getOvertimeMinutes() != 0) {
					
					// this is this last month which the balance have some time
					if(!theLastBalanceForThisUserHasEvaluated) {
						
						employeeDashboardData.setEmployeeFullName(fetchUserFullName(monthlyBalance));
						
						employeeDashboardData.setHoursInLastMonth(fetchHoursInLastMonth(monthlyBalance));
						employeeDashboardData.setHoursInLastMonthMinutes(fetchLastMonthHoursInMinutes(monthlyBalance));

						employeeDashboardData.setLastMonth(fetchLastMonthWhitBalance(monthlyBalance, aCase));
						employeeDashboardData.setLastMonthNumber(fetchMonthNumber(monthlyBalance));
						
						employeeDashboardData.setBalance(fetchBalance(monthlyBalance));
						employeeDashboardData.setBalanceInMinutes(fetchBalanceInMinutes(monthlyBalance));
						
						if(!Strings.isNullOrEmpty(employeeDashboardData.getHoursInLastMonth()) 
								&& !"0:00".equals(employeeDashboardData.getHoursInLastMonth())) {
							theLastBalanceForThisUserHasEvaluated = true;
						}
									
					}
										
					// still not have this data
					/*employeeDashboardData.setWorkedHourComercial();
						employeeDashboardData.setWorkedHourNight();*/
				}
				employeeDashboardData.setCountingFromMonth(fetchWhichMonthIsTheFirstToCountInParameters());
				employeeDashboardData.setCountingFromMonthNumber(fetchMonthNumber(null));
				
				employeeDashboardData
					.setShouldBeWorked(fetchHoursThatWasNeedToBeWorkedInPeriod(monthlyBalance,
						employeeDashboardData.getShouldBeWorked()));
				
				employeeDashboardData
					.setWorkedHoursOnTheParameterPeriod(fetchWorkedHours(monthlyBalance, 
						employeeDashboardData.getWorkedHoursOnTheParameterPeriod()));
				
				employeeDashboardData
					.setWorkedHoursOnTheParameterPeriodMinutes(fetchWorkedHoursInMinutes(monthlyBalance,
						employeeDashboardData.getWorkedHoursOnTheParameterPeriodMinutes()));
			});
			aCase.getResult().setResultList(dashboardFilterList);
		}
	}

	private String fetchWhichMonthIsTheFirstToCountInParameters() {
		return limitDate.format(DateTimeFormatter.ofPattern("MM/yyyy")).toString();
	}
	
	private String formatHoursToString(long minutes) {
		String minute = String.valueOf(minutes - minutes / 60 * 60).replace("-", "");
		if(minute.length() < 2)
			minute = "0".concat(minute);
		return  String.valueOf(minutes / 60).concat(":").concat(minute);
	}
	
	private String fetchBalance(MonthlyBalance monthlyBalance) {
		Optional<BankedHours> bankedHours = bankedHoursRepository.findByEmployeeId(monthlyBalance.getEmployee().getId());
		if(bankedHours.isPresent()) {
			Double balance = bankedHours.get().getBalance();
			return formatHoursToString(balance.intValue() * 60 + (int)((balance - balance.intValue()) * 60));
		}
		return null;
	}

	private Long fetchBalanceInMinutes(MonthlyBalance monthlyBalance) {
		Optional<BankedHours> bankedHours = bankedHoursRepository.findByEmployeeId(monthlyBalance.getEmployee().getId());
		if(bankedHours.isPresent()) {
			Double balance = bankedHours.get().getBalance();
			return (long) (balance.intValue() * 60 + (int)((balance - balance.intValue()) * 60));
		}
		return null;
	}
	
	private String fetchHoursThatWasNeedToBeWorkedInPeriod(MonthlyBalance monthlyBalance, String shouldBeWorked) {
		long minutes = 0;
		if(!Strings.isNullOrEmpty(shouldBeWorked)) {
			minutes = Integer.parseInt(shouldBeWorked.substring(0, shouldBeWorked.indexOf(":"))) * 60
					+ Integer.parseInt(shouldBeWorked.substring(shouldBeWorked.indexOf(":") + 1, shouldBeWorked.length()));
		}
		return formatHoursToString(monthlyBalance.getMonthWorkload() * 60 + minutes); 
	}
	
	private String fetchWorkedHours(MonthlyBalance monthlyBalance, String previousValue) {
		long minutes = 0;
		if(!Strings.isNullOrEmpty(previousValue)) {
			minutes = Integer.parseInt(previousValue.substring(0, previousValue.indexOf(":"))) * 60
					+ Integer.parseInt(previousValue.substring(previousValue.indexOf(":") + 1, previousValue.length()));
		}
		return formatHoursToString(monthlyBalance.getBalanceHours() * 60 + monthlyBalance.getBalanceMinutes() + minutes);
	}

	private Long fetchWorkedHoursInMinutes(MonthlyBalance monthlyBalance, Long previousValue) {
		return monthlyBalance.getBalanceHours() * 60 + monthlyBalance.getBalanceMinutes() + (previousValue != null ? previousValue : 0); 
	}
	
	private String fetchHoursInLastMonth(MonthlyBalance monthlyBalance) {
		return formatHoursToString((monthlyBalance.getOvertimeHours() * 60 + monthlyBalance.getOvertimeMinutes())
				- (monthlyBalance.getAbscenseHours() * 60 + monthlyBalance.getAbscenseMinutes()));
	}

	private Long fetchLastMonthHoursInMinutes(MonthlyBalance monthlyBalance) {
		return (long) ((monthlyBalance.getOvertimeHours() * 60 + monthlyBalance.getOvertimeMinutes())
				- (monthlyBalance.getAbscenseHours() * 60 + monthlyBalance.getAbscenseMinutes()));
	}
	
	private String fetchLastMonthWhitBalance(MonthlyBalance monthlyBalance, INavigationCase<DashboardFilter> aCase) {
		return monthlyBalance.getMonthAndYear().getMonth().getDisplayName(TextStyle.FULL, new Locale("pt"));
	}
	
	private Short fetchMonthNumber(MonthlyBalance monthlyBalance) {
		if(monthlyBalance != null)
			return (short) (monthlyBalance.getMonthAndYear().getMonth().ordinal() + 1);		
		return (short) (limitDate.getMonth().ordinal() + 1);
	}
	
	private String fetchUserFullName(MonthlyBalance monthlyBalance) {
		return monthlyBalance.getEmployee().getName()
				.concat(" ").concat(monthlyBalance.getEmployee().getLastName());
	}

	private void calculateTheLastMonthBalance() {
		long empId = 0;
		for(int current = 0; current < m.size() -1; current++) {
			for(int next = current + 1; next < m.size(); next++) {
			
				long currentEmployeeId = m.get(current).getEmployee().getId();
				
				if(currentEmployeeId != m.get(next).getEmployee().getId())
					break;
				
				if(empId != currentEmployeeId)
					empId = currentEmployeeId;			
				
				long currentMonthBalance = 
						(m.get(current).getOvertimeHours() * 60 + m.get(current).getOvertimeMinutes())
						- (m.get(current).getAbscenseHours() * 60 + m.get(current).getAbscenseMinutes());
				
				long nextMonthBalance = 
						(m.get(next).getOvertimeHours() * 60 + m.get(next).getOvertimeMinutes()) 
						- (m.get(next).getAbscenseHours() * 60 + m.get(next).getAbscenseMinutes());
				
				if(currentMonthBalance > 0 && nextMonthBalance < 0 || currentMonthBalance < 0 && nextMonthBalance > 0) {
					boolean breakLoop = applicateNewValues(m, current, next, currentMonthBalance, nextMonthBalance);
					if(breakLoop) break;
				}
			}
		}
		logInConsole();
	}

	private void logInConsole() {
		m.forEach(i -> {
			System.out.println(i.getEmployee().getId() 
					+ ": " 
					+ i.getMonthAndYear() 
					+ ", abscenseHours:" + i.getAbscenseHours() 
					+ ", abscenseMinutes: " + i.getAbscenseMinutes()
					+ ", overtimeHours: " + i.getOvertimeHours()
					+ ", overtimeMinutes: " + i.getOvertimeMinutes());
		});		
	}

	/**
	 * Fetches every monthly balance that fits the data within the 
	 * non compensatory limit parameter for every active employee, 
	 * then sort the values based on employee and the month
	 * @param aCase 
	 */
	private void searchEveryBalanceWithinParameterLimit(INavigationCase<DashboardFilter> aCase) {
		
		List<Parameter> parameterList = parameterRepository.findAllActive();
		if(parameterList != null && !parameterList.isEmpty()) {
			int limitMonth = parameterList.get(0).getBankCompensationLimitTime();
		
			limitDate = LocalDate.now().minusMonths(limitMonth).withDayOfMonth(1);
				
			m = dashboardFilterDAO.findDashboardData(limitDate);
			
			sortMonthlyBalanceList();
			
		} else {
			aCase.suspendExecution();
			aCase.getResult().setError("Erro durante consulta de parâmetros!");
		}
	}

	private void sortMonthlyBalanceList() {
		m.sort((m1, m2) -> {
			if(!m1.getEmployee().getId().equals(m2.getEmployee().getId()))
				return m1.getEmployee().getId().compareTo(m2.getEmployee().getId());
			else
				return m1.getMonthAndYear().compareTo(m2.getMonthAndYear());
		});
	}

	/**
	 * 
	 * @param m 	List that contains every month with each user and his balance
	 * @param current 	index for the current employee
	 * @param next 	index for the next employee
	 * @param currentMonthBalance 	current value used to calculate with the next value,
	 * this will validate if the current balance should be cleared and then, 
	 * recalculate and set the next value to his new calculated value, 
	 * the same goes for the case when the next balance value is less than the current
	 * @param nextMonthBalance 	as said above
	 * @return clearCurrent 	this variable is used to verify which one of the indexes
	 * should be cleared, also, is returned to activate the loop break, it's valid cause 
	 * booth behaviors are the same
	 */
	private boolean applicateNewValues(List<MonthlyBalance> m, int current,
			int next, long currentMonthBalance, long nextMonthBalance) {
		
		boolean clearCurrent = false;
		if((currentMonthBalance > 0 && currentMonthBalance + nextMonthBalance <= 0) 
		|| (currentMonthBalance <= 0 && currentMonthBalance + nextMonthBalance >= 0)) 
			clearCurrent = true;
		
		m.get(clearCurrent ? current : next).setAbscenseHours(0);
		m.get(clearCurrent ? current : next).setAbscenseMinutes(0);
		m.get(clearCurrent ? current : next).setOvertimeHours(0);
		m.get(clearCurrent ? current : next).setOvertimeMinutes(0);

		// adiciona os valores que sobraram em extra ou em falta
		long minutesLeft = currentMonthBalance + nextMonthBalance;
		if(minutesLeft > 0) {
			m.get(!clearCurrent ? current : next).setAbscenseHours(0);
			m.get(!clearCurrent ? current : next).setAbscenseMinutes(0);
			m.get(!clearCurrent ? current : next).setOvertimeHours((int)minutesLeft  / 60);
			m.get(!clearCurrent ? current : next).setOvertimeMinutes((int)(minutesLeft - minutesLeft / 60 * 60));
		} else {
			m.get(!clearCurrent ? current : next).setAbscenseHours((int)minutesLeft * -1 / 60);
			m.get(!clearCurrent ? current : next).setAbscenseMinutes((int)(minutesLeft - minutesLeft / 60 * 60) * -1);
			m.get(!clearCurrent ? current : next).setOvertimeHours(0);
			m.get(!clearCurrent ? current : next).setOvertimeMinutes(0);
		}
		return clearCurrent;
	}
}
