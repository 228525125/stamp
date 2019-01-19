package org.cx.stamp.core;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 输出信息格式错误
 * @author 陈贤
 *
 */
public class IncorrectFormatState extends AbstractState<Machine> {

	private static IncorrectFormatState state = null;
	
	public static IncorrectFormatState getInstance(){
		if(null==state)
			state = new IncorrectFormatState();
		
		return state;
	}
	
	@Override
	public void execute(Machine t) {
		// TODO Auto-generated method stub
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datetime = sFormat.format(new Date(System.currentTimeMillis()));
		String msg = "当前时间："+datetime+"\r\n";
		msg += "刻印数据："+"\r\n";
		msg += t.getStampContent()+"\r\n";
		msg += "刻印状态：格式错误！"+"\r\n";
		msg += "--------------------------------";
		
		t.printMessage(msg);
		
		t.changeState(WaitState.getInstance());
	}
}
