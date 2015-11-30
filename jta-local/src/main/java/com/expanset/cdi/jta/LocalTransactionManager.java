package com.expanset.cdi.jta;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.expanset.cdi.jta.spi.ResourceTransaction;

/**
 * Implementation of {@link TransactionManager} for local transactions.
 */
@ApplicationScoped
public class LocalTransactionManager implements TransactionManager, UserTransaction {
	
	protected ThreadLocal<LocalTransaction> currentTransactionHolder = new ThreadLocal<LocalTransaction>();

	@Override
	public void begin() 
			throws NotSupportedException, SystemException {
		if(currentTransactionHolder.get() != null) {
			return;
		}	
		currentTransactionHolder.set(new LocalTransaction());
	}

	@Override
	public void commit() 
			throws 
				RollbackException, 
				HeuristicMixedException, 
				HeuristicRollbackException,
				SecurityException, 
				IllegalStateException, 
				SystemException {
		final LocalTransaction transaction = currentTransactionHolder.get();
		if(transaction != null) {
			try {
				transaction.commit();
			} finally {
				currentTransactionHolder.remove();
			}
		}
	}

	@Override
	public int getStatus() 
			throws SystemException {
		final LocalTransaction transaction = currentTransactionHolder.get();
		if(transaction != null) {
			return transaction.getStatus();
		}
		return Status.STATUS_NO_TRANSACTION;
	}

	@Override
	public Transaction getTransaction() 
			throws SystemException {
		return currentTransactionHolder.get();
	}

	@Override
	public void resume(Transaction tobj) 
			throws InvalidTransactionException, IllegalStateException, SystemException {
		if(!(tobj instanceof LocalTransaction)) {
			throw new InvalidTransactionException();
		}
		final LocalTransaction localTransaction = (LocalTransaction)tobj;
		localTransaction.resume();
		currentTransactionHolder.set(localTransaction);
	}

	@Override
	public void rollback() 
			throws IllegalStateException, SecurityException, SystemException {
		final LocalTransaction transaction = currentTransactionHolder.get();
		if(transaction != null) {
			try {
				transaction.rollback();
			} finally {
				currentTransactionHolder.remove();
			}
		}
	}

	@Override
	public void setRollbackOnly() 
			throws IllegalStateException, SystemException {
		final LocalTransaction transaction = currentTransactionHolder.get();
		if(transaction != null) {
			transaction.setRollbackOnly();
		}
	}

	@Override
	public void setTransactionTimeout(int seconds) 
			throws SystemException {
		final LocalTransaction transaction = currentTransactionHolder.get();
		if(transaction != null) {
			transaction.setTransactionTimeout(seconds);
		}
	}

	@Override
	public Transaction suspend() 
			throws SystemException {
		final LocalTransaction transaction = currentTransactionHolder.get();
		if(transaction != null) {
			transaction.suspend();
			currentTransactionHolder.set(null);
			return transaction;
		}		
		return null;
	}

	public Object getTransactionKey() {
		final LocalTransaction transaction = currentTransactionHolder.get();
		if(transaction != null) {
			return transaction.getTransactionKey();
		}
		return null;
	}

	public void putResource(Object key, Object value) {
		final LocalTransaction transaction = currentTransactionHolder.get();
		if(transaction != null) {
			transaction.putResource(key, value);
		} else {
			throw new RuntimeException("No active transaction");
		}
	}

	public Object getResource(Object key) {
		final LocalTransaction transaction = currentTransactionHolder.get();
		if(transaction != null) {
			return transaction.getResource(key);
		}
		return null;
	}

	public void registerSynchronization(Synchronization sync) {
		final LocalTransaction transaction = currentTransactionHolder.get();
		if(transaction != null) {
			try {
				transaction.registerSynchronization(sync);
			} catch (IllegalStateException | RollbackException | SystemException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public boolean getRollbackOnly() {
		final LocalTransaction transaction = currentTransactionHolder.get();
		if(transaction != null) {
			return transaction.getRollbackOnly();
		}
		return false;
	}

	public void registerResourceTransaction(ResourceTransaction resourceTransaction) {
		final LocalTransaction transaction = currentTransactionHolder.get();
		if(transaction == null) {
			throw new RuntimeException("Active transaction is not exists");
		}	
		
		transaction.registerResourceTransaction(resourceTransaction);		
	}
}
