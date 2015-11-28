package com.expanset.cdi.jta;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.TransactionSynchronizationRegistry;

/**
 * Implementation of {@link TransactionSynchronizationRegistry} for local transactions.
 */
@ApplicationScoped
public class LocalTransactionSynchronizationRegistry implements TransactionSynchronizationRegistry {

	@Inject
	private LocalTransactionManager localTransactionManager;
	
	@Override
	public Object getTransactionKey() {
		return localTransactionManager.getTransactionKey();
	}

	@Override
	public void putResource(Object key, Object value) {
		localTransactionManager.putResource(key, value);
	}

	@Override
	public Object getResource(Object key) {
		return localTransactionManager.getResource(key);
	}

	@Override
	public void registerInterposedSynchronization(Synchronization sync) {
		localTransactionManager.registerSynchronization(sync);
	}

	@Override
	public int getTransactionStatus() {
		try {
			return localTransactionManager.getStatus();
		} catch (SystemException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setRollbackOnly() {
		try {
			localTransactionManager.setRollbackOnly();
		} catch (IllegalStateException | SystemException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean getRollbackOnly() {
		return localTransactionManager.getRollbackOnly();
	}
}
