package org.cx.stamp.core;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.cx.stamp.tools.FileHandle;
import org.cx.stamp.tools.JDBCService;
import org.cx.stamp.tools.PropertiesUtil;
import org.cx.stamp.tools.Tools;

public class ReadyState extends AbstractState<Machine> {

	private static ReadyState state = null;
	
	public static ReadyState getInstance(){
		if(null==state)
			state = new ReadyState();
		
		return state;
	}
	
	@Override
	public Integer getCode() {
		// TODO Auto-generated method stub
		return Code_ReadyState;
	}

	@Override
	public void execute(Machine t) {
		// TODO Auto-generated method stub
		
		String content = t.generateStampContent();
		
		if(null!=content){
			FileHandle.create(PropertiesUtil.getConfigure("file.workPath"), PropertiesUtil.getConfigure("file.nr"), content);
			FileHandle.create(PropertiesUtil.getConfigure("file.workPath"), PropertiesUtil.getConfigure("file.end"), "");
			
			SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String datetime = sFormat.format(new Date(System.currentTimeMillis()));
			String msg = "当前时间："+datetime+"\r\n";
			msg += "生成文件："+PropertiesUtil.getConfigure("file.nr")+","+PropertiesUtil.getConfigure("file.end")+"\r\n";
			msg += "刻印内容："+"\r\n";
			msg += content+"\r\n";
			msg += "任务单号："+t.getBillNo()+"\r\n";
			msg += "刻印状态：等待刻印！"+"\r\n";
			
			msg += "--------------------------------";
			t.printMessage(msg);
			
			try {
				JDBCService.insert(PropertiesUtil.getConfigure("sql.insert"), t.getSerialNumberText(), datetime, t.getBillNo());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				t.printMessage("数据库连接异常！\r\n请检查网络，然后重新启动应用程序！\r\n原因："+e.getMessage());
			}
		}else{
			String msg = "出厂编号生成失败！\r\n"
					+ "请检查任务单号是否正确，或编号溢出！\r\n"
					+ "程序已停止！\r\n"
					+ "--------------------------------";
			t.printMessage(msg);			
			t.changeState(BlankState.getInstance());
			t.doStopButton();
		}
		
		t.changeState(WaitState.getInstance());
	}
}
