package com.cdtu.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

public class UploadServlet extends HttpServlet {

	public UploadServlet() {
		super();
	}

	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		boolean ismultipartContent = ServletFileUpload.isMultipartContent(request);
		if(!ismultipartContent){
			throw new RuntimeException("your form is not from multiype/");
		}
		//1.创建工厂类DiskFileItemFactory对象：
		DiskFileItemFactory factory = new DiskFileItemFactory();
//		2.使用工厂创建解析器对象：
		ServletFileUpload fileUpload = new ServletFileUpload(factory);
		fileUpload.setHeaderEncoding("UTF-8");
//		3.使用解析器来解析request对象：
		try {
			List<FileItem> fileItems = fileUpload.parseRequest(request);
			for (FileItem fileItem : fileItems) {
				if(fileItem.isFormField()){
					//普通表单
					processFormField(fileItem);
				}
				else{
					//上传文件表单
					processFormUpload(fileItem);
				}
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	//上传文件表单
	private void processFormUpload(FileItem fileitem) {
		//得到文件名称
		String filename = fileitem.getName();//‪C:\Users\Administrator\Pictures\1.jpg 或者1.jpg
		//避免文件名重复
		filename=UUID.randomUUID()+filename;
		//处理文件名 
		if(filename!=null){
			//filename=filename.substring(filename.lastIndexOf(File.separator)+1);
			filename = FilenameUtils.getName(filename);//效果同上
		}
		//得到文件输入流
		try {
			InputStream inputStream = fileitem.getInputStream();
			//得到文件存盘目录
			String directoryRealPath = this.getServletContext().getRealPath("/WEB-INF/upload");
			File storeFile = new  File(directoryRealPath);
			if(!storeFile.exists()){
				storeFile.mkdirs();
			}
			String username ="文成";
			//按用户+日期分配目录
			String childDirectory = makeChildDirectory(storeFile,username);
			File file = new File(storeFile,childDirectory+File.separator+filename);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			int len=0;
			byte [] b = new byte[1024];
			while((len=inputStream.read(b))!=-1){
				fileOutputStream.write(b, 0, len);
			}
			inputStream.close();
			fileOutputStream.close();
			fileitem.delete();//上传完成后  删除临时文件
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private String makeChildDirectory(File storeFile, String username) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dataDirectory = username+File.separator+sdf.format(new Date());		
		File childDirectory = new File(storeFile,dataDirectory);
		if(!childDirectory.exists()){
			childDirectory.mkdirs();
		}
		return dataDirectory;
	}

	//普通表单
	private void processFormField(FileItem fileItem) {
		String fieldName = fileItem.getFieldName();
		String value = null;
		try {
			value = fileItem.getString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(fieldName+"="+value);
	}
	
	public void init() throws ServletException {
		// Put your code here
	}

}
