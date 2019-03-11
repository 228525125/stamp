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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JLabel;

import org.cx.stamp.core.AbstractState;
import org.cx.stamp.core.Machine;
import org.cx.stamp.tools.JDBCService;
import org.cx.stamp.tools.Logger;
import org.cx.stamp.tools.PropertiesUtil;

import javax.swing.BoxLayout;

public class MainFrame extends JFrame {
	
	private JPanel contentPane;
	private JTextField textField;          //���񵥺�
	private JTextArea textArea;            //��Ϣ����
	private JTable table;
	private JButton beginButton;           //��ʼ��ť
	private JButton stopButton;            //ֹͣ��ť
	private JButton setButton;             //���ð�ť
	private JButton queryButton;           //��ѯ��ť
	
	private Boolean lock = false;
	
	private Machine machine = new Machine();
	private JPanel panel_1;
	private JTextField selectTextField;     //�����ı���
	private JTextField stampTextField;      //����ͺ��ı���
	private JTextField serialTextField;     //��ǰ��ӡ�ĳ������
	
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
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		beginButton = new JButton("\u5F00\u59CB");
		beginButton.setFont(new Font("����", Font.PLAIN, 12));
		beginButton.setBounds(10, 10, 69, 21);
		
		//-------------------------��ʼ��ť�¼�---------------------------------
		/*
		 * �����ж��Ƿ�ѡ�������񵥣�
		 * ���ж�ѡ������񵥵Ĳ�Ʒ����Ƿ����Ҫ��
		 * �����ֺᴨ�����������Ʒ��ѡ�������ɹ���ע�����ɹ��������ݿ���ɣ�����ֻ�ж��Ƿ���Կ�ʼ����״̬��
		 */
		beginButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ae) {
				// TODO Auto-generated method stub
				String model = stampTextField.getText();
				String billNo = selectTextField.getText();
				
				/*
				 * ����Ǻᴨ�������ͺ���CV��ͷ����ôҪ��֤������Ϣ�������ԣ����������Ʒ����ֻ��Ҫ���񵥺�
				 */
				if("".equals(billNo)){
					JOptionPane.showMessageDialog(null, "��ѡ��һ�����񵥣�", null, JOptionPane.INFORMATION_MESSAGE);
					return ;
				}
				
				if("".equals(model) || model.length()<2){
					JOptionPane.showMessageDialog(null, "��ӡ����Ϊ�գ�������ѡ�����񵥣�", null, JOptionPane.INFORMATION_MESSAGE);
					return ;
				}
				
				if("CYS".equals(PropertiesUtil.getConfigure("stamp.model")) && "CV".equals(model.substring(0, 2)) && validateStampContent()){
					clickBeginButton();
				} else if("JH".equals(PropertiesUtil.getConfigure("stamp.model"))){
					clickBeginButton();
				}
				else{
					JOptionPane.showMessageDialog(null, "��ӡ���ݲ����������飡", null, JOptionPane.INFORMATION_MESSAGE);
					return ;
				}
			}
		});
		contentPane.add(beginButton);
		
		stopButton = new JButton("\u505C\u6B62");
		stopButton.setFont(new Font("����", Font.PLAIN, 12));
		stopButton.setBounds(10, 41, 68, 21);
		stopButton.setEnabled(false);
		
		//-------------------------ֹͣ��ť�¼�------------------------------
		/*
		 * ֻ�е�ϵͳ���ڡ���ɡ��������������ȴ���������ʱ���⼸��״̬ʱ������ʹ�ð�ť��
		 * ���ѡ������ݣ�
		 * ֹͣ״̬����
		 */
		stopButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(AbstractState.Code_CompleteState.equals(machine.getCurrentState().getCode()) 
				|| AbstractState.Code_IncorrectFormatState.equals(machine.getCurrentState().getCode())
				|| AbstractState.Code_WaitState.equals(machine.getCurrentState().getCode())
				|| AbstractState.Code_OvertimeState.equals(machine.getCurrentState().getCode())){
					clickStopButton();
				}
			}
		});
		contentPane.add(stopButton);
		
		JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 73, 214, 313);
		
		textArea = new JTextArea();
		textArea.setForeground(Color.GREEN);
		textArea.setBackground(new Color(0, 0, 0));
		textArea.setFont(new Font("����", Font.PLAIN, 12));
		textArea.setBounds(10, 73, 214, 313);
		
		scrollPane.setViewportView(textArea);
		contentPane.add(scrollPane);
		
		setButton = new JButton("�������ݿ�");
		setButton.setFont(new Font("����", Font.PLAIN, 12));
		setButton.setBounds(129, 10, 69, 21);
		setButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try {
					if(JDBCService.isSuccess())
						printMessage("���ݿ����ӳɹ���\r\n--------------------------------");
					else
						printMessage("���ݿ�����ʧ�ܣ�\r\n--------------------------------");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					printMessage("���ݿ������쳣��\r\n�������磬Ȼ����������Ӧ�ó���\r\nԭ��"+e.getMessage());
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
		
		//-------------------------��ѯ��ť�¼�----------------------------
		/*
		 * ��ձ�����ݣ�
		 * �����ݿ��ȡ�������������
		 */
		queryButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String text = textField.getText();
				
				table.clearSelection();
				
				try {
					List<Map<String, Object>> ret = JDBCService.query_rwdh(text);
					DefaultTableModel tm = (DefaultTableModel) table.getModel();
					
					/*
					 * ��ձ��
					 */
					for(int i=0;i<tm.getRowCount();i++){
						for(int j=0;j<tm.getColumnCount();j++){
							tm.setValueAt(null, i, j);
						}
						tm.removeRow(i);
					}
					
					tm.setRowCount(0);
					
					String fieldString = PropertiesUtil.getConfigure("sql.query.fieldName");
					String [] fields = fieldString.split(",");
					Vector<String> columnName = new Vector<String>();
					String [] columnNames = PropertiesUtil.getConfigure("table.columnNames").split(",");
					for(String cn : columnNames){
						columnName.add(cn);
					}
					
					/*columnName.add("���񵥺�");
					columnName.add("���");
					columnName.add("¯����");
					columnName.add("����");
					columnName.add("ѹ��");
					columnName.add("�¶�");
					columnName.add("�������");*/
					
					Vector rows = new Vector();
					
					/*
					 * ��������
					 */
					for(int i=0;i<ret.size();i++){
						Map<String, Object> bean = ret.get(i);
						
						String sqlData = "����"+text+"��ѯ�����\r\n";
						Iterator<String> iterator = bean.keySet().iterator();
						while(iterator.hasNext()){
							String key = iterator.next();
							sqlData += bean.get(key);
							if(iterator.hasNext())
								sqlData += "\r\n";
						}
						
						printMessage(sqlData);
						Logger.info("���ݿ��ѯ�����"+sqlData);
						printMessage("--------------------------------");
						
						String info = "";
						Vector row = new Vector();
						for(int j=0;j<fields.length;j++){
							Object value = bean.get(fields[j]);
							row.add(value);
							
							info += value;
							if((j+1)<fields.length)
								info += ",";
						}
						rows.add(row);
						
						Logger.info("������("+i+")�����ݣ�"+info);
					}
					
					tm.setColumnCount(columnName.size());       //��������
					tm.setRowCount(rows.size());                //��������
					
					tm.setDataVector(rows, columnName);
					
					/*
					 * ��֤�������
					 */
					String tableData = "";
					for(int i=0;i<table.getRowCount();i++){
						tableData += ""+i+"�У�";
						for(int j=0;j<table.getColumnCount();j++){
							tableData += table.getValueAt(i, j);
							
							if((j+1)<table.getColumnCount())
								tableData +=",";
						}
						if((i+1)<table.getRowCount())
							tableData += "\n";
					}
					Logger.info("���ձ�����ݣ�"+tableData);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					printMessage("���ݿ������쳣��\r\n�������磬Ȼ����������Ӧ�ó���\r\nԭ��"+e.getMessage());
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
		String[] columnNames = PropertiesUtil.getConfigure("table.columnNames").split(","); 
		String[][] rowData = new String[][]{
		};
		TableModel tm = new DefaultTableModel(rowData, columnNames);
		table.setModel(tm);
		
		//���ѡ����
		ListSelectionModel selectionModel = table.getSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		//-------------------------ѡ�����¼�----------------------------
		/*
		 * ��ջ���ĸ������ݣ�
		 * Ȼ�����������ȡ���涨���ݽ��б��棻
		 */
		selectionModel.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub
				int selectedRow = table.getSelectedRow();
				
				if(!lock && !Integer.valueOf(-1).equals(selectedRow)){
					
					String log = "ѡ���У�"+selectedRow+"\n";
					/*
					 * ȫ����գ���֤��������һ��
					 */
					clearStampContent();
					
					Integer billNoIndex = Integer.valueOf(PropertiesUtil.getConfigure("sql.query.field.billNo"));
					Object billNo = table.getValueAt(selectedRow, billNoIndex);
					if(null!=billNo){
						selectTextField.setText(billNo.toString());
						MainFrame.this.billNo = billNo.toString();
						log += "billNo-"+billNo.toString()+",";
					}else{
						selectTextField.setText("");
						MainFrame.this.billNo = null;
						log += "billNo-null,";
					}
					
					Integer modelIndex = Integer.valueOf(PropertiesUtil.getConfigure("sql.query.field.model"));
					Object model = table.getValueAt(selectedRow, modelIndex);
					if(null!=model){
						stampTextField.setText(model.toString());
						MainFrame.this.model = model.toString();
						log += "model-"+model.toString()+",";
					}else{
						stampTextField.setText("");
						MainFrame.this.model = null;
						log += "model-null,";
					}
					
					Integer batchNoIndex = Integer.valueOf(PropertiesUtil.getConfigure("sql.query.field.batchNo"));
					Object batchNo = table.getValueAt(selectedRow, batchNoIndex);
					if(null!=batchNo){
						MainFrame.this.batchNo = batchNo.toString();
						log += "batchNo-"+batchNo.toString()+",";
					}else{
						MainFrame.this.batchNo = null;
						log += "batchNo-null,";
					}
					
					Integer materialIndex = Integer.valueOf(PropertiesUtil.getConfigure("sql.query.field.material"));
					Object material = table.getValueAt(selectedRow, materialIndex);
					if(null!=material){
						MainFrame.this.material = material.toString();
						log += "material-"+material.toString()+",";
					}else{
						MainFrame.this.material = null;
						log += "material-null,";
					}
					
					
					Integer pressureIndex = Integer.valueOf(PropertiesUtil.getConfigure("sql.query.field.pressure"));
					Object pressure = table.getValueAt(selectedRow, pressureIndex);
					if(null!=pressure){
						MainFrame.this.pressure = pressure.toString();
						log += "pressure-"+pressure.toString()+",";
					}else{
						MainFrame.this.pressure = null;
						log += "pressure-null,";
					}
					
					Integer temperatureIndex = Integer.valueOf(PropertiesUtil.getConfigure("sql.query.field.temperature"));
					Object temperature = table.getValueAt(selectedRow, temperatureIndex);
					if(null!=temperature){
						MainFrame.this.temperature = temperature.toString();
						log += "temperature-"+temperature.toString()+",";
					}else{
						MainFrame.this.temperature = null;
						log += "temperature-null,";
					}
					
					Integer serialTypeIndex = Integer.valueOf(PropertiesUtil.getConfigure("sql.query.field.serialType"));
					Object serialType = table.getValueAt(selectedRow, serialTypeIndex);
					if(null!=serialType){
						MainFrame.this.serialType = serialType.toString();
						log += "serialType-"+serialType.toString();
					}else{
						MainFrame.this.serialType = null;
						log += "serialType-null";
					}
					
					serialTextField.setText("");
					
					Logger.info("ѡ���������ݣ�"+log);
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
		//stampTextField.setText("���������ӡ����");
		panel_1.add(stampTextField);
		stampTextField.setColumns(20);
		
		serialTextField = new JTextField();
		serialTextField.setForeground(Color.MAGENTA);
		serialTextField.setEditable(false);
		serialTextField.setBackground(new Color(175, 238, 238));
		//serialTextField.setText("���������ӡ����");
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
				if("���������ӡ����".equals(text)){
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
						printMessage("���ݿ����ӳɹ���\r\n--------------------------------");
					else{
						printMessage("���ݿ�����ʧ�ܣ�\r\n--------------------------------");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					String msg = "���ݿ������쳣��\r\n�������磬Ȼ����������Ӧ�ó���\r\nԭ��"+e.getMessage();
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
		text += "\r\n";
		text += msg;
		textArea.setText(text);
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
	 * ������ţ����磺CT005XXXXX
	 * @return
	 */
	public String getSerialNumberText() {
		return this.serialTextField.getText();
	}
	
	public void doStopButton() {
		this.stopButton.doClick(1000);
	}
	
	/**
	 * ��֤����ַ�
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
	
	private void clickBeginButton() {
		machine.start();
		beginButton.setEnabled(false);
		stopButton.setEnabled(true);
		setButton.setEnabled(false);
		queryButton.setEnabled(false);
		stampTextField.setEditable(false);
		
		lock = true;
	}
	
	private void clickStopButton() {
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
	
	private static class __Tmp {
		private static void __tmp() {
			  javax.swing.JPanel __wbp_panel = new javax.swing.JPanel();
		}
	}
}
