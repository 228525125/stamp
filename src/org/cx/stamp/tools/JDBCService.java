package org.cx.stamp.tools;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCService {

	public static Connection getConnection() throws SQLException{
		String DRIVER = PropertiesUtil.getConfigure("database.driverClassName");
		String URL = PropertiesUtil.getConfigure("database.url");
		String USER = PropertiesUtil.getConfigure("database.username");
		String PASSWORD = PropertiesUtil.getConfigure("database.password");
		try {
			Class.forName(DRIVER);
			Connection conn=DriverManager.getConnection(URL, USER, PASSWORD);
			return conn;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Boolean isSuccess() throws SQLException {
		Connection conn = getConnection();
		Boolean ret = null==conn ? false : true;
		if(ret){
			conn.close();
		}
		return ret;
	}
	
	public static List<Map<String, Object>> query(String sql, String parameter) throws SQLException {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		
		Connection conn = getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, parameter);
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()){
			Map<String, Object> bean = new HashMap<String, Object>();
			Integer count = rs.getMetaData().getColumnCount();
			for(int i=1;i<=count;i++){
				String name = rs.getMetaData().getColumnName(i);
				bean.put(name, rs.getObject(i));
			}
			ret.add(bean);
		}
		
		rs.close();
		ps.close();
		conn.close();
		
		return ret;
	}
	
	public static void insert(String sql, String content, String datetime, String billNo, String sn) throws SQLException {
		
		Connection conn = getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, content);
		ps.setString(2, datetime);
		ps.setString(3, billNo);
		ps.setString(4, sn);
		ps.execute();
		
		ps.close();
		conn.close();
	}
	
	/*public static String generateSerialNumber(String sql, String partContent, Integer length) throws SQLException {
		String ret = "1";
		List<Map<String, Object>> list = query(sql,partContent);
		if(!list.isEmpty()){
			String content = list.get(0).toString();
			String num = content.substring(content.length()-5, content.length()-1);
			Integer serial = Integer.valueOf(num)+1;
			ret = serial.toString();
		}
		
		for(int i=ret.length();i<length;i++){
			ret = "0" + ret;
		}
		
		return ret;
	}
	
	public static String generateSerialNumber(String sql, String type) throws SQLException {
		String result = null;
		List<Map<String, Object>> list = query(sql, type);
		if(!list.isEmpty()){
			String serial = list.get(0).get("SValue").toString();
			Connection conn = getConnection();
			CallableStatement cs = conn.prepareCall("{call stamp_serial(?,?)}");
			cs.setObject(1, serial);
			cs.registerOutParameter(2, java.sql.Types.VARCHAR);
			cs.execute();
			result = cs.getObject(2).toString();
			
			cs.close();
            conn.close();
		}
		
		return result;
	}*/
	
	/**
	 * 生成出厂编号
	 * @param BillNo 任务单号
	 * @return
	 * @throws SQLException
	 */
	public static String generateSerialNumber(String BillNo) throws SQLException {
		String result = null;
		Connection conn = getConnection();
		CallableStatement cs = conn.prepareCall("{call generateSN(?,?)}");
		cs.setObject(1, BillNo);
		cs.registerOutParameter(2, java.sql.Types.VARCHAR);
		cs.execute();
		if(null!=cs.getObject(2))
			result = cs.getObject(2).toString();

		cs.close();
        conn.close();
		
		return result;
	}
	
	public static void update(String sql, List<String> params) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
		
		for(int i=0;i<params.size();i++){
			ps.setString(i+1, params.get(i));
		}
		ps.execute();
		
		ps.close();
		conn.close();
	}
	
	public static List<Map<String, Object>> query_rwdh(String billNo) throws SQLException {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		
		Connection conn = getConnection();
		CallableStatement cs = conn.prepareCall("{call query_rwdh(?)}");
		cs.setString(1, billNo);
		ResultSet rs = cs.executeQuery();
		
		while(rs.next()){
			Map<String, Object> bean = new HashMap<String, Object>();
			Integer count = rs.getMetaData().getColumnCount();
			for(int i=1;i<=count;i++){
				String name = rs.getMetaData().getColumnName(i);
				bean.put(name, rs.getObject(i));
			}
			ret.add(bean);
		}
		
		rs.close();
		cs.close();
		conn.close();
		
		return ret;
	}
	
	public static void main(String[] args) {
		
		try {
			List<Map<String, Object>> list = query_rwdh("07287");
			System.out.println(list.size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//update(PropertiesUtil.getConfigure("sql.update"), "AM7001WM18M050002", "Y");
		
		//System.out.println(generateSerialNumber("select content from test where content like ? order by content desc", "%"+"AB7001WH18H05"+"%", 4));
		
		/*List<Map<String, Object>> list = null;
		try {
			list = query("select top 10 a.FBillNo,b.FHelpCode,a.FGMPBatchNo,b.FNumber,b.FName,b.FModel,a.FQty from ICMO a left join t_ICItem b on a.FItemID=b.FItemID where a.FBillNo like ? order by a.FCheckDate desc","%"+"74143"+"%");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Map<String, Object> bean : list){
			for(String key : bean.keySet()){
				System.out.println(key+":"+bean.get(key));
			}
		}*/
		
		/*if(!list.isEmpty()){
			String content = list.get(0).toString();
			String num = content.substring(content.length()-5, content.length()-1);
			System.out.println(num);
			Integer serial = Integer.valueOf(num)+1;
			System.out.println(serial.toString().length());
			String ret = serial.toString();
			for(int i=ret.length();i<4;i++){
				ret = "0" + ret;
			}
			System.out.println(ret);
		}else{
			
		}*/
		
		/*String sql = "insert into test(content,datetime,mark) values(?,?,'N')";
		String content = "AM7001WM18M050002";
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datetime = sFormat.format(new Date(System.currentTimeMillis()));
		insert(sql, content, datetime);*/
	}
}
