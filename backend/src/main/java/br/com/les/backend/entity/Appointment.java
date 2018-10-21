package br.com.les.backend.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.stereotype.Component;

@Component
@Entity
public class Appointment extends DomainEntity {
	
	private LocalDate date;
	private LocalTime morningEntrance;
	private LocalTime morningOut;
	private LocalTime afternoonEntrance;
	private LocalTime afternoonOut;
	private LocalTime nightEntrance;
	private LocalTime nightOut;
	private LocalTime particularExit;
	private LocalTime particularExitReturn;
	private LocalTime balance;
	private LocalTime hoursLeft;
	private LocalTime dayOvertime;
	
	@ManyToOne( fetch=FetchType.EAGER )
	@JoinColumn( name="employee_id" )
	private Employee employee;
	
	@OneToMany( cascade=CascadeType.ALL )
	@LazyCollection(LazyCollectionOption.FALSE)
	private List< HistChangeMonthlyBalance > histChangeMonthlyBalanceList;

	public Appointment() {}
	
	public Appointment( Long employeeId ) {
		this.employee = new Employee( employeeId );;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getMorningEntrance() {
		return morningEntrance;
	}

	public void setMorningEntrance(LocalTime morningEntrance) {
		this.morningEntrance = morningEntrance;
	}

	public LocalTime getMorningOut() {
		return morningOut;
	}

	public void setMorningOut(LocalTime morningOut) {
		this.morningOut = morningOut;
	}

	public LocalTime getAfternoonEntrance() {
		return afternoonEntrance;
	}

	public void setAfternoonEntrance(LocalTime afternoonEntrance) {
		this.afternoonEntrance = afternoonEntrance;
	}

	public LocalTime getAfternoonOut() {
		return afternoonOut;
	}

	public void setAfternoonOut(LocalTime afternoonOut) {
		this.afternoonOut = afternoonOut;
	}

	public LocalTime getNightEntrance() {
		return nightEntrance;
	}

	public void setNightEntrance(LocalTime nightEntrance) {
		this.nightEntrance = nightEntrance;
	}

	public LocalTime getNightOut() {
		return nightOut;
	}

	public void setNightOut(LocalTime nightOut) {
		this.nightOut = nightOut;
	}

	public LocalTime getParticularExit() {
		return particularExit;
	}

	public void setParticularExit(LocalTime particularExit) {
		this.particularExit = particularExit;
	}

	public LocalTime getParticularExitReturn() {
		return particularExitReturn;
	}

	public void setParticularExitReturn(LocalTime particularExitReturn) {
		this.particularExitReturn = particularExitReturn;
	}

	public LocalTime getBalance() {
		return balance;
	}

	public void setBalance(LocalTime balance) {
		this.balance = balance;
	}

	public LocalTime getHoursLeft() {
		return hoursLeft;
	}

	public void setHoursLeft(LocalTime hoursLeft) {
		this.hoursLeft = hoursLeft;
	}

	public LocalTime getDayOvertime() {
		return dayOvertime;
	}

	public void setDayOvertime(LocalTime dayOvertime) {
		this.dayOvertime = dayOvertime;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public List<HistChangeMonthlyBalance> getHistChangeMonthlyBalanceList() {
		return histChangeMonthlyBalanceList;
	}

	public void setHistChangeMonthlyBalanceList(List<HistChangeMonthlyBalance> histChangeMonthlyBalanceList) {
		this.histChangeMonthlyBalanceList = histChangeMonthlyBalanceList;
	}

}
