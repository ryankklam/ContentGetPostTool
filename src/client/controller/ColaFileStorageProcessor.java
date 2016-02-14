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
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.ibatis.session.SqlSession;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.config.Config;
import client.entity.WebStorageHandlingInterface;
import client.singleton.SessionFactorySingleton;
import client.utils.HtmlUtils;
import client.utils.ToolBoxUtility;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mybatis.inter.ColafileResourcesMapper;
import com.mybatis.model.ColafileResources;
import com.mybatis.model.YunFileInfoItemEntity;

/**
 * @ClassName: ColaFileStorageProcessor  
 * @Description: Implementation of www.coladrive.com (Rewrite from ColaFileProcessor)
 * @date 2016-1-25 
 */
public class ColaFileStorageProcessor extends WebStorageHandlingProcessor implements WebStorageHandlingInterface{
	
	private static final Logger logger = LoggerFactory.getLogger(ColaFileStorageProcessor.class);
	
	private static final String lastPageCHN = "最后一页";
	private static final String nextPageCHN = "下一页";
	private static final String backToUpLevel = "返回上层文件夹";
	private static final String STORAGE_DEST = "COLAFILE";
	
	private static final String internalServerError520 = "500 Internal Server Error";
	private static final String BadGateway502 = "502 Bad Gateway";
	
	private static final String tempFolderId = "60736";
	private static final String sthFolderId = "60701";
	private static final String tempSoloFolderId = "60738";
	
	//Regex expression
	private String getPageNum =",(\\d.*)\\);";
	private String CALLFILEPAGE = "javascript:callfilepage\\((\\d.*)\\);";
	private String OPENFOLDER = "javascript:openfolder\\((\\d.*),";
	

	public ColaFileStorageProcessor() {
		super();
		Map headerMap = new HashMap();		
		processor.setheaderMap(headerMap);
	}

	/* (non-Javadoc)
	 * @see client.entity.webStorageHandlingInterface#login()
	 */
	@Override
	public boolean login() {
		List<NameValuePair> loginParm = new LinkedList<NameValuePair>();
		loginParm.add(new BasicNameValuePair("action", "login"));
		loginParm.add(new BasicNameValuePair("formhash", "95d3049b"));		
		loginParm.add(new BasicNameValuePair("password", Config.COLAFILE_PWD));
		loginParm.add(new BasicNameValuePair("ref","/mydisk.php"));
		loginParm.add(new BasicNameValuePair("task", "login"));
		loginParm.add(new BasicNameValuePair("username", Config.COLAFILE_USER));
		try {
			loginParm.add(new BasicNameValuePair("verycode", captchaCode(Config.COLAFILE_CAPTCHA_URL)));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
		
		String reply = processor.postWithHost(Config.COLAFILE_LOGIN_URL, loginParm);
		return true;
	}
	
	public Map<String, String> getFolderDetailById(String folderId){
		String respHtml = null;
		
		while(respHtml==null || respHtml.equalsIgnoreCase("")){
			respHtml = getSpaceFileFolderDetail(folderId,"1");
		}
			
		return	getSubFolderMapByRespHtml(respHtml);
	}
	
	public String getSpaceFileFolderDetail(String folderId , String pageNum){
		List<NameValuePair> spaceMainParm = new LinkedList<NameValuePair>();
		spaceMainParm.add(new BasicNameValuePair("action", "space_files_list"));
		spaceMainParm.add(new BasicNameValuePair("folder_id", folderId));
		spaceMainParm.add(new BasicNameValuePair("item", "ajax"));
		spaceMainParm.add(new BasicNameValuePair("pg",pageNum));
		
		String replyHtml = null;
		
		try {
			while(replyHtml==null){
				replyHtml = processor.getWithHostAndParm(Config.COLAFILE_SPACE_FILE_URL, spaceMainParm);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return replyHtml;
	}
	
	public Map<String,String> getSubFolderMapByRespHtml(String respHtml){
		
		Map<String,String> subFolderMap = new HashMap<String,String>();
		
		Document docPages = Jsoup.parse(respHtml);
		Elements folderList = docPages.select("div.pull-left"); // / div with
		
		for (Element item : folderList) {
				String temp = item.getElementsByTag("a").attr("href");
				String folderId = HtmlUtils.getMatcherValue(temp, this.OPENFOLDER)[0];
				String folderName =  item.getElementsByTag("a").text();
				if(!folderName.equalsIgnoreCase(backToUpLevel)){
					subFolderMap.put(folderId,folderName);
				}
		}
		
		return subFolderMap;
	}
	
	public Map<String,ColafileResources> getColafileItembyFolderId(String folderName,String targetFolderId) throws ParseException, IOException{
		
		Map<String,ColafileResources> itemMap = new HashMap<String, ColafileResources>();
		
		String respHtml = null;
		String folderId = targetFolderId;
		
		respHtml = null;
		while(respHtml==null || respHtml.equalsIgnoreCase("")){
			respHtml = getSpaceFileFolderDetail(folderId,"1");
		}
		Document docPages = Jsoup.parse(respHtml);
		Elements pageList = docPages.select("div.pagination ul li"); // / div with
		
		//to get total page
		int totalPage = 0;
		
		//issue#3 start
		if(pageList.size()==0){
			totalPage = 1;
		}else{
			Element page = pageList.get(pageList.size()-1);
			if(page.text().equalsIgnoreCase(lastPageCHN)){
				totalPage =  Integer.parseInt(HtmlUtils.getMatcherValue(page.getElementsByTag("a").first().attr("href").toString(), getPageNum)[0]);			
			}else{
				if(page.text().equalsIgnoreCase(nextPageCHN)){
					page = pageList.get(pageList.size()-2);
					totalPage = Integer.parseInt(page.text());
				}
			}
		}
		//issue#3 end

		for(int currentPage = 1; currentPage<=totalPage ; currentPage++){
			
			logger.info(folderName + " currentPage:" + currentPage );
			String htmlReply = null;
			while(htmlReply==null || htmlReply.equalsIgnoreCase("")||htmlReply.contains(internalServerError520)
					||htmlReply.contains(BadGateway502)){
				htmlReply = getSpaceFileFolderDetail(folderId, String.valueOf(currentPage));
				
			}

			docPages = Jsoup.parse(htmlReply);
			Elements items = docPages.select("div.pull-left");
			
			int getCount = 0;
			for(int i=1;i<items.size();i++){
				Element item = items.get(i).getElementsByTag("a").first();
				
				String extension = "." + ToolBoxUtility.getExtensionName(item.attr("title"));
				String title = item.attr("title").replaceAll(extension, "");
				String fileId = HtmlUtils.getMatcherValue(item.attr("href"), this.CALLFILEPAGE)[0];
				
				ColafileResources colaItem = new ColafileResources();
				colaItem.setStoragedest(STORAGE_DEST);
				colaItem.setParentid(Config.COLAFILE_STH_FID);
				colaItem.setSelffolderid(folderId);
				colaItem.setSelffoldername(folderName);
				colaItem.setObjectlink(fileId);
				colaItem.setObjectname(title);
				
				itemMap.put(title, colaItem);
				getCount++;
			}
			
			if(getCount != 10 && totalPage != currentPage){
				logger.info("Post Not Count!!!");
				throw new RuntimeException("Post Not Count Exception!!!");
			}
		}

		return itemMap;
	}
	
	public boolean moveFolder(String destFolderId,String selfFolderId){
		List<NameValuePair> moveFolderParm = new LinkedList<NameValuePair>();
		moveFolderParm.add(new BasicNameValuePair("action", "folder_move"));
		moveFolderParm.add(new BasicNameValuePair("dest_fid", destFolderId));
		moveFolderParm.add(new BasicNameValuePair("folder_id", selfFolderId));
		moveFolderParm.add(new BasicNameValuePair("formhash", "2cf49625"));
		moveFolderParm.add(new BasicNameValuePair("task","move_folder"));
		
		String replyHtml = null;
		
		while(replyHtml==null){
			try {
				replyHtml = processor.postWithHost(Config.COLAFILE_SPACE_FILE_AJAX_URL, moveFolderParm);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		return Boolean.parseBoolean(replyHtml);

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
		ColafileResourcesMapper colaMapper = session.getMapper(ColafileResourcesMapper.class);
		return colaMapper.selectPendingPostItem();
	}

	/* (non-Javadoc)
	 * @see client.entity.WebStorageHandlingInterface#getOutstandingItemCount()
	 */
	@Override
	public int getOutstandingItemCount() throws IOException {
		SqlSession refreshSession = SessionFactorySingleton.getInstance().getNewOpenSession();
		ColafileResourcesMapper colaMapper = refreshSession.getMapper(ColafileResourcesMapper.class);
		int count = colaMapper.selectPendingPostItemCount();
		logger.info(ToolBoxUtility.getCurrentTime() + " - OutstandingCount = " + count);
		refreshSession.close();
		return count;
	}
	
	public void updatecolaFileFolderFromTempQueue() throws IOException, InterruptedException{
		
		ColafileResourcesMapper colaMapper = SessionFactorySingleton.getInstance().getOpenSession().getMapper(ColafileResourcesMapper.class);
		Map<String, String> tempQueueMap =  getFolderDetailById(tempFolderId);
		
		for (Entry<String, String> queueItem : tempQueueMap.entrySet()) {
			
			boolean movefolderFlag = true;
			
			String folderId = queueItem.getKey();
			String folderName = queueItem.getValue();
			
			Map<String,ColafileResources> resourceMap = getColafileItembyFolderId(folderName,folderId);
			
			if(resourceMap.size()==0){
				logger.info("folder " + folderName + " is null , pls further check!!!~");
				break;
			}
			
			for (Entry<String, ColafileResources> key : resourceMap.entrySet()) {

				ColafileResources rsValue = key.getValue();				
				if(colaMapper.selectByPrimaryKey(rsValue.getObjectlink())==null){
					colaMapper.insert(rsValue);
				}
			}
			SessionFactorySingleton.getInstance().getOpenSession().commit();
			
			//Check Pending Upload
			//Get list from local
//			String localPath = Config.CAPTCHA_TMP_PATH+"\\" + folderName;
			String localPath = Config.PENDING_UPLOAD_PATH +"\\" + folderName;
			movefolderFlag = !hasPendingUpload(localPath);
			
			if(movefolderFlag){
				//move folder from temp to sth
				boolean movedone = moveFolder(sthFolderId, folderId);
				logger.info("Move folder from temp to sth : " + movedone);
				
				if(movedone){
					boolean result = ToolBoxUtility.deleteFolder(localPath);  
					}
//				}
			}
			
			logger.info("updatecolaFileFolder (" + folderName + ") Completed~~~");
		}
		
		logger.info(Thread.currentThread().getStackTrace()[1].getMethodName() + " Completed~~~");
	}
	
	//Check Pending Upload
	//Get list from local
	public static boolean hasPendingUpload(String localPath) throws IOException{
		
		boolean pendFlag = false;
		
		ColafileResourcesMapper colaMapper = SessionFactorySingleton.getInstance().getOpenSession().getMapper(ColafileResourcesMapper.class);
		
    	File testDataDir = new File(localPath);
    	if(testDataDir.exists()){
    		for (File file : testDataDir.listFiles()) {
    			
    			if(!file.isDirectory()){
    				String extension = "." + ToolBoxUtility.getExtensionName(file.getName());
    				String name = file.getName().replaceAll(extension, "");
    				
    				if(name.contains("69")){
    					logger.info(file.getName() + " was ignored due to colafile checking!!!");
    					
		                // 目标文件
		                File targetFile = new File(Config.PENDING_UPLOAD_YUNFILE_PATH + File.separator + file.getName());
		                ToolBoxUtility.copyFile(file, targetFile);
    					ToolBoxUtility.deleteFile(file.getPath());
    					continue;
    				}		
    				
    				List<ColafileResources> colaList = new ArrayList<ColafileResources>();
    				colaList = colaMapper.selectByObjectName(name);
    				
    				if(colaList.size()==0){
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
