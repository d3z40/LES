package br.com.les.backend.strategy.employee;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import br.com.les.backend.entity.Employee;
import br.com.les.backend.navigator.INavigationCase;
import br.com.les.backend.navigator.IStrategy;
import br.com.les.backend.repository.EmployeeRepository;

@Configuration
public class IdInactivate implements IStrategy<Employee> {
	
	@Autowired private EmployeeRepository employeeRepository;

	@Override
	public void process(Employee aEntity, INavigationCase<Employee> aCase) {

		if (aEntity != null && aEntity.getId() != null) {
		
			Optional<Employee> employee = employeeRepository.findById(aEntity.getId());
			if(employee.isPresent() && !employee.filter(e -> !e.getActive()).isPresent()) {
				aCase.getResult().setError("Colaborador já está inativo!");
			}
			return;
		}
		aCase.suspendExecution();
		aCase.getResult().setError("Colaborador inexistente!");
		return;
	}
}
