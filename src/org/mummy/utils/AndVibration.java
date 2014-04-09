package org.mummy.utils;


public class AndVibration {
	
	public static void duration(final int pMilliseconds) {		
		
		if ( AndEnviroment.getInstance().getVibro()) {
			try {
				AndEnviroment.getInstance().getEngine().vibrate(pMilliseconds);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}