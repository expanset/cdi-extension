package com.expanset.cdi.jpa;

import java.text.MessageFormat;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.jboss.weld.injection.ParameterInjectionPoint;
import org.jboss.weld.injection.spi.JpaInjectionServices;
import org.jboss.weld.injection.spi.ResourceReference;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;
import org.jboss.weld.injection.spi.helpers.SimpleResourceReference;

/**
 * {@link JpaInjectionServices} implementation.
 */
public class CdiJpaInjectionServices implements JpaInjectionServices {
	
	private volatile EntityManagerProvider entityManagerProvider;
	
	private volatile EntityManagerFactoryProvider entityManagerFactoryProvider;

	@Override
	public void cleanup() {
		entityManagerProvider = null;
		entityManagerFactoryProvider = null;
	}

	@Override
	public ResourceReferenceFactory<EntityManager> registerPersistenceContextInjectionPoint(InjectionPoint injectionPoint) {
        final PersistenceContext context = getResourceAnnotated(injectionPoint).getAnnotation(PersistenceContext.class);
        if (context == null) {
        	throw new RuntimeException(MessageFormat.format("Annotation \"{0}\" is not found in \"{1}\"", 
        			PersistenceContext.class, injectionPoint.getMember()));
        }
        
        return new EntityManagerResourceReferenceFactory(context);
	}

	@Override
	public ResourceReferenceFactory<EntityManagerFactory> registerPersistenceUnitInjectionPoint(InjectionPoint injectionPoint) {
        final PersistenceUnit unit = getResourceAnnotated(injectionPoint).getAnnotation(PersistenceUnit.class);
        if (unit == null) {
        	throw new RuntimeException(MessageFormat.format("Annotation \"{0}\" is not found in \"{1}\"", 
        			PersistenceUnit.class, injectionPoint.getMember()));
        }
                
        return new EntityManagerFactoryResourceReferenceFactory<EntityManagerFactory>(unit);
	}

	@Override
	public EntityManager resolvePersistenceContext(InjectionPoint injectionPoint) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityManagerFactory resolvePersistenceUnit(InjectionPoint injectionPoint) {
		throw new UnsupportedOperationException();
	}

	private EntityManagerProvider getEntityManagerProvider() {
		if(entityManagerProvider == null) {
			entityManagerProvider = CDI.current().select(EntityManagerProvider.class).get();
		}
		return entityManagerProvider;
	}
	
	private EntityManagerFactoryProvider getEntityManagerFactoryProvider() {
		if(entityManagerFactoryProvider == null) {
			entityManagerFactoryProvider = CDI.current().select(EntityManagerFactoryProvider.class).get();
		}
		return entityManagerFactoryProvider;
	}	
	
	private static Annotated getResourceAnnotated(InjectionPoint injectionPoint) {
        if(injectionPoint instanceof ParameterInjectionPoint) {
            return ((ParameterInjectionPoint<?, ?>)injectionPoint).getAnnotated().getDeclaringCallable();
        }
        return injectionPoint.getAnnotated();
    }

    private class EntityManagerResourceReferenceFactory implements ResourceReferenceFactory<EntityManager> {
        
        private final PersistenceContext context;

        public EntityManagerResourceReferenceFactory(PersistenceContext context) {
        	this.context = context;
		}

		@Override
        public ResourceReference<EntityManager> createResource() {
            final EntityManagerDelegated result = new EntityManagerDelegated(
            		getEntityManagerProvider(), context.unitName(), context.synchronization(), context.properties());
            return new SimpleResourceReference<EntityManager>(result);
        }
    }   
    
    private class EntityManagerFactoryResourceReferenceFactory<T> implements ResourceReferenceFactory<EntityManagerFactory> {
    	
    	private final PersistenceUnit unit;

        public EntityManagerFactoryResourceReferenceFactory(PersistenceUnit unit) {
            this.unit =  unit;
        }

        @Override
        public ResourceReference<EntityManagerFactory> createResource() {
            final EntityManagerFactoryDelegated result = new EntityManagerFactoryDelegated(
            		getEntityManagerFactoryProvider(), unit.unitName());
            return new SimpleResourceReference<EntityManagerFactory>(result);
        }
    }   
}
