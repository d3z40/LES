package br.com.les.backend.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.springframework.stereotype.Component;

@Component
@Entity
public class BaseHourCalculation extends DomainEntity {

	private Integer workload;
	private LocalDate expirationDate;
	private Double salary;
	private Double overtimePercentage;
	private Double nightOvertimePercentage;
	private Double weekEndOvertimePercentage;

	@OneToOne
	@JoinColumn(name = "hour_type_id")
	private HourType hourType;

	@OneToOne
	@JoinColumn(name = "employee_id")
	private Employee employee;

	
	public Integer getWorkload() {
		return workload;
	}

	public void setWorkload(Integer workload) {
		this.workload = workload;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}

	public HourType getHourType() {
		return hourType;
	}

	public void setHourType(HourType hourType) {
		this.hourType = hourType;
	}

	public LocalDate getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDate expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Double getOvertimePercentage() {
		return overtimePercentage;
	}

	public void setOvertimePercentage(Double overtimePercentage) {
		this.overtimePercentage = overtimePercentage;
	}

	public Double getNightOvertimePercentage() {
		return nightOvertimePercentage;
	}

	public void setNightOvertimePercentage(Double nightOvertimePercentage) {
		this.nightOvertimePercentage = nightOvertimePercentage;
	}

	public Double getWeekEndOvertimePercentage() {
		return weekEndOvertimePercentage;
	}

	public void setWeekEndOvertimePercentage(Double weekEndOvertimePercentage) {
		this.weekEndOvertimePercentage = weekEndOvertimePercentage;
	}
}
