package com.expanset.cdi.jpa;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceProperty;
import javax.persistence.SynchronizationType;
import javax.transaction.Status;
import javax.transaction.Synchronization;import javax.transaction.SystemException;
import javax.transaction.TransactionSynchronizationRegistry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expanset.cdi.jta.LocalTransactionService;
import com.expanset.cdi.jta.spi.ResourceTransaction;

/**
 * Access to the current {@link EntityManager}, which is bounded to the current transaction.
 */
@Dependent
public class EntityManagerProvider {
	
	@Inject
	@SuppressWarnings("cdi-ambiguous-dependency")
	private EntityManagerFactoryProvider entityManagerFactoryProvider;
	
	@Inject
	@SuppressWarnings("cdi-ambiguous-dependency")
	private TransactionSynchronizationRegistry transactionSynchronizationRegistry; 
	
	@Inject
	private Instance<LocalTransactionService> locatTransactionService;
	
	private static Logger LOG = LoggerFactory.getLogger(EntityManagerProvider.class);

	/**
	 * @param unitName Unit name of {@link EntityManager}.
	 * @param synchronization Type of synchronization.
	 * @param properties Properties in annotation.
	 * @return Get or creates {@link EntityManager}, which is bounded to the current transaction.
	 */
	public EntityManager get(String unitName, SynchronizationType synchronization, PersistenceProperty[] properties) {
		if(transactionSynchronizationRegistry.getTransactionStatus() != Status.STATUS_ACTIVE) {
			throw new RuntimeException("No active transaction");
		}
		
		final EntityManagerKey key = new EntityManagerKey(unitName, synchronization, properties);
		EntityManager entityManager = (EntityManager)transactionSynchronizationRegistry.getResource(key);
		if(entityManager == null) {
			LOG.trace("Create new EntityManager, unit name: {}", unitName);
			
			final EntityManagerFactory factory = entityManagerFactoryProvider.get(unitName);
			if(factory == null) {
				throw new RuntimeException(
						MessageFormat.format("Entity manager factory not found for \"{0}\" init", unitName));
			}
			
			if(properties == null) {
				entityManager = factory.createEntityManager(synchronization);
			} else {
				entityManager = factory.createEntityManager(synchronization, 
						Arrays.stream(properties).collect(Collectors.toMap(PersistenceProperty::name, PersistenceProperty::value)) );
			}
			
			transactionSynchronizationRegistry.putResource(key, entityManager);
			transactionSynchronizationRegistry.registerInterposedSynchronization(new EntityManagerCleanup(entityManager));
			if(synchronization == SynchronizationType.SYNCHRONIZED) {
				LOG.trace("Start transaction for EntityManager, unit name: {}", unitName);
				
				joinTransaction(entityManager);
			}
		}

		return entityManager;
	}
	
	/**
	 * Join {@link EntityManager} to the current transaction.
	 * @param entityManager {@link EntityManager} to join to the current transaction.
	 */
	public void joinTransaction(EntityManager entityManager) {
		if(locatTransactionService.isUnsatisfied()) {
			entityManager.joinTransaction();
		} else if(!entityManager.isJoinedToTransaction()) {
			locatTransactionService.get().registerResourceTransaction(
					new EntityManagerTransactionWrapper(entityManager.getTransaction()));
		}
	}
		
	private static class EntityManagerKey {

		private final String unitName;
		
		private final SynchronizationType synchronization;
		
		private final PersistenceProperty[] properties;
		
		public EntityManagerKey(String unitName, SynchronizationType synchronization, PersistenceProperty[] properties) {
			this.unitName = unitName;
			this.synchronization = synchronization;
			this.properties = properties;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(properties);
			result = prime * result + ((synchronization == null) ? 0 : synchronization.hashCode());
			result = prime * result + ((unitName == null) ? 0 : unitName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final EntityManagerKey other = (EntityManagerKey) obj;
			if (!Arrays.equals(properties, other.properties)) {
				return false;
			}
			if (synchronization != other.synchronization) {
				return false;
			}
			if (!StringUtils.equals(unitName, other.unitName)) {
				return false;
			}
			return true;
		}
	}
	
	private class EntityManagerCleanup implements Synchronization {
		
		private final EntityManager entityManager;
		
		public EntityManagerCleanup(EntityManager entityManager) {
			this.entityManager = entityManager;
		}

		@Override
		public void beforeCompletion() {
		}

		@Override
		public void afterCompletion(int status) {	
			try {
				entityManager.close();
			} catch (Exception e) {
				LOG.error("Error when closing EntityManager", e);
			}
		}
	}
	
	private static class EntityManagerTransactionWrapper implements ResourceTransaction {

		private EntityTransaction transaction;
		
		public EntityManagerTransactionWrapper(EntityTransaction transaction) {
			this.transaction = transaction;
			transaction.begin();
		}

		@Override
		public void commit() 
				throws IllegalStateException, SystemException {
			transaction.commit();
		}

		@Override
		public void rollback() 
				throws IllegalStateException, SystemException {
			transaction.rollback();
		}

		@Override
		public void suspend() {
		}

		@Override
		public void resume() {
		}

		@Override
		public void setTimeout(int seconds) {
		}
	}
}
