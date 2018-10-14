package br.com.les.backend.dao;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import br.com.les.backend.entity.DomainEntity;

@Component
@SuppressWarnings("unchecked")
public class AppointmentDAO extends AbstractDAO {

	private StringBuilder hql;
	private Query query;

	public <T extends DomainEntity> int softDelete(T entity) {

		hql = new StringBuilder();
		
		hql.append( "update Appointment set active = :active where id = :id");
		
		query = getEntityManager().createQuery( hql.toString() );
		
		query.setParameter("active", false );
		query.setParameter("id", entity.getId() );
		
		return query.executeUpdate();
	}
}
