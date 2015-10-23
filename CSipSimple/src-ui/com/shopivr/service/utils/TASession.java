package com.shopivr.service.utils;

public final class TASession {
	
//	private static TASession singleton = new TASession();
	
	/* Static 'instance' method */
//	public static TASession getInstance() {
//		return singleton;
//	}
	
	public enum ClientStatus {
		ENABLE,
		DISABLE;
	}
/*	   
	public enum ClientStatus {
		ENABLE(0),
		DISABLE(1);
		
		private int value;
		private ClientStatus(int value) {
                this.value = value;
        }
	}
*/
	public static ClientStatus status;
	public static ClientStatus previousStatus;
	
	public static void saveStatus() {
		previousStatus = status;
	}
}
