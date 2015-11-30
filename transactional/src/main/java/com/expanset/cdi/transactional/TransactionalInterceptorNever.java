package com.expanset.cdi.transactional;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.InvalidTransactionException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.Transactional;
import javax.transaction.TransactionalException;

@Interceptor
@Transactional(Transactional.TxType.NEVER)
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 100)
public class TransactionalInterceptorNever extends AbstractTransactionalInterceptor {

    @AroundInvoke
    public Object intercept(InvocationContext context) 
    		throws Exception {
        final TransactionManager transactionManager = getTransactionManager();
        final Transaction transaction = transactionManager.getTransaction();
        if (transaction != null) {
            throw new TransactionalException("Transaction exists", new InvalidTransactionException());
        }
        
        return context.proceed();
    }
}
