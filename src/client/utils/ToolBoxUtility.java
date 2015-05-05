/**
 *  @Author: 	Ryan K K Lam
 *  @Links: 	https://github.com/ryankklam
 *  @Email: 	ryan4299899@126.com
 *
 */
package client.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.filechooser.FileSystemView;

import client.controller.HttpProcessor;

/**
 * @ClassName: ToolBoxUtility
 * @Description: Toolbox Utility to provide base function
 * @date 2014-12-21
 */
public class ToolBoxUtility {
	
	private static String getIPURL = "http://www.ip138.com/ips1388.asp";

	public static String Md5Encryption(String plainText, int bit) {
		return Md5Encryption(plainText.getBytes(),bit);
	}
	
	public static String Md5Encryption(byte[] plainByte, int bit) {	
		
		String plainText = null;
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainByte);
			byte b[] = md.digest();


			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			if(bit == 32){
				plainText = buf.toString();
			}
			
			if(bit == 16){
				plainText = buf.toString().substring(8, 24);
			}
			

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return plainText;
	}
	
	 /** *//**文件重命名 
	    * @param path 文件目录 
	    * @param oldname  原来的文件名 
	    * @param newname 新文件名 
	    */ 
	public static void renameFile(String path, String oldname, String newname) {
		if (!oldname.equals(newname)) {// 新的文件名和以前文件名不同时,才有必要进行重命名
			File oldfile = new File(path + "/" + oldname);
			File newfile = new File(path + "/" + newname);
			if (!oldfile.exists()) {
				return;// 重命名文件不存在
			}
			if (newfile.exists())// 若在该目录下已经有一个文件和新文件名相同，则不允许重命名
				System.out.println(newname + "已经存在！");
			else {
				oldfile.renameTo(newfile);
			}
		} else {
			System.out.println("新文件名和旧文件名相同...");
		}
	}
	
    // 复制文件夹
    public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
        // 新建目标目录
        (new File(targetDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                String dir1 = sourceDir + "/" + file[i].getName();
                // 准备复制的目标文件夹
                String dir2 = targetDir + "/" + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }
    
    // 复制文件
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }
    
	public static void putStrToTXT(File outputTXT, String contentStr) {

		if (!outputTXT.getParentFile().exists()) {
			outputTXT.getParentFile().mkdirs();
		}

		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputTXT, true)));
//			bw = new BufferedWriter(new FileWriter(outputTXT));
			bw.newLine();
			bw.write(contentStr);

			if (bw != null) {
				bw.flush();
				bw.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public static String getExtensionName(String filename) {   
        if ((filename != null) && (filename.length() > 0)) {   
            int dot = filename.lastIndexOf('.');   
            if ((dot >-1) && (dot < (filename.length() - 1))) {   
                return filename.substring(dot + 1);   
            }   
        }   
        return filename;   
    } 
	
	public static boolean validateNdigits(String str , int n){
		Pattern pt = Pattern.compile("^\\d{" + n + "}$"); //验证n位的数字：^\d{n}$
		Matcher mt = pt.matcher(str);
		return mt.find();
	}
	
	public static String getWebIP(){
		
		String webIP = "";
		HttpProcessor processor = new HttpProcessor();

		try{
		String ipHtml = processor.getWithHost(getIPURL);
		Pattern p = Pattern.compile("\\[([^\\[\\]]+)\\]");
		Matcher m = p.matcher(ipHtml);
	      while(m.find()) {
	    	  webIP = m.group(1);
	         }

		}catch(Exception e){
			e.printStackTrace();
			webIP = "Error";
		}

		processor.client.getConnectionManager().closeIdleConnections(0,TimeUnit.SECONDS); //无用资源，释放链接
		return webIP;
	}
	
    /** 
     * 获得指定文件的byte数组 
     */  
    public static byte[] getFileBytes(String filePath){  
        byte[] buffer = null;  
        try {  
            File file = new File(filePath);  
            FileInputStream fis = new FileInputStream(file);  
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000];  
            int n;  
            while ((n = fis.read(b)) != -1) {  
                bos.write(b, 0, n);  
            }  
            fis.close();  
            bos.close();  
            buffer = bos.toByteArray();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return buffer;  
    } 
    
    public static int randomInt(int min,int max){
        Random random = new Random();
        return random.nextInt(max)%(max-min+1) + min;
    }
	
    public static boolean outputBytePic(byte[] byteCaptcha , String outpath){
		File captchaFile = null;
		
		try {

			if(outpath != ""){
				captchaFile = new File(outpath + "\\code.jpg");
			}else{
				captchaFile = new File(FileSystemView.getFileSystemView()
						.getHomeDirectory().getPath()
						+ "\\code.jpg");
			}

			FileOutputStream fos = new FileOutputStream(captchaFile);
			fos.write(byteCaptcha);
			fos.flush();
			fos.close();
			
			return true;
		} catch (Exception e) {
			System.err.println("验证码写入失败,请重新运行该程序");
			return false;
		}
    }
    
    public static String randomIntAString(int min,int max){
    	return String.valueOf(randomInt(min,max));
    }
}