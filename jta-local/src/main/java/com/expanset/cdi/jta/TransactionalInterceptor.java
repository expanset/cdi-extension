package com.expanset.cdi.jta;

import java.lang.reflect.Method;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.InvalidTransactionException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionRequiredException;
import javax.transaction.Transactional;
import javax.transaction.TransactionalException;

/**
 * Interceptor to transactional methods.
 */
@Dependent
@Interceptor
@Transactional
public class TransactionalInterceptor {
	
	@Inject
	protected TransactionManager transactionManager;
	
	@AroundInvoke
	public Object invoke(InvocationContext methodInvocation) 
			throws Throwable {
		final Transactional transactional = 
				readTransactionMetadata(methodInvocation);
		
		Transaction currentTransaction = null;
		Transaction suspendedTransaction = null;
		switch(transactional.value()) {
		case MANDATORY:
			if(transactionManager.getStatus() == Status.STATUS_NO_TRANSACTION) {
				throw new TransactionalException("Transaction required", new TransactionRequiredException());
			}			
			break;
		case NEVER:
			if(transactionManager.getStatus() != Status.STATUS_NO_TRANSACTION) {
				throw new TransactionalException("Cannot run inside transaction", new InvalidTransactionException());
			}			
			break;
		case NOT_SUPPORTED:
			suspendedTransaction = transactionManager.suspend();
			break;
		case REQUIRED:
			if(transactionManager.getStatus() == Status.STATUS_NO_TRANSACTION) {
				transactionManager.begin();
				currentTransaction = transactionManager.getTransaction();
			}
			break;
		case REQUIRES_NEW:
			if(transactionManager.getStatus() != Status.STATUS_NO_TRANSACTION) {
				suspendedTransaction = transactionManager.suspend();
			}
			transactionManager.begin();
			currentTransaction = transactionManager.getTransaction();
			break;
		default:
			break;
		}		
		
		try {			
			return methodInvocation.proceed();			
		} catch(Throwable e) {
			if(isSetRollbackOnly(transactional, e)) {
				transactionManager.setRollbackOnly();
			}			
			throw e;
		} finally {
			try {
				if(currentTransaction != null) {
					if(currentTransaction.getStatus() == Status.STATUS_ACTIVE 
							|| currentTransaction.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
						if(currentTransaction.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
							currentTransaction.rollback();
						} else {
							currentTransaction.commit();
						}
					}
				}
			} finally {
				if(suspendedTransaction != null) {
					transactionManager.resume(suspendedTransaction);
				}			
			}
		}
	}
	
	protected Transactional readTransactionMetadata(InvocationContext methodInvocation) {
		final Method method = methodInvocation.getMethod();
		Transactional transactional = method.getAnnotation(Transactional.class);
		if (transactional == null) {
			final Class<?> targetClass = methodInvocation.getTarget().getClass();
			transactional = targetClass.getAnnotation(Transactional.class);
		}
		  
		return transactional;
	}	
	
	@SuppressWarnings("unchecked")
	protected boolean isSetRollbackOnly(Transactional transactional, Throwable e) 
			throws IllegalStateException, SystemException {		
		for (Class<? extends Throwable> exceptOn : transactional.dontRollbackOn()) {
			if (exceptOn.isInstance(e)) {
				return false;
			}
		}		
		for (Class<? extends Throwable> exceptOn : transactional.rollbackOn()) {
			if (exceptOn.isInstance(e)) {
				return true;
			}
		}
		if(transactional.rollbackOn().length != 0) {
			return false;
		}		
		return true;
	}	
}
