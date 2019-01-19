package org.cx.stamp.core;

import java.sql.SQLException;

import org.cx.stamp.MainFrame;
import org.cx.stamp.tools.FileHandle;
import org.cx.stamp.tools.JDBCService;
import org.cx.stamp.tools.Logger;
import org.cx.stamp.tools.PropertiesUtil;
import org.cx.stamp.tools.Tools;

/**
 * 刻印机
 * @author chenxian
 *
 */
public class Machine {

	public static String STATUS_NG_OVERTIME = "数据超时";
	public static String STATUS_NG_INCORRECTFORMAT = "格式有误";
	
	private StateMachine<Machine> stateMachine = null;
	private Thread thread = null;
	private MainFrame frame = null;
	
	private String serialNumber = null;          //AA01
	private String stampContent = null;          //NR.TXT内容
	
	public Machine() {
		// TODO Auto-generated constructor stub
		this.stateMachine = new StateMachine<Machine>(this);
		this.stateMachine.setCurrentState(WaitState.getInstance());
	}
	
	public void initStatus() {
		FileHandle.create(PropertiesUtil.getConfigure("file.workPath"), PropertiesUtil.getConfigure("file.status"), "");
	}
	
	/**
	 * 更新刻印机状态
	 */
	public void updateState() {
		getStateMachine().update();
	}
	
	/**
	 * 改变刻印机状态
	 * @param newState
	 */
	public void changeState(AbstractState<Machine> newState) {
		getStateMachine().changeState(newState);
	}
	
	/**
	 * 上一个状态
	 * @return
	 */
	public AbstractState<Machine> getPreviousState() {
		return getStateMachine().getPreviousState();
	}
	
	/**
	 * 是否刻印机等待数据
	 * @return
	 */
	public Boolean isReady() {
		String str = FileHandle.reader(PropertiesUtil.getConfigure("file.workPath"), PropertiesUtil.getConfigure("file.status"));
		return PropertiesUtil.getConfigure("file.status.ready").equals(str);
	}
	
	/**
	 * 是否正在刻印
	 * @return
	 */
	public Boolean isRunning() {
		String str = FileHandle.reader(PropertiesUtil.getConfigure("file.workPath"), PropertiesUtil.getConfigure("file.status"));
		return PropertiesUtil.getConfigure("file.status.running").equals(str);
	}
	
	/**
	 * 是否接收数据超时
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
	 * 是否刻印内容格式有问题
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
	 * 是否刻印完一次数据
	 * @return
	 */
	public Boolean isComplete() {
		String str = FileHandle.reader(PropertiesUtil.getConfigure("file.workPath"), PropertiesUtil.getConfigure("file.status"));
		return PropertiesUtil.getConfigure("file.status.ok").equals(str);
	}
	
	public String generateStampContent() {
		
		this.stampContent = null;
		
		try {
			//生成新的编码
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
			String msg = "数据库连接异常！\r\n"
					+ "请检查网络，然后重新启动应用程序！\r\n"
					+ "原因："+e.getMessage()+"\r\n"
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
		sn = sn.substring(0, 5)+" "+sn.substring(5, 8)+" "+sn.substring(8, 10);  //调整格式
		String M5 = "\r\nM5="+sn;
		this.stampContent = content + M1 + M2 + M3 + M4 + M5;
		
		return stampContent;
	}
	
	/**
	 * 获取刻印内容，包含M1 - M5
	 * @return
	 */
	public String getStampContent() {
		return this.stampContent;
	}
	
	/**
	 * 打印消息
	 * @param msg
	 */
	public void printMessage(String msg) {
		this.frame.printMessage(msg);
		Logger.info(msg);
	}
	
	/**
	 * 停止数据传输
	 */
	public void stop() {
		Thread t = getThread();
		t.stop();
	}
	
	/**
	 * 启动数据传输
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
	 * 出厂编号，例如：CT005XXXXX
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
