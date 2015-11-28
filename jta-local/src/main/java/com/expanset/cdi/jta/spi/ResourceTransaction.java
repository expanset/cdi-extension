package com.expanset.cdi.jta.spi;

import javax.transaction.SystemException;

public interface ResourceTransaction {
	
	void commit()
			throws IllegalStateException, SystemException;
	
	void rollback()
			throws IllegalStateException, SystemException;

	void suspend();	

	void resume();	

	void setTimeout(int seconds);
}
