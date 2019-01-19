package org.cx.stamp.tools;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Tools {

	/**
	 * ���ݸ����ı��룬�����µĿ�ӡ����
	 * @param serialType ���磺CT005  
	 * @return ������CT005�ı��룬���磺LAA01
	 * @throws SQLException 
	
	public static String generateSerialNumber(String serialType) throws SQLException {
		String newSerial = JDBCService.generateSerialNumber(PropertiesUtil.getConfigure("sql.generateSerialNumber"), serialType);
		
		List<String> params = new ArrayList<String>();
		params.add(newSerial);
		params.add(serialType);
		JDBCService.update(PropertiesUtil.getConfigure("sql.update.serialNumber"), params);
		
		return newSerial;
	} */
	
	/**
	 * ����һ���հ׵�״̬�ļ�
	 */
	public static void createBlankStatusFile() {
		FileHandle.create(PropertiesUtil.getConfigure("file.workPath"), PropertiesUtil.getConfigure("file.status"), "");
	}
	
	public static void main(String[] args) {
		
	}
}
