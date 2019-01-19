package org.cx.stamp.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

	public static String getConfigure(String name) {
		Properties prop = new Properties();
		
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("resource/db.properties");
		//InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("D:/stamp/db.properties");
		try {
			prop.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String value = prop.getProperty(name);
		return value;
	}
	
	public static void main(String[] args) {
		System.out.println(ClassLoader.getSystemResource("../"));
	}
}
