package com.expanset.cdi.jpa;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceProperty;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.SynchronizationType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;

public class EntityManagerDelegated implements EntityManager {
	
	private final EntityManagerProvider entityManagerProvider;
	
	private final String unitName;
	
	private final SynchronizationType synchronization;
	
	private final PersistenceProperty[] properties;
	
	public EntityManagerDelegated(
			EntityManagerProvider entityManagerProvider, 
			String unitName, 
			SynchronizationType synchronization, 
			PersistenceProperty[] properties) {
		this.entityManagerProvider = entityManagerProvider;
		this.unitName = unitName;
		this.synchronization = synchronization;
		this.properties = properties;
	}	
	

	@Override
	public void persist(Object entity) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		em.persist(entity);
	}

	@Override
	public <T> T merge(T entity) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.merge(entity);
	}

	@Override
	public void remove(Object entity) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		em.remove(entity);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.find(entityClass, primaryKey);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  this.properties);
		return em.find(entityClass, primaryKey, properties);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.find(entityClass, primaryKey, lockMode);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  this.properties);
		return em.find(entityClass, primaryKey, lockMode, properties);
	}

	@Override
	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.getReference(entityClass, primaryKey);
	}

	@Override
	public void flush() {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		em.flush();
	}

	@Override
	public void setFlushMode(FlushModeType flushMode) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		em.setFlushMode(flushMode);
	}

	@Override
	public FlushModeType getFlushMode() {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.getFlushMode();
	}

	@Override
	public void lock(Object entity, LockModeType lockMode) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		em.lock(entity, lockMode);
	}

	@Override
	public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  this.properties);
		em.lock(entity, lockMode, properties);
	}

	@Override
	public void refresh(Object entity) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		em.refresh(entity);
	}

	@Override
	public void refresh(Object entity, Map<String, Object> properties) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  this.properties);
		em.refresh(entity, properties);
	}

	@Override
	public void refresh(Object entity, LockModeType lockMode) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		em.refresh(entity, lockMode);
	}

	@Override
	public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  this.properties);
		em.refresh(entity, lockMode, properties);			
	}

	@Override
	public void clear() {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		em.clear();
	}

	@Override
	public void detach(Object entity) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		em.detach(entity);
	}

	@Override
	public boolean contains(Object entity) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.contains(entity);
	}

	@Override
	public LockModeType getLockMode(Object entity) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.getLockMode(entity);
	}

	@Override
	public void setProperty(String propertyName, Object value) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		em.setProperty(propertyName, value);
	}

	@Override
	public Map<String, Object> getProperties() {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.getProperties();
	}

	@Override
	public Query createQuery(String qlString) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.createQuery(qlString);
	}

	@Override
	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.createQuery(criteriaQuery);
	}

	@Override
	public Query createQuery(@SuppressWarnings("rawtypes") CriteriaUpdate updateQuery) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.createQuery(updateQuery);
	}

	@Override
	public Query createQuery(@SuppressWarnings("rawtypes") CriteriaDelete deleteQuery) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.createQuery(deleteQuery);
	}

	@Override
	public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.createQuery(qlString, resultClass);
	}

	@Override
	public Query createNamedQuery(String name) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.createNamedQuery(name);
	}

	@Override
	public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.createNamedQuery(name, resultClass);
	}

	@Override
	public Query createNativeQuery(String sqlString) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.createNamedQuery(sqlString);
	}

	@Override
	public Query createNativeQuery(String sqlString, @SuppressWarnings("rawtypes") Class resultClass) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.createNativeQuery(sqlString, resultClass);
	}

	@Override
	public Query createNativeQuery(String sqlString, String resultSetMapping) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.createNativeQuery(sqlString, resultSetMapping);
	}

	@Override
	public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.createNamedStoredProcedureQuery(name);
	}

	@Override
	public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.createStoredProcedureQuery(procedureName);
	}

	@Override
	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, @SuppressWarnings("rawtypes") Class... resultClasses) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.createStoredProcedureQuery(procedureName, resultClasses);
	}

	@Override
	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.createStoredProcedureQuery(procedureName, resultSetMappings);
	}

	@Override
	public void joinTransaction() {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		em.joinTransaction();
	}

	@Override
	public boolean isJoinedToTransaction() {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.isJoinedToTransaction();
	}

	@Override
	public <T> T unwrap(Class<T> cls) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.unwrap(cls);
	}

	@Override
	public Object getDelegate() {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.getDelegate();
	}

	@Override
	public void close() {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		em.close();
	}

	@Override
	public boolean isOpen() {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.isOpen();
	}

	@Override
	public EntityTransaction getTransaction() {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.getTransaction();
	}

	@Override
	public EntityManagerFactory getEntityManagerFactory() {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.getEntityManagerFactory();
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.getCriteriaBuilder();
	}

	@Override
	public Metamodel getMetamodel() {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.getMetamodel();
	}

	@Override
	public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.createEntityGraph(rootType);
	}

	@Override
	public EntityGraph<?> createEntityGraph(String graphName) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.createEntityGraph(graphName);
	}

	@Override
	public EntityGraph<?> getEntityGraph(String graphName) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.getEntityGraph(graphName);					
	}

	@Override
	public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
		final EntityManager em = entityManagerProvider.get(unitName,  synchronization,  properties);
		return em.getEntityGraphs(entityClass);
	}
}
