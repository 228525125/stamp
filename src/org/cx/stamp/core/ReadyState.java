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
			String msg = "��ǰʱ�䣺"+datetime+"\r\n";
			msg += "�����ļ���"+PropertiesUtil.getConfigure("file.nr")+","+PropertiesUtil.getConfigure("file.end")+"\r\n";
			msg += "��ӡ���ݣ�"+"\r\n";
			msg += content+"\r\n";
			msg += "���񵥺ţ�"+t.getBillNo()+"\r\n";
			msg += "��ӡ״̬���ȴ���ӡ��"+"\r\n";
			
			msg += "--------------------------------";
			t.printMessage(msg);
			
			try {
				JDBCService.insert(PropertiesUtil.getConfigure("sql.insert"), t.getSerialNumberText(), datetime, t.getBillNo());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				t.printMessage("���ݿ������쳣��\r\n�������磬Ȼ����������Ӧ�ó���\r\nԭ��"+e.getMessage());
			}
		}else{
			String msg = "�����������ʧ�ܣ�\r\n"
					+ "�������񵥺��Ƿ���ȷ�����������\r\n"
					+ "������ֹͣ��\r\n"
					+ "--------------------------------";
			t.printMessage(msg);			
			t.changeState(BlankState.getInstance());
			t.doStopButton();
		}
		
		t.changeState(WaitState.getInstance());
	}
}
