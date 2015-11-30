package com.expanset.cdi.transactional;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.PassivationCapable;
import javax.inject.Inject;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;

import javax.transaction.TransactionScoped;

public class TransactionContext implements Context {

	@Inject
    private TransactionManager transactionManager;

	@Inject
	private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    private Map<Transaction, BeansHolder<?>> transactions = new HashMap<Transaction, BeansHolder<?>>();

    @Override
    public Class<? extends Annotation> getScope() {
        return TransactionScoped.class;
    }

	@Override
    @SuppressWarnings("unchecked")
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        if (!isActive()) {
            throw new ContextNotActiveException();
        }

        final PassivationCapable passivationCapableType = (PassivationCapable) contextual;
        final Object existingBean = transactionSynchronizationRegistry.getResource(passivationCapableType.getId());
        if (existingBean != null) {
            return (T) existingBean;
        } else if (creationalContext != null) {
        	final Transaction currentTransaction = getCurrentTransaction();
        	final T newBean = contextual.create(creationalContext);
            transactionSynchronizationRegistry.putResource(passivationCapableType.getId(), newBean);

            synchronized (transactions) {
            	BeansHolder<T> beanHolder = (BeansHolder<T>) transactions.get(currentTransaction);
                if (beanHolder == null) {
                    beanHolder = new BeansHolder<T>(this, currentTransaction);
                    transactions.put(currentTransaction, beanHolder);
                }
                beanHolder.registerBean(contextual, creationalContext, newBean);
            }
            return newBean;
        } else {
            return null;
        }
    }

    @Override
    public <T> T get(Contextual<T> contextual) {
        return get(contextual, null);
    }

    @Override
    public boolean isActive() {
        final Transaction transaction = getCurrentTransaction();
        if (transaction == null) {
            return false;
        }

        try {
            final int currentStatus = transaction.getStatus();
            return currentStatus != Status.STATUS_NO_TRANSACTION
                    && currentStatus != Status.STATUS_COMMITTED
                    && currentStatus != Status.STATUS_ROLLEDBACK;
        } catch (SystemException e) {
            throw new RuntimeException(e);
        }
    }

    void cleanupScope(Transaction transaction) {
        synchronized (transactions) {
            transactions.remove(transaction);
        }
    }
    
    private Transaction getCurrentTransaction() {
    	try {
			return transactionManager.getTransaction();
		} catch (SystemException e) {
			throw new RuntimeException(e);
		}
    }
    
    private static class BeansHolder<T> implements Synchronization {
    	
        private Set<BeanHolder> beans;
        
        private TransactionContext context;
        
        private Transaction transaction;

        public BeansHolder(TransactionContext context, Transaction transaction) {
            this.context = context;
            this.transaction = transaction;
            this.beans = new CopyOnWriteArraySet<BeanHolder>();

            try {
                transaction.registerSynchronization(this);
            } catch (RollbackException e) {
                throw new RuntimeException(e);
            } catch (SystemException e) {
                throw new RuntimeException(e);
            }
        }

        public void registerBean(Contextual<T> contextual, CreationalContext<T> creationalContext, T bean) {
            beans.add(new BeanHolder(contextual, creationalContext, bean));
        }

        @Override
        public void beforeCompletion() {
        }

        @Override
        public void afterCompletion(int i) {
            for (BeanHolder beanHolder : beans) {
                beanHolder.destroy();
            }

            context.cleanupScope(transaction);
        }

        private class BeanHolder {
        	
        	private Contextual<T> contextual;
            
        	private CreationalContext<T> creationalContext;
            
        	private T bean;

            private BeanHolder(Contextual<T> contextual, CreationalContext<T> creationalContext, T bean) {
                this.contextual = contextual;
                this.creationalContext = creationalContext;
                this.bean = bean;
            }

            public void destroy() {
                contextual.destroy(bean, creationalContext);
            }
        }
    }
}
