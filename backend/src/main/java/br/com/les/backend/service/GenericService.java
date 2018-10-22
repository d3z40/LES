package br.com.les.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.les.backend.dao.GenericDAO;
import br.com.les.backend.entity.DomainEntity;

@Component
public class GenericService<T extends DomainEntity> implements IService<T> {

	@Autowired protected GenericDAO<T> genericDAO;

	@Override
	public T save(T clazz) {
		return genericDAO.save(clazz);
	}

	@Override
	public T update(T clazz) {
		return genericDAO.update(clazz);
	}
	
	@Override
	public List<T> find(T entity) {
		return genericDAO.find(entity);
	}	

	@Override
	public void delete(T entity) {
		genericDAO.delete(entity);
	}

	
}