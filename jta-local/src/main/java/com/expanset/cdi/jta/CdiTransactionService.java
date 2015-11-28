package com.expanset.cdi.jta;

import javax.enterprise.inject.spi.CDI;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.UserTransaction;

import org.jboss.weld.transaction.spi.TransactionServices;

// TODO Extract to other jar. Remove weld dependency.

public class CdiTransactionService implements TransactionServices {
	
	private volatile LocalTransactionManager transactionManager;

	@Override
	public void cleanup() {
		transactionManager = null;
	}

	@Override
	public void registerSynchronization(Synchronization synchronizedObserver) {
		Transaction transaction;
		try {
			transaction = getTransactionManager().getTransaction();
			if(transaction != null) {
				transaction.registerSynchronization(synchronizedObserver);
			}
		} catch (SystemException | IllegalStateException | RollbackException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isTransactionActive() {
		try {
			return getTransactionManager().getStatus() == Status.STATUS_ACTIVE;
		} catch (SystemException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public UserTransaction getUserTransaction() {
		return getTransactionManager();
	}
	
	private LocalTransactionManager getTransactionManager() {
		if(transactionManager == null) {
			transactionManager = CDI.current().select(LocalTransactionManager.class).get();
		}
	
		return transactionManager;
	}
}
