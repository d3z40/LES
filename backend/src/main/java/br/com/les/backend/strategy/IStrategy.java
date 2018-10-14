package br.com.les.backend.strategy;

import br.com.les.backend.entity.DomainEntity;
import br.com.les.backend.utils.Result;

public interface IStrategy<T extends DomainEntity> {

	Result<T> execute(T entity, String action, String callerMethod );

}
