package com.expanset.cdi.transactional;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.Transactional;

public class AbstractTransactionalInterceptor {

    @Inject
    private BeanManager beanManager;

    @Inject
    private TransactionManager transactionManager;
    
	protected TransactionManager getTransactionManager() {
		return transactionManager;
	}

    protected Object proceedInNewTransaction(InvocationContext context) 
    		throws Exception {
    	transactionManager.begin();
    	final Transaction transaction = transactionManager.getTransaction();
        try {
            return proceedInExistingTransaction(context, transaction);
        } finally {
            endTransaction(transaction);
        }
    }

    protected Object proceedInExistingTransaction(InvocationContext context, Transaction transaction) 
    		throws Exception {
        try {
            return context.proceed();
        } catch (Exception e) {
            handleException(context, transaction, e);
            
            throw e;
        }
    }

    protected void handleException(InvocationContext context, Transaction transaction, Exception e) 
    		throws Exception {
        final Transactional transactional = getTransactional(context);

        for (Class<?> dontRollbackOnClass : transactional.dontRollbackOn()) {
            if (dontRollbackOnClass.isAssignableFrom(e.getClass())) {
                throw e;
            }
        }

        for (Class<?> rollbackOnClass : transactional.rollbackOn()) {
            if (rollbackOnClass.isAssignableFrom(e.getClass())) {
            	transaction.setRollbackOnly();
                throw e;
            }
        }

        if (transactional.rollbackOn().length == 0) {
        	transaction.setRollbackOnly();
        }

        throw e;
    }

    protected void endTransaction(Transaction transaction) 
    		throws Exception {
        if (transaction.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
        	transaction.rollback();
        } else {
        	transaction.commit();
        }
    }
    
    private Transactional getTransactional(InvocationContext context) {
        Transactional transactional = context.getMethod().getAnnotation(Transactional.class);
        if (transactional != null) {
            return transactional;
        }

        final Class<?> targetClass = context.getTarget().getClass();
        transactional = targetClass.getAnnotation(Transactional.class);
        if (transactional != null) {
            return transactional;
        }

        for (Annotation annotation : context.getMethod().getDeclaringClass().getAnnotations()) {
            if (beanManager.isStereotype(annotation.annotationType())) {
                for (Annotation stereotyped : beanManager.getStereotypeDefinition(annotation.annotationType())) {
                    if (stereotyped.annotationType().equals(Transactional.class)) {
                        return (Transactional) stereotyped;
                    }
                }
            }
        }

        throw new RuntimeException("Transactional annotation not found");
    }   
}
