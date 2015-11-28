package com.expanset.cdi.jta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

import com.expanset.cdi.jta.spi.ResourceTransaction;

/**
 * Implementation of {@link Transaction} for local transactions.
 */
public class LocalTransaction implements Transaction {
		
	protected final ResourceTransaction resourceTransaction;
	
	protected final UUID transactionKey = UUID.randomUUID();
	
	protected final Map<Object, Object> properties = new HashMap<>();	
	
	protected final List<Synchronization> listeners = new ArrayList<>();
	
	private int status = Status.STATUS_ACTIVE;
	
	public LocalTransaction(ResourceTransaction resourceTransaction) {
		this.resourceTransaction = resourceTransaction;
	}
	
	public ResourceTransaction getResourceTransaction() {
		return resourceTransaction;
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
		if(status == Status.STATUS_MARKED_ROLLBACK || status == Status.STATUS_ROLLEDBACK) {
			throw new RollbackException();
		} else if(status != Status.STATUS_ACTIVE) {
			throw new IllegalStateException();
		} else {
			for(Synchronization sync : listeners) {
				sync.beforeCompletion();
			}
			
			status = Status.STATUS_COMMITTING;
			try {				
				resourceTransaction.commit();
				status = Status.STATUS_COMMITTED;
			} catch (Throwable e) {
				status = Status.STATUS_UNKNOWN;
				throw e;
			} finally {
				for(Synchronization sync : listeners) {
					sync.afterCompletion(status);
				}								
			}
		}
	}

	@Override
	public boolean delistResource(XAResource xaRes, int flag) 
			throws IllegalStateException, SystemException {
		throw new IllegalStateException("Not supported");
	}

	@Override
	public boolean enlistResource(XAResource xaRes) 
			throws RollbackException, IllegalStateException, SystemException {
		throw new IllegalStateException("Not supported");
	}

	@Override
	public int getStatus() 
			throws SystemException {
		return status;
	}

	@Override
	public void registerSynchronization(Synchronization sync)
			throws RollbackException, IllegalStateException, SystemException {
		listeners.add(sync);
	}

	@Override
	public void rollback() 
			throws IllegalStateException, SystemException {
		if(status != Status.STATUS_ACTIVE && status != Status.STATUS_MARKED_ROLLBACK) {
			throw new IllegalStateException();
		} else {
			status = Status.STATUS_ROLLING_BACK;
			try {
				resourceTransaction.rollback();
				status = Status.STATUS_ROLLEDBACK;
			} catch (Throwable e) {
				status = Status.STATUS_UNKNOWN;
				throw e;
			} finally {
				for(Synchronization sync : listeners) {
					sync.afterCompletion(status);
				}								
			}

		}			
	}

	@Override
	public void setRollbackOnly() 
			throws IllegalStateException, SystemException {
		status = Status.STATUS_MARKED_ROLLBACK;
	}

	public boolean getRollbackOnly() {
		return status == Status.STATUS_MARKED_ROLLBACK;
	}

	public void setTransactionTimeout(int seconds) {
		resourceTransaction.setTimeout(seconds);
	}

	public Object getTransactionKey() {
		return transactionKey;
	}

	public void putResource(Object key, Object value) {
		properties.put(key, value);
	}

	public Object getResource(Object key) {
		return properties.get(key);
	}
}
