package com.expanset.cdi.jta;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.expanset.cdi.jta.spi.ResourceTransaction;

@Dependent
public class LocalTransactionService {

	@Inject
	private LocalTransactionManager transactionManager;
	
	public void registerResourceTransaction(ResourceTransaction resourceTransaction) {
		transactionManager.registerResourceTransaction(resourceTransaction);
	}
}
