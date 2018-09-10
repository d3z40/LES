package br.com.les.backend.strategy.employee;

import br.com.les.backend.entity.DomainEntity;
import br.com.les.backend.utils.Result;

public interface IEmployeeStrategy {

	Result execute( DomainEntity entity, String callerMethod );
}