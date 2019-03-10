package org.cx.stamp.tools;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Tools {

	/**
	 * 根据给定的编码，生成新的刻印编码
	 * @param serialType 例如：CT005  
	 * @return 不包含CT005的编码，例如：LAA01
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
	 * 创建一个空白的状态文件
	 */
	public static void createBlankStatusFile() {
		FileHandle.create(PropertiesUtil.getConfigure("file.workPath"), PropertiesUtil.getConfigure("file.status"), "");
	}
	
	/**
	 * 格式化SN，非横川阀组，则生成url格式的字符串
	 * @param sn
	 * @return
	 */
	public static String formatSN(String sn) {
		String result = null;
		if("JH".equals(sn.substring(0, 2))){
			result = PropertiesUtil.getConfigure("product.url")+sn;
		}else{
			result = sn.substring(0, 5)+" "+sn.substring(5, 8)+" "+sn.substring(8, 10);  //调整格式
		}
		return result;
	}
	
	public static void main(String[] args) {
		
	}
}
