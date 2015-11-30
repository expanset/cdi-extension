package com.expanset.cdi.transactional;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.Transactional;

@Interceptor
@Transactional(Transactional.TxType.NOT_SUPPORTED)
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 100)
public class TransactionalInterceptorNotSupported extends AbstractTransactionalInterceptor {
	
    @AroundInvoke
    public Object intercept(InvocationContext context) 
    		throws Exception {
        final TransactionManager transactionManager = getTransactionManager();
        Transaction transaction = transactionManager.getTransaction();
        if (transaction != null) {
        	transaction = transactionManager.suspend();
            try {
                return context.proceed();
            } finally {
                transactionManager.resume(transaction);
            }
        } else {
        	return context.proceed();
        }
    }
}
