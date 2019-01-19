package org.cx.stamp.core;

import java.sql.SQLException;

import org.cx.stamp.MainFrame;
import org.cx.stamp.tools.FileHandle;
import org.cx.stamp.tools.JDBCService;
import org.cx.stamp.tools.Logger;
import org.cx.stamp.tools.PropertiesUtil;
import org.cx.stamp.tools.Tools;

/**
 * ��ӡ��
 * @author chenxian
 *
 */
public class Machine {

	public static String STATUS_NG_OVERTIME = "���ݳ�ʱ";
	public static String STATUS_NG_INCORRECTFORMAT = "��ʽ����";
	
	private StateMachine<Machine> stateMachine = null;
	private Thread thread = null;
	private MainFrame frame = null;
	
	private String serialNumber = null;          //AA01
	private String stampContent = null;          //NR.TXT����
	
	public Machine() {
		// TODO Auto-generated constructor stub
		this.stateMachine = new StateMachine<Machine>(this);
		this.stateMachine.setCurrentState(WaitState.getInstance());
	}
	
	public void initStatus() {
		FileHandle.create(PropertiesUtil.getConfigure("file.workPath"), PropertiesUtil.getConfigure("file.status"), "");
	}
	
	/**
	 * ���¿�ӡ��״̬
	 */
	public void updateState() {
		getStateMachine().update();
	}
	
	/**
	 * �ı��ӡ��״̬
	 * @param newState
	 */
	public void changeState(AbstractState<Machine> newState) {
		getStateMachine().changeState(newState);
	}
	
	/**
	 * ��һ��״̬
	 * @return
	 */
	public AbstractState<Machine> getPreviousState() {
		return getStateMachine().getPreviousState();
	}
	
	/**
	 * �Ƿ��ӡ���ȴ�����
	 * @return
	 */
	public Boolean isReady() {
		String str = FileHandle.reader(PropertiesUtil.getConfigure("file.workPath"), PropertiesUtil.getConfigure("file.status"));
		return PropertiesUtil.getConfigure("file.status.ready").equals(str);
	}
	
	/**
	 * �Ƿ����ڿ�ӡ
	 * @return
	 */
	public Boolean isRunning() {
		String str = FileHandle.reader(PropertiesUtil.getConfigure("file.workPath"), PropertiesUtil.getConfigure("file.status"));
		return PropertiesUtil.getConfigure("file.status.running").equals(str);
	}
	
	/**
	 * �Ƿ�������ݳ�ʱ
	 * @return
	 */
	public Boolean isOvertime() {
		String str = FileHandle.reader(PropertiesUtil.getConfigure("file.workPath"), PropertiesUtil.getConfigure("file.status"));
		if(null!=str && !Integer.valueOf(-1).equals(str.indexOf(STATUS_NG_OVERTIME)))
			return true;
		else
			return false;
	}
	
	/**
	 * �Ƿ��ӡ���ݸ�ʽ������
	 * @return
	 */
	public Boolean isIncorrectFormat() {
		String str = FileHandle.reader(PropertiesUtil.getConfigure("file.workPath"), PropertiesUtil.getConfigure("file.status"));
		
		if(null!=str && !Integer.valueOf(-1).equals(str.indexOf(STATUS_NG_INCORRECTFORMAT)))
			return true;
		else
			return false;
	}
	
	/**
	 * �Ƿ��ӡ��һ������
	 * @return
	 */
	public Boolean isComplete() {
		String str = FileHandle.reader(PropertiesUtil.getConfigure("file.workPath"), PropertiesUtil.getConfigure("file.status"));
		return PropertiesUtil.getConfigure("file.status.ok").equals(str);
	}
	
	public String generateStampContent() {
		
		this.stampContent = null;
		
		try {
			//�����µı���
			String serialNumber = JDBCService.generateSerialNumber(getBillNo());
			if(null==serialNumber){
				serialNumber = null;
				setSerialNumberText("");
				return null;
			}
			this.serialNumber = serialNumber;
			setSerialNumberText(getSerialType()+serialNumber);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			String msg = "���ݿ������쳣��\r\n"
					+ "�������磬Ȼ����������Ӧ�ó���\r\n"
					+ "ԭ��"+e.getMessage()+"\r\n"
					+ "--------------------------------";
			printMessage(msg);
			serialNumber = null;
			setSerialNumberText("");
		}
		
		String content = "[IN1]";
		String M1 = "\r\nM1="+getModel();
		String M2 = "\r\nM2="+getPressure()+"@"+getTemperature();
		String M3 = "\r\nM3="+getMaterial();
		String M4 = "\r\nM4="+"HT."+getBatchNo();
		String sn = getSerialNumberText();
		sn = sn.substring(0, 5)+" "+sn.substring(5, 8)+" "+sn.substring(8, 10);  //������ʽ
		String M5 = "\r\nM5="+sn;
		this.stampContent = content + M1 + M2 + M3 + M4 + M5;
		
		return stampContent;
	}
	
	/**
	 * ��ȡ��ӡ���ݣ�����M1 - M5
	 * @return
	 */
	public String getStampContent() {
		return this.stampContent;
	}
	
	/**
	 * ��ӡ��Ϣ
	 * @param msg
	 */
	public void printMessage(String msg) {
		this.frame.printMessage(msg);
		Logger.info(msg);
	}
	
	/**
	 * ֹͣ���ݴ���
	 */
	public void stop() {
		Thread t = getThread();
		t.stop();
	}
	
	/**
	 * �������ݴ���
	 */
	public void start() {
		initStatus();
		getThread().start();
		
	}
	
	public StateMachine<Machine> getStateMachine() {
		return stateMachine;
	}
	
	public void setFrame(MainFrame frame) {
		this.frame = frame;
	}

	public String getSerialType() {
		return this.frame.getSerialType();
	}
	
	public String getBillNo() {
		return this.frame.getBillNo();
	}
	
	public String getModel(){
		return this.frame.getModel();
	}
	
	public String getBatchNo() {
		return this.frame.getBatchNo();
	}
	
	public String getMaterial() {
		return this.frame.getMaterial();
	}
	
	public String getPressure() {
		return this.frame.getPressure();
	}
	
	public String getTemperature() {
		return this.frame.getTemperature();
	}
	
	public String getSerialNumber() {
		return this.serialNumber;
	}
	
	/**
	 * ������ţ����磺CT005XXXXX
	 * @return
	 */
	public String getSerialNumberText() {
		return this.frame.getSerialNumberText();
	}
	
	public void setSerialNumberText(String serialNumberText) {
		this.frame.setSerialNumberText(serialNumberText);
	}
	
	public void doStopButton() {
		this.frame.doStopButton();
	}
	
	private Thread getThread() {
		if(null==thread || !thread.isAlive()){
			thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while(true){
						updateState();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Thread.yield();
					}
				}
			});
		}
		return thread;
	}
	
	public static void main(String[] args) {
		String str = "CT005MMA01";
		System.out.println(str.substring(0, 5)+" "+str.substring(5, 8)+" "+str.substring(8, 10));
	}
}
