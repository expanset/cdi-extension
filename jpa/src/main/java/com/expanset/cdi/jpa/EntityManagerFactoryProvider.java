package com.expanset.cdi.jpa;

import javax.persistence.EntityManagerFactory;

/**
 * Access to {@link EntityManagerFactory}. Must be implemented.
 */
public interface EntityManagerFactoryProvider {
	
	EntityManagerFactory get(String unitName);
}
