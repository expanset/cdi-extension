package com.expanset.cdi.transactional;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.Transactional;

@Interceptor
@Transactional(Transactional.TxType.REQUIRED)
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 100)
public class TransactionalInterceptorRequired extends AbstractTransactionalInterceptor {
	
    @AroundInvoke
    public Object intercept(InvocationContext context) 
    		throws Exception {
        final TransactionManager transactionManager = getTransactionManager();
        final Transaction transaction = transactionManager.getTransaction();
        if (transaction == null) {
            return proceedInNewTransaction(context);
        } else {
            return proceedInExistingTransaction(context, transaction);
        }
    }
}
