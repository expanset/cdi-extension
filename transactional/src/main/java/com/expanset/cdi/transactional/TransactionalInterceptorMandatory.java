package com.expanset.cdi.transactional;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionRequiredException;
import javax.transaction.Transactional;
import javax.transaction.TransactionalException;

@Interceptor
@Transactional(Transactional.TxType.MANDATORY)
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 100)
public class TransactionalInterceptorMandatory extends AbstractTransactionalInterceptor {

    @AroundInvoke
    public Object intercept(InvocationContext context) 
    		throws Exception {
        final TransactionManager transactionManager = getTransactionManager();
        final Transaction transaction = transactionManager.getTransaction();
        if (transaction == null) {
            throw new TransactionalException("Transaction is required", new TransactionRequiredException());
        }
        
        return proceedInExistingTransaction(context, transaction);
    }
}