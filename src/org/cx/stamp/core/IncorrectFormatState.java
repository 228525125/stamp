package org.cx.stamp.core;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * �����Ϣ��ʽ����
 * @author ����
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
		String msg = "��ǰʱ�䣺"+datetime+"\r\n";
		msg += "��ӡ���ݣ�"+"\r\n";
		msg += t.getStampContent()+"\r\n";
		msg += "��ӡ״̬����ʽ����"+"\r\n";
		msg += "--------------------------------";
		
		t.printMessage(msg);
		
		t.changeState(WaitState.getInstance());
	}
}
