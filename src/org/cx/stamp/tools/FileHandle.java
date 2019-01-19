package org.cx.stamp.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandle {

	/*public static String WorkPath = "F:/work";
	public static String STATUS = "STATUS.TXT";
	public static String STATUS_READY = "Ready";
	public static String STATUS_RUNNING = "RUNNING";
	public static String STATUS_NG_OVERTIME = "NG：接收打标数据超时！";
	public static String STATUS_NG_INCORRECTFORMAT = "NG：传递内容格式有误，请检查！";
	public static String STATUS_OK = "OK";
	public static String NR = "NR.TXT";
	public static String END = "END.TXT";*/
	
	public static String reader(String filePath, String fileName) {
		File file = new File(filePath+"/"+fileName);
		
		if(file.exists() && file.renameTo(file)){
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);

				String str = br.readLine();
				String content = str;
				while(null!=str){
					str = br.readLine();
					if(null!=str){
						content += "\r\n"+str;
					}
				}
				fr.close();
				br.close();
				return content;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				Logger.info(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Logger.info(e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static void create(String filePath, String fileName, String content) {
		File file = new File(filePath);
		
		if(!file.exists()){
			file.mkdirs();
		}
		
		try {
			file = new File(filePath +"/"+ fileName);
			
			if(!file.exists()){
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file);
			fw.write(content);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logger.info(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		/*String str = FileHandle.reader(PropertiesUtil.getConfigure("file.workPath"), PropertiesUtil.getConfigure("file.status"));
		Integer index = str.indexOf(Machine.STATUS_NG_INCORRECTFORMAT);
		
		System.out.println(index);*/
		
		//WriterAppender wa=new WriterAppender(new SimpleLayout(),System.out);
		//Logger.logger.addAppender(wa);
		
		
		//FileHandle handle = new FileHandle();
		
		//System.out.println(str);
		
		/*while(true){
			String status = handle.reader(WorkPath,STATUS);
			if("Ready".equals(status)){
				String content = "[IN1]\r\nM1=ABCDEFG\r\nM2=A";
				handle.create(WorkPath, NR, content);
				handle.create(WorkPath, END, "");
			}
			
			if("NG：接收打标数据超时！".equals(status)){
				
			}
			
			if("RUNNING".equals(status)){
				
			}
			
			if("NG：传递内容格式有误，请检查！".equals(status)){
				String content = handle.reader(WorkPath, NR);
				System.out.println(content);
				System.out.println("停止");
			}
			
			if("OK".equals(status)){
				
			}
		}*/
		
		//System.out.println(text);
		//FileReader fr = new FileReader(str1);
	}
}
