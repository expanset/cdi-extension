package com.expanset.cdi.transactional;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.transaction.TransactionScoped;

public class TransactionalExtension implements Extension {

	public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery event, BeanManager manager) {
		event.addScope(TransactionScoped.class, true, true);
        
        event.addAnnotatedType(manager.createAnnotatedType(TransactionalInterceptorRequired.class));
        event.addAnnotatedType(manager.createAnnotatedType(TransactionalInterceptorRequiresNew.class));
        event.addAnnotatedType(manager.createAnnotatedType(TransactionalInterceptorMandatory.class));
        event.addAnnotatedType(manager.createAnnotatedType(TransactionalInterceptorSupports.class));  
        event.addAnnotatedType(manager.createAnnotatedType(TransactionalInterceptorNotSupported.class));
        event.addAnnotatedType(manager.createAnnotatedType(TransactionalInterceptorNever.class));
    }
	
    public void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager manager) {
        event.addContext(new TransactionContext());
    }
}
