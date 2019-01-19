package org.cx.stamp.tools;

import org.apache.log4j.PropertyConfigurator;

public class Logger
{
	public static org.apache.log4j.Logger logger; 
	
	public static void info(String msg) {
		logger.info(msg);
	}
	
	static {
		PropertyConfigurator.configure(ClassLoader.getSystemResource("resource/log4j.lcf"));
		logger = org.apache.log4j.Logger.getRootLogger();
	}
}
