package org.cx.stamp;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import java.awt.Font;

import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;

import org.cx.stamp.core.Machine;
import org.cx.stamp.tools.JDBCService;
import org.cx.stamp.tools.Logger;
import org.cx.stamp.tools.PropertiesUtil;
import javax.swing.BoxLayout;

public class MainFrame extends JFrame {
	
	private JPanel contentPane;
	private JTextField textField;          //任务单号
	private JTextArea textArea;            //信息窗口
	private JTable table;
	private JButton beginButton;           //开始按钮
	private JButton stopButton;            //停止按钮
	private JButton setButton;             //设置按钮
	private JButton queryButton;           //查询按钮
	
	private Boolean lock = false;
	
	private Machine machine = new Machine();
	private JPanel panel_1;
	private JTextField selectTextField;     //任务单文本框
	private JTextField stampTextField;      //刻印内容文本框
	private JTextField serialTextField;     //当前刻印的出厂编号
	
	private String billNo = null;
	private String model = null;
	private String batchNo = null;
	private String material = null;
	private String pressure = null;
	private String temperature = null;
	private String serialType = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 811, 437);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		beginButton = new JButton("\u5F00\u59CB");
		beginButton.setFont(new Font("宋体", Font.PLAIN, 12));
		beginButton.setBounds(10, 10, 69, 21);
		beginButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ae) {
				// TODO Auto-generated method stub
				String model = stampTextField.getText();
				String billNo = selectTextField.getText();
				if("".equals(model)){
					JOptionPane.showMessageDialog(null, "刻印内容为空，请重新选择任务单！", null, JOptionPane.INFORMATION_MESSAGE);
				}else if("".equals(billNo)){
					JOptionPane.showMessageDialog(null, "请选择一张任务单！", null, JOptionPane.INFORMATION_MESSAGE);
				}else if(!validateStampContent()){
					JOptionPane.showMessageDialog(null, "刻印内容不完整，请检查！", null, JOptionPane.INFORMATION_MESSAGE);
				}else{
					machine.start();
					beginButton.setEnabled(false);
					stopButton.setEnabled(true);
					setButton.setEnabled(false);
					queryButton.setEnabled(false);
					stampTextField.setEditable(false);
					
					lock = true;
				}
			}
		});
		contentPane.add(beginButton);
		
		stopButton = new JButton("\u505C\u6B62");
		stopButton.setFont(new Font("宋体", Font.PLAIN, 12));
		stopButton.setBounds(10, 41, 68, 21);
		stopButton.setEnabled(false);
		stopButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				beginButton.setEnabled(true);
				stopButton.setEnabled(false);
				setButton.setEnabled(true);
				queryButton.setEnabled(true);
				
				lock = false;
				
				clearStampContent();
				selectTextField.setText("");
				stampTextField.setText("");
				
				machine.stop();
			}
		});
		contentPane.add(stopButton);
		
		JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 73, 214, 313);
        
		
		textArea = new JTextArea();
		textArea.setForeground(Color.GREEN);
		textArea.setBackground(new Color(0, 0, 0));
		textArea.setFont(new Font("宋体", Font.PLAIN, 12));
		textArea.setBounds(10, 73, 214, 313);
		
		scrollPane.setViewportView(textArea);
		contentPane.add(scrollPane);
		
		setButton = new JButton("测试数据库");
		setButton.setFont(new Font("宋体", Font.PLAIN, 12));
		setButton.setBounds(129, 10, 69, 21);
		setButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try {
					if(JDBCService.isSuccess())
						printMessage("数据库连接成功！\r\n--------------------------------");
					else
						printMessage("数据库连接失败！\r\n--------------------------------");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					printMessage("数据库连接异常！\r\n请检查网络，然后重新启动应用程序！\r\n原因："+e.getMessage());
				}
			}
		});
		
		contentPane.add(setButton);
		
		textField = new JTextField();
		textField.setBounds(302, 25, 380, 21);
		contentPane.add(textField);
		textField.setColumns(10);
		
		queryButton = new JButton("\u67E5\u8BE2");
		queryButton.setBounds(692, 24, 93, 23);
		queryButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String text = textField.getText();				
				
				try {
					List<Map<String, Object>> ret = JDBCService.query_rwdh(text);
					DefaultTableModel tm = (DefaultTableModel) table.getModel();
					if(ret.isEmpty()){
						tm.setRowCount(0);
					}else{
						tm.setRowCount(ret.size());
					}
					
					String fieldString = PropertiesUtil.getConfigure("sql.query.fieldName");
					String [] fields = fieldString.split(",");
					
					for(int i=0;i<ret.size();i++){
						Map<String, Object> bean = ret.get(i);
						for(int j=0;j<fields.length;j++){
							table.setValueAt(bean.get(fields[j]), i, j);
						}
					}
					
					table.clearSelection();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					printMessage("数据库连接异常！\r\n请检查网络，然后重新启动应用程序！\r\n原因："+e.getMessage());
				}
			}
		});
		contentPane.add(queryButton);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(234, 73, 551, 313);
		contentPane.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		table = new JTable();
		String[] columnNames = new String[]{"任务单号","规格","炉批号","材质","压力","温度","出厂编号"}; 
		String[][] rowData = new String[][]{
				//{"WORK061002","AM7001WH","18M05","06.07.0028","插入筒","xxx","20"},
				//{"WORK061003","AM7001WI","18M05","06.07.0028","插入筒","xxx","20"},
				//{"WORK061004","AM7001WN","18M05","06.07.0028","插入筒","xxx","20"}
		};
		TableModel tm = new DefaultTableModel(rowData, columnNames);
		table.setModel(tm);
		
		/*table.setAutoscrolls(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		TableColumn rwColumn = table.getColumnModel().getColumn(0);
		TableColumn thColumn = table.getColumnModel().getColumn(1);
		TableColumn pcColumn = table.getColumnModel().getColumn(2);
		rwColumn.setWidth(200);
		thColumn.setWidth(150);
		pcColumn.setWidth(100);*/
		
		//表格选择器
		ListSelectionModel selectionModel = table.getSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		selectionModel.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub
				int selectedRow = table.getSelectedRow();
				
				if(!lock && !Integer.valueOf(-1).equals(selectedRow)){
					
					/*
					 * 全部清空，保证各项数据一致
					 */
					clearStampContent();
					
					Integer billNoIndex = Integer.valueOf(PropertiesUtil.getConfigure("sql.query.field.billNo"));
					Object billNo = table.getValueAt(selectedRow, billNoIndex);
					if(null!=billNo){
						selectTextField.setText(billNo.toString());
						MainFrame.this.billNo = billNo.toString();
					}else{
						selectTextField.setText("");
						MainFrame.this.billNo = null;
					}
					
					Integer modelIndex = Integer.valueOf(PropertiesUtil.getConfigure("sql.query.field.model"));
					Object model = table.getValueAt(selectedRow, modelIndex);
					if(null!=model){
						stampTextField.setText(model.toString());
						MainFrame.this.model = model.toString();
					}else{
						stampTextField.setText("");
						MainFrame.this.model = null;
					}
					
					Integer batchNoIndex = Integer.valueOf(PropertiesUtil.getConfigure("sql.query.field.batchNo"));
					Object batchNo = table.getValueAt(selectedRow, batchNoIndex);
					if(null!=batchNo){
						MainFrame.this.batchNo = batchNo.toString();
					}else{
						MainFrame.this.batchNo = null;
					}
					
					Integer materialIndex = Integer.valueOf(PropertiesUtil.getConfigure("sql.query.field.material"));
					Object material = table.getValueAt(selectedRow, materialIndex);
					if(null!=material){
						MainFrame.this.material = material.toString();
					}else{
						MainFrame.this.material = null;
					}
					
					
					Integer pressureIndex = Integer.valueOf(PropertiesUtil.getConfigure("sql.query.field.pressure"));
					Object pressure = table.getValueAt(selectedRow, pressureIndex);
					if(null!=pressure){
						MainFrame.this.pressure = pressure.toString();
					}else{
						MainFrame.this.pressure = null;
					}
					
					Integer temperatureIndex = Integer.valueOf(PropertiesUtil.getConfigure("sql.query.field.temperature"));
					Object temperature = table.getValueAt(selectedRow, temperatureIndex);
					if(null!=temperature){
						MainFrame.this.temperature = temperature.toString();
					}else{
						MainFrame.this.temperature = null;
					}
					
					Integer serialTypeIndex = Integer.valueOf(PropertiesUtil.getConfigure("sql.query.field.serialType"));
					Object serialType = table.getValueAt(selectedRow, serialTypeIndex);
					if(null!=serialType){
						MainFrame.this.serialType = serialType.toString();
					}else{
						MainFrame.this.serialType = null;
					}
					
					serialTextField.setText("");
					
				}
			}
		});
		
		panel.add(table.getTableHeader(), BorderLayout.NORTH);
		panel.add(table, BorderLayout.CENTER);
		
		panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		
		selectTextField = new JTextField();
		selectTextField.setEditable(false);
		selectTextField.setForeground(new Color(0, 0, 0));
		selectTextField.setBackground(new Color(175, 238, 238));
		//selectTextField.setText("11111111111111");
		panel_1.add(selectTextField);
		selectTextField.setColumns(20);
		
		stampTextField = new JTextField();
		stampTextField.setEditable(false);
		stampTextField.setBackground(new Color(175, 238, 238));
		stampTextField.setForeground(new Color(255, 0, 0));
		//stampTextField.setText("这里输入刻印内容");
		panel_1.add(stampTextField);
		stampTextField.setColumns(20);
		
		serialTextField = new JTextField();
		serialTextField.setForeground(Color.MAGENTA);
		serialTextField.setEditable(false);
		serialTextField.setBackground(new Color(175, 238, 238));
		//serialTextField.setText("这里输入刻印内容");
		panel_1.add(serialTextField);
		serialTextField.setColumns(20);
		
		stampTextField.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				String text = stampTextField.getText();
				if("这里输入刻印内容".equals(text)){
					stampTextField.setText("");
				}
			}
		});
		 
		JLabel label = new JLabel("\u4EFB\u52A1\u5355\u53F7\uFF1A");
		label.setBounds(237, 25, 70, 21);
		contentPane.add(label);
		
		machine.setFrame(this);
		
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub
				try {
					if(JDBCService.isSuccess())
						printMessage("数据库连接成功！\r\n--------------------------------");
					else{
						printMessage("数据库连接失败！\r\n--------------------------------");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					String msg = "数据库连接异常！\r\n请检查网络，然后重新启动应用程序！\r\n原因："+e.getMessage();
					printMessage(msg);
					JOptionPane.showMessageDialog(null, msg, null, JOptionPane.ERROR_MESSAGE);
					
				}
			}
			
			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void printMessage(String msg) {
		String text = textArea.getText();
		msg += "\r\n";
		msg += text;
		textArea.setText(msg);
	}
	
	public String getModel() {
		return this.model;
	}
	
	public String getBillNo() {
		return this.billNo;
	}
	
	public String getMaterial() {
		return this.material;
	}
	
	public String getBatchNo() {
		return batchNo;
	}
	
	public String getPressure() {
		return pressure;
	}
	
	public String getTemperature() {
		return temperature;
	}
	
	public String getSerialType() {
		return serialType;
	}
	
	public void setSerialNumberText(String text) {
		this.serialTextField.setText(text);
	}
	
	/**
	 * 出厂编号，例如：CT005XXXXX
	 * @return
	 */
	public String getSerialNumberText() {
		return this.serialTextField.getText();
	}
	
	public void doStopButton() {
		this.stopButton.doClick(1000);
	}
	
	/**
	 * 验证输出字符
	 * @param text
	 * @return
	 */
	private Boolean validateStampContent() {
		if(null!=this.billNo && !"".equals(this.billNo)
		&& null!=this.model && !"".equals(this.model)
		&& null!=this.batchNo && !"".equals(this.batchNo)
		&& null!=this.material && !"".equals(this.material)
		&& null!=this.pressure && !"".equals(this.pressure)
		&& null!=this.temperature && !"".equals(this.temperature)
		&& null!=this.serialType && !"".equals(this.serialType))
			return true;
		else
			return false;
	}
	
	private void clearStampContent() {
		this.billNo = null;
		this.model = null;
		this.batchNo = null;
		this.material = null;
		this.pressure = null;
		this.temperature = null;
		this.serialType = null;
	}
	
	/*public void clickStopButton() {
		beginButton.setEnabled(true);
		stopButton.setEnabled(false);
		setButton.setEnabled(true);
		
		lock = false;
	}*/
	
	private static class __Tmp {
		private static void __tmp() {
			  javax.swing.JPanel __wbp_panel = new javax.swing.JPanel();
		}
	}
}
