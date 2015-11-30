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
import com.expanset.common.errors.MultiErrorException;

/**
 * Implementation of {@link Transaction} for local transactions.
 */
public class LocalTransaction implements Transaction {
		
	protected final List<ResourceTransaction> resourceTransactions = new ArrayList<>();
	
	protected final UUID transactionKey = UUID.randomUUID();
	
	protected final Map<Object, Object> properties = new HashMap<>();	
	
	protected final List<Synchronization> listeners = new ArrayList<>();
	
	private int status = Status.STATUS_ACTIVE;
		
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
				for(ResourceTransaction resourceTransaction : resourceTransactions) {
					MultiErrorException multiError = null; 
					try {
						resourceTransaction.commit();
					} catch (Exception e) {
						if(multiError == null) {
							multiError = new MultiErrorException();
						}
						multiError.addError(e);
					}
					if(multiError != null) {
						if(multiError.getErrors().size() > 1) {
							throw multiError;
						} else {
							throw multiError.getErrors().get(0);
						}
					}
				}					
				status = Status.STATUS_COMMITTED;
			} catch (IllegalStateException | SystemException e) {
				status = Status.STATUS_UNKNOWN;
				throw e;
			} catch (Exception e) {
				status = Status.STATUS_UNKNOWN;
				throw new IllegalStateException(e);
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
				for(ResourceTransaction resourceTransaction : resourceTransactions) {
					MultiErrorException multiError = null; 
					try {
						resourceTransaction.rollback();
					} catch (Exception e) {
						if(multiError == null) {
							multiError = new MultiErrorException();
						}
						multiError.addError(e);
					}
					if(multiError != null) {
						if(multiError.getErrors().size() > 1) {
							throw multiError;
						} else {
							throw multiError.getErrors().get(0);
						}
					}
				}					
				status = Status.STATUS_ROLLEDBACK;
			} catch (IllegalStateException | SystemException e) {
				status = Status.STATUS_UNKNOWN;
				throw e;
			} catch (Exception e) {
				status = Status.STATUS_UNKNOWN;
				throw new IllegalStateException(e);
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
		for(ResourceTransaction resourceTransaction : resourceTransactions) {
			resourceTransaction.setTimeout(seconds);
		}		
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

	public void suspend() {
		for(ResourceTransaction resourceTransaction : resourceTransactions) {
			resourceTransaction.suspend();
		}		
	}

	public void resume() {
		for(ResourceTransaction resourceTransaction : resourceTransactions) {
			resourceTransaction.resume();
		}		
	}
	
	public void registerResourceTransaction(ResourceTransaction resourceTransaction) {
		resourceTransactions.add(resourceTransaction);
	}
}
