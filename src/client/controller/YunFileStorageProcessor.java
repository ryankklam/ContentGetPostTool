/**
 *  @Author: 	Ryan K K Lam
 *  @Links: 	https://github.com/ryankklam
 *  @Email: 	ryan4299899@126.com
 *
 */
package client.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.config.Config;
import client.entity.WebStorageHandlingInterface;
import client.singleton.SessionFactorySingleton;
import client.utils.ToolBoxUtility;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mybatis.inter.YunFileInfoItemEntityMapper;
import com.mybatis.model.YunFileInfoItemEntity;

/**
 * @ClassName: YunFileStorageProcessor  
 * @Description: Web storage handler implementation for www.yunfile.com
 * @date 2016-1-9 
 */
public class YunFileStorageProcessor extends WebStorageHandlingProcessor implements WebStorageHandlingInterface{
	private static final Logger logger = LoggerFactory.getLogger(YunFileStorageProcessor.class);
	

	@SuppressWarnings("unchecked")
	public YunFileStorageProcessor() {
		super();
		@SuppressWarnings("rawtypes")
		Map headerMap = new HashMap();
		headerMap.put("Host", "www.yunfile.com");
		headerMap.put("Referer", "http://www.yunfile.com/");
		
		processor.setheaderMap(headerMap);
	}
	
	/* (non-Javadoc)
	 * @see client.entity.webStorageHandlingInterface#login()
	 */
	@Override
	public boolean login() {
		List<NameValuePair> loginParm = new LinkedList<NameValuePair>();
		
		loginParm.add(new BasicNameValuePair("LoginButton", "登录"));
		loginParm.add(new BasicNameValuePair("action", "validateLogin"));
		loginParm.add(new BasicNameValuePair("module", "member"));
		loginParm.add(new BasicNameValuePair("password", Config.YUNFILE_PWD));
		loginParm.add(new BasicNameValuePair("returnPath",""));
		loginParm.add(new BasicNameValuePair("username", Config.YUNFILE_USER));
		processor.postWithHost(Config.YUNFILE_LOGIN_URL, loginParm);
		
		return true;
		
	}
	

	public List<YunFileInfoItemEntity> getFolderDetailByPath(String folderPath) {
		
		List<YunFileInfoItemEntity> replyList = new ArrayList<YunFileInfoItemEntity>();
		
		List<NameValuePair> postParm = new LinkedList<NameValuePair>();
		postParm.add(new BasicNameValuePair("module", "fileService"));
		postParm.add(new BasicNameValuePair("action", "dir"));
		postParm.add(new BasicNameValuePair("path", folderPath));
		String replyJson = processor.postWithHost(Config.YUNFILE_VIEW_URL, postParm);

		JSONArray array = JSON.parseArray(replyJson);
		
		for(Object obj : array){
			JSONObject jobj = (JSONObject)obj;
			YunFileInfoItemEntity yunInfo = (YunFileInfoItemEntity)JSONObject.toJavaObject(jobj, YunFileInfoItemEntity.class);
			String itemCode = yunInfo.getDownLink();
			itemCode = itemCode.replaceAll("http://dix3.com/fs/", "");
			itemCode = itemCode.replaceAll("/", "");
			
			yunInfo.setDownLink(itemCode);
			replyList.add(yunInfo);
		}

		return replyList;	
	}

	/* (non-Javadoc)
	 * @see client.entity.WebStorageHandlingInterface#upload()
	 */
	@Override
	public void upload() {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see client.entity.WebStorageHandlingInterface#selectPendingPostItem()
	 */
	@Override
	public List selectPendingPostItem() {
		YunFileInfoItemEntityMapper yfiMapper = session.getMapper(YunFileInfoItemEntityMapper.class);
		return yfiMapper.selectPendingPostItem();
	}

	/* (non-Javadoc)
	 * @see client.entity.WebStorageHandlingInterface#checkAndUpdateOutstandingItem()
	 */
	public void checkAndUpdateOutstandingItem() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see client.entity.WebStorageHandlingInterface#getOutstandingItemCount()
	 */
	@Override
	public int getOutstandingItemCount() throws IOException {
		SqlSession refreshSession = SessionFactorySingleton.getInstance().getNewOpenSession();
		YunFileInfoItemEntityMapper yfiMapper = session.getMapper(YunFileInfoItemEntityMapper.class);
		int count = yfiMapper.selectPendingPostItemCount();
		logger.info(ToolBoxUtility.getCurrentTime() + " - OutstandingCount = " + count);
		refreshSession.close();
		return count;
	}
	
	public void updateYunFileTempQueueFile() throws IOException{
		List<YunFileInfoItemEntity> itemList = getFolderDetailByPath("/Share/forjav");
		
		YunFileInfoItemEntityMapper yunFileItemMapper = SessionFactorySingleton.getInstance().getOpenSession().getMapper(YunFileInfoItemEntityMapper.class);
		for(YunFileInfoItemEntity record : itemList){
			try{
				yunFileItemMapper.insert(record);
			}catch(Exception e){
//				e.printStackTrace();			
			}

		}
		SessionFactorySingleton.getInstance().getOpenSession().commit();
		
		String localPath = Config.PENDING_UPLOAD_YUNFILE_PATH;
		boolean movefolderFlag = !hasPendingUpload(localPath);
		
		logger.info(Thread.currentThread().getStackTrace()[1].getMethodName() + " Completed~~~");
	}
	
	//Check Pending Upload
	//Get list from local
	public static boolean hasPendingUpload(String localPath) throws IOException{
		
		boolean pendFlag = false;
		
		YunFileInfoItemEntityMapper mapper = SessionFactorySingleton.getInstance().getOpenSession().getMapper(YunFileInfoItemEntityMapper.class);
		
    	File testDataDir = new File(localPath);
    	if(testDataDir.exists()){
    		for (File file : testDataDir.listFiles()) {
    			
    			if(!file.isDirectory()){
    				String extension = "." + ToolBoxUtility.getExtensionName(file.getName());
    				String name = file.getName().replaceAll(extension, "");
    				
    				
    				List<YunFileInfoItemEntity> fileList = new ArrayList<YunFileInfoItemEntity>();
    				fileList = mapper.selectByObjectName(name);
    				
    				if(fileList.size()==0){
    					logger.info("Pending Upload : " + name);
    					pendFlag = true;
    				}else{
    					ToolBoxUtility.deleteFile(file.getPath());//clean up file as it's already uploaded.
    				}
    			}
    		}
    	}

		return pendFlag;
	}
	

}
