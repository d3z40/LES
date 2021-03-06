package br.com.les.backend.strategy.employee;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.base.Strings;

import br.com.les.backend.entity.DomainEntity;
import br.com.les.backend.entity.Employee;
import br.com.les.backend.entity.Role;
import br.com.les.backend.navigator.INavigationCase;
import br.com.les.backend.navigator.IStrategy;
import br.com.les.backend.repository.RoleRepository;

@Configuration
public class ValidateManagerExistence implements IStrategy<Employee> {

	@Autowired private RoleRepository roleRepository;
	
	@Override
	public void process(Employee aEntity, INavigationCase<Employee> aCase) {

		if (aEntity != null && !Strings.isNullOrEmpty(aEntity.getPis())) {
	
			Optional<Role> r = Optional.empty();
			for ( GrantedAuthority role : aEntity.getUser().getAuthorities() ) {
				r = roleRepository.findActiveById( ( ( DomainEntity ) role ).getId() );
				if(r.get().equals(Role.ROLE_EMPLOYEE))
					break;
				else
					r = Optional.empty();
			}
			if(r.isPresent()) {
				
				if(aEntity.getManager().getId() == null)
					aCase.getResult().setError("Gestor deve ser informado!");
			}
			return;
		}
		aCase.suspendExecution();
		aCase.getResult().setError("Número do PIS não definido!");
		return;
	}
}

