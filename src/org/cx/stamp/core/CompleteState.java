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
		String msg = "��ǰʱ�䣺"+datetime+"\r\n";
		msg += "��ӡ���ݣ�"+"\r\n";
		msg += t.getStampContent()+"\r\n";
		msg += "���񵥺ţ�"+t.getBillNo()+"\r\n";
		msg += "��ӡ״̬������ɣ�"+"\r\n";
		msg += "--------------------------------";
		
		t.printMessage(msg);
		
		try {
			List<String> params = new ArrayList<String>();
			params.add(PropertiesUtil.getConfigure("serial.confirm"));
			params.add(t.getSerialNumberText());
			JDBCService.update(PropertiesUtil.getConfigure("sql.update"), params);			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			t.printMessage("���ݿ������쳣��\r\n�������磬Ȼ����������Ӧ�ó���\r\nԭ��"+e.getMessage());
		}
		
		t.changeState(WaitState.getInstance());
	}
}
