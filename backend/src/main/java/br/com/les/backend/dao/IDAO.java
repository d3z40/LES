package br.com.les.backend.dao;

import java.util.List;

import br.com.les.backend.entity.DomainEntity;

public interface IDAO<T extends DomainEntity> {

	T save(T aEntity);

	T update(T aEntity);

	T findById(Long id, Class<? extends T> clazz);

	List<T> findAll(T entity);	
}
