package org.cx.stamp.core;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.cx.stamp.tools.JDBCService;
import org.cx.stamp.tools.PropertiesUtil;
import org.cx.stamp.tools.Tools;

public class CompleteState extends AbstractState<Machine> {

	private static CompleteState state = null;
	
	public static CompleteState getInstance(){
		if(null==state)
			state = new CompleteState();
		
		return state;
	}
	
	@Override
	public Integer getCode() {
		// TODO Auto-generated method stub
		return AbstractState.Code_CompleteState;
	}
	
	@Override
	public void execute(Machine t) {
		// TODO Auto-generated method stub
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datetime = sFormat.format(new Date(System.currentTimeMillis()));
		String msg = "当前时间："+datetime+"\r\n";
		msg += "刻印数据："+"\r\n";
		msg += t.getStampContent()+"\r\n";
		msg += "任务单号："+t.getBillNo()+"\r\n";
		msg += "刻印状态：已完成！"+"\r\n";
		msg += "--------------------------------";
		
		t.printMessage(msg);
		
		try {
			List<String> params = new ArrayList<String>();
			params.add(PropertiesUtil.getConfigure("serial.confirm"));
			params.add(t.getSerialNumberText());
			JDBCService.update(PropertiesUtil.getConfigure("sql.update"), params);			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			t.printMessage("数据库连接异常！\r\n请检查网络，然后重新启动应用程序！\r\n原因："+e.getMessage());
		}
		
		t.changeState(WaitState.getInstance());
	}
}
