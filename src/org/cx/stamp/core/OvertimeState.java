package org.cx.stamp.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.cx.stamp.tools.FileHandle;

public class OvertimeState extends AbstractState<Machine> {

	private static OvertimeState state = null;
	
	public static OvertimeState getInstance(){
		if(null==state)
			state = new OvertimeState();
		
		return state;
	}
	
	@Override
	public Integer getCode() {
		// TODO Auto-generated method stub
		return AbstractState.Code_OvertimeState;
	}
	
	@Override
	public void execute(Machine t) {
		// TODO Auto-generated method stub
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datetime = sFormat.format(new Date(System.currentTimeMillis()));
		String msg = "��ǰʱ�䣺"+datetime+"\r\n";
		msg += "��ӡ״̬����Ϣ���ͳ�ʱ��"+"\r\n";
		msg += "--------------------------------";
		
		t.printMessage(msg);
		
		t.changeState(WaitState.getInstance());
	}
}
