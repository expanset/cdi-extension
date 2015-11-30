package com.expanset.cdi.jpa;

import java.util.Map;

import javax.persistence.Cache;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;

/**
 * Implementation if {@link EntityManagerFactory}, that delegates all methods to current {@link EntityManagerFactory}. 
 */
public class EntityManagerFactoryDelegated implements EntityManagerFactory {
	
	private final EntityManagerFactoryProvider entityManagerFactoryProvider;
	
	private final String unitName;
	
	public EntityManagerFactoryDelegated(EntityManagerFactoryProvider entityManagerFactoryProvider, String unitName) {
		this.entityManagerFactoryProvider = entityManagerFactoryProvider;
		this.unitName = unitName;
	}

	@Override
	public EntityManager createEntityManager() {
		final EntityManagerFactory f = entityManagerFactoryProvider.get(unitName);
		return f.createEntityManager();
	}

	@Override
	public EntityManager createEntityManager(@SuppressWarnings("rawtypes") Map map) {
		final EntityManagerFactory f = entityManagerFactoryProvider.get(unitName);
		return f.createEntityManager(map);
	}

	@Override
	public EntityManager createEntityManager(SynchronizationType synchronizationType) {
		final EntityManagerFactory f = entityManagerFactoryProvider.get(unitName);
		return f.createEntityManager(synchronizationType);
	}

	@Override
	public EntityManager createEntityManager(SynchronizationType synchronizationType, @SuppressWarnings("rawtypes") Map map) {
		final EntityManagerFactory f = entityManagerFactoryProvider.get(unitName);
		return f.createEntityManager(synchronizationType, map);
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		final EntityManagerFactory f = entityManagerFactoryProvider.get(unitName);
		return f.getCriteriaBuilder();
	}

	@Override
	public Metamodel getMetamodel() {
		final EntityManagerFactory f = entityManagerFactoryProvider.get(unitName);
		return f.getMetamodel();
	}

	@Override
	public boolean isOpen() {
		final EntityManagerFactory f = entityManagerFactoryProvider.get(unitName);
		return f.isOpen();
	}

	@Override
	public void close() {
		final EntityManagerFactory f = entityManagerFactoryProvider.get(unitName);
		f.close();
	}

	@Override
	public Map<String, Object> getProperties() {
		final EntityManagerFactory f = entityManagerFactoryProvider.get(unitName);
		return f.getProperties();
	}

	@Override
	public Cache getCache() {
		final EntityManagerFactory f = entityManagerFactoryProvider.get(unitName);
		return f.getCache();
	}

	@Override
	public PersistenceUnitUtil getPersistenceUnitUtil() {
		final EntityManagerFactory f = entityManagerFactoryProvider.get(unitName);
		return f.getPersistenceUnitUtil();
	}

	@Override
	public void addNamedQuery(String name, Query query) {
		final EntityManagerFactory f = entityManagerFactoryProvider.get(unitName);
		f.addNamedQuery(name, query);
	}

	@Override
	public <T> T unwrap(Class<T> cls) {
		final EntityManagerFactory f = entityManagerFactoryProvider.get(unitName);
		return f.unwrap(cls);
	}

	@Override
	public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
		final EntityManagerFactory f = entityManagerFactoryProvider.get(unitName);
		f.addNamedEntityGraph(graphName, entityGraph);
	}
}
