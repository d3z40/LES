package br.com.les.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import br.com.les.backend.entity.Appointment;
import br.com.les.backend.entity.Employee;

public interface AppointmentRepository extends GenericRepository<Appointment>{

	@Query("select t from Appointment t where t.date >= ?1")
	List< Appointment > findByDate(LocalDateTime date);

	@Query("select t from Appointment t where t.calculated = false and t.employee = ?1")
	List<Appointment> findPending(Employee employee);
}
