/**
 *  @Author: 	Ryan K K Lam
 *  @Links: 	https://github.com/ryankklam
 *  @Email: 	ryan4299899@126.com
 *
 */
package client.controller;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.config.Config;
import client.entity.WebStorageHandlingInterface;
import client.singleton.SessionFactorySingleton;
import client.utils.ImageUtils;
import client.utils.ToolBoxUtility;

/**
 * @ClassName: webStoragehandlingProcessor  
 * @Description: TODO(Override class description here)  
 * @date 2016-1-9 
 */
public abstract class WebStorageHandlingProcessor implements WebStorageHandlingInterface{
	
	private static final Logger logger = LoggerFactory.getLogger(WebStorageHandlingProcessor.class);

	HttpProcessor processor;
	SqlSession session;
	public WebStorageHandlingProcessor() {
		super();
		processor = new HttpProcessor();
		try {
			session = SessionFactorySingleton.getInstance().getOpenSession();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public String captchaCode(String captchaUrl) throws IOException, InterruptedException {
		
		String captchaCode = Double.toString(Math.random());
		captchaCode = "t=" + captchaCode; 
		
		byte[] byteCaptcha = processor.getByteCode(captchaUrl, captchaCode);

		ToolBoxUtility.outputBytePic(byteCaptcha, Config.CAPTCHA_TMP_PATH);

		File codeFile = new File(Config.CAPTCHA_TMP_PATH + "\\code.jpg");
		
		if(Config.COLAFILE_AUTO_CAPTCHA){
			ImageUtils imgUtils = new ImageUtils(Config.TESSERACT_PATH);
			ImageUtils.cleanImage(codeFile, Config.CAPTCHA_TMP_PATH);
			return imgUtils.recognizeText(codeFile);
		}else{
			return getLine("请输入验证码:");
		}
	}
	
	/**
	 * 读取用户输入
	 * @param title
	 * @return 内容
	 */
	private String getLine(String title) {
		Scanner scanner = new Scanner(System.in);
		if(null != title)
			System.out.print(title);
		String line = scanner.nextLine();
		return line;
	}
	
}
