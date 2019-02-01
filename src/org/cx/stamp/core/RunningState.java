package org.cx.stamp.core;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RunningState extends AbstractState<Machine> {

	private static RunningState state = null;
	
	public static RunningState getInstance(){
		if(null==state)
			state = new RunningState();
		
		return state;
	}
	
	@Override
	public Integer getCode() {
		// TODO Auto-generated method stub
		return Code_RunningState;
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
		msg += "刻印状态：刻印中..."+"\r\n";
		msg += "--------------------------------";
		
		t.printMessage(msg);
		
		t.changeState(WaitState.getInstance());
	}
}
