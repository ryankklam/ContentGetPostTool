/**
 *  @Author: 	Ryan K K Lam
 *  @Links: 	https://github.com/ryankklam
 *  @Email: 	ryan4299899@126.com
 *
 */
package client.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.ibatis.session.SqlSession;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uuid.GlobalSequenceIdProvider;
import client.config.Config;
import client.entity.MessageDefinePool;
import client.singleton.JavCaptchaMapCacheManager;
import client.singleton.SessionFactorySingleton;
import client.utils.HtmlUtils;
import client.utils.ToolBoxUtility;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mybatis.inter.JavPostStatusMapper;
import com.mybatis.inter.ResourceSearchRequestFileMapper;
import com.mybatis.model.JavPostInfo;
import com.mybatis.model.JavPostStatus;
import com.mybatis.model.ResourceSearchRequestFile;

/**
 * @ClassName: JavPostProcessor  
 * @Description: To automatic execute process for http://www.javlib.com
 * @date 2015-2-27 
 */
public class JavPostProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(JavPostProcessor.class);
	SqlSession session;
	HttpProcessor processor;
	Map<String, String> captchaSingleton;
	
	//URL link
	//Moved to Config file 
	
	//variable
	String page = "page=";
	String listString = "识别码搜寻结果";
	String timeOut522 = "522:";
	String unknown520 = "520:";

	String REQUESTFILESTATUS_COMPLETED = "Completed";
	
	public JavPostProcessor() throws IOException {
		
		processor = new HttpProcessor();
		
		session = SessionFactorySingleton.getInstance().getOpenSession();
		
		captchaSingleton = JavCaptchaMapCacheManager.getInstance().getMapCache();
		
	}


	protected List<JavPostInfo> getMostWanted() throws ParseException, IOException{
		
		List<JavPostInfo> javPostInfoItemList = new ArrayList<JavPostInfo>();
		
		String replyHtml = processor.getWithHost(Config.JAVLIB_MOSTWANTALL_URL);
		
		Document docPages = Jsoup.parse(replyHtml);

		String pagelast = docPages.select("a.page.last").first().attr("href"); // <a class="page last" href="/...
		String regx = "page=(\\d*.)"; //末尾加. 贪婪匹配		
		String[] pageLastStr = HtmlUtils.getMatcherValue(pagelast, regx);
		int pageLastInt =Integer.parseInt(pageLastStr[0]);
		
		for(int currentPage=1;currentPage<=pageLastInt;currentPage++){
			String templink =  Config.JAVLIB_MOSTWANTALL_URL + page + currentPage;
			
			replyHtml = processor.getWithHost(templink);
			docPages = Jsoup.parse(replyHtml);
			Elements postList = docPages.select("div.video"); //li element
			
			if (postList.size() != 0) { //
				for (int j = 0; j < postList.size(); j++) {
					Element postItem = postList.get(j);
					String postTitle = postItem.getElementsByTag("a").attr("title");
					String postLink = postItem.getElementsByTag("a").attr("id");
					String postId = postTitle.split(" ")[0];
					
					JavPostInfo item = new JavPostInfo();
					item.setJavpostid(postId);
					item.setJavpostlink(postLink);
					item.setJavposttitle(postTitle);

					javPostInfoItemList.add(item);
					logger.info(postLink + " - " + postTitle );	
				}
			}
		}
		
		logger.info("Total count to process : " + javPostInfoItemList.size());
		return javPostInfoItemList;
	}
	
	public void recoverPostJavByMapping(Map<String,String> processMap) throws ParseException, IOException, InterruptedException{
		
		if(JavLogin()){
			
			for (String key : processMap.keySet()) {
				String resourceName = key;
			    String downloadLink = processMap.get(key);
				
				postJavByFanhao(resourceName, downloadLink);
			}

			logger.info("recoverPostJavByMapping Completed!");	
		}
	}
	
	public String assignProcessSequence(String prefix){
		
		String sequence = "";
		
		ResourceSearchRequestFileMapper rsrfm = session.getMapper(ResourceSearchRequestFileMapper.class);
		
		ResourceSearchRequestFile searchRequestItem = rsrfm.selectLatestProcessRecordByPrefix(prefix);
		
		if(searchRequestItem!=null && !searchRequestItem.getRequestprocessstatus().equalsIgnoreCase(REQUESTFILESTATUS_COMPLETED)){
			sequence = searchRequestItem.getProcesssequence();
		}else{
			long l = GlobalSequenceIdProvider.nextVal();
			sequence = String.valueOf(l);
		}

		return sequence;
	}
	
	public String getJavCaptcha(){
		long time = System.currentTimeMillis();
		String captchaCode = String.valueOf(time / 1000);
		byte[] byteCaptcha = processor.getByteCode(Config.JAVLIB_CAPTCHA_URL, captchaCode);

		String md5Captcha = ToolBoxUtility.Md5Encryption(byteCaptcha, 32);
		String confirmType = null;
		if (captchaSingleton.get(md5Captcha) == null) {
			logger.info("Captcha not in database!!!");
			processor.outputCaptchaFromByte(byteCaptcha);
		} else {
			confirmType = captchaSingleton.get(md5Captcha);
		}
		
		return confirmType;
	}
	
	public boolean JavLogin(){
		
		String confirmType = getJavCaptcha();
		
		List<NameValuePair> loginParm = new LinkedList<NameValuePair>();
		loginParm.add(new BasicNameValuePair("userid", Config.JAVUSER));
		loginParm.add(new BasicNameValuePair("securehash",Config.JAVPWD_HASH));
		loginParm.add(new BasicNameValuePair("rememberme", "0"));
		loginParm.add(new BasicNameValuePair("confirmobj", confirmType));
		loginParm.add(new BasicNameValuePair("IP_ADDRESS", ToolBoxUtility.getWebIP()));

		Map tempMap = processor.getCookieMap();
		tempMap.put("over18", "18");
		processor.setCookieMap(tempMap);
		String replyJson = processor.postWithHost(Config.JAVLIB_LOGIN_URL, loginParm);
		
		if(replyJson!=null){
			JSONObject result = JSON.parseObject(replyJson);
			if(result.getIntValue("ERROR") == 1){
				logger.info("Javlib 登陆成功！");
				return true;
			}
		}
	
		return false;
	}
	
	public void deleteYIMUHEFiles() throws ParseException, IOException{
		JavPostStatusMapper jpsm = session.getMapper(JavPostStatusMapper.class);
		List<JavPostStatus> itemList = jpsm.selectByStorageDest("YIMUHE");
		Iterator<JavPostStatus> it = itemList.iterator();
		while(it.hasNext()){
			deleteCommentByJavPostStatusItem(it.next());
		}
		
	}
	
	public boolean deleteCommentByJavPostStatusItem(JavPostStatus item) throws ParseException, IOException{
		
		logger.info("Start deleteCommentByJavPostStatusItem : " + item.getJavpostlink());	
		
		JavPostStatusMapper jpsm = session.getMapper(JavPostStatusMapper.class);
		
		String javFullLink = Config.JAVLIB_MAIN_URL + "?v=" + item.getJavpostlink();
		String ajaxId = "";
		String replyHtml = "";
		
		while(ajaxId.equalsIgnoreCase("")){
			replyHtml = processor.getWithHost(javFullLink);
			ajaxId = HtmlUtils.getVar("\\$ajaxid", replyHtml);
		}
		
			String commentIdStr = "comment_" + item.getJavpostid();
			
			List<NameValuePair> deleteParm = new LinkedList<NameValuePair>();
			deleteParm.add(new BasicNameValuePair("lang", "cn"));
			deleteParm.add(new BasicNameValuePair("videoid", ajaxId));
			deleteParm.add(new BasicNameValuePair("targetid", commentIdStr));
			deleteParm.add(new BasicNameValuePair("type", "3"));

			String replyJson = processor.postWithHost(Config.JAVLIB_COMMENTDELETE_URL, deleteParm);
			
			if(replyJson != null && !replyJson.equalsIgnoreCase("")){
//				continue;

			JSONObject result = JSON.parseObject(replyJson);
			
			if(result.getIntValue("ERROR") == 1){
//				postResult = true;
				logger.info(item.getJavpostlink()+" delete成功！");
				
				jpsm.deleteByPrimaryKey(item.getJavpostlink());

				session.commit();
				
				return true;
				}
			}

		return false;
	}
	
	public List<JavPostInfo> getPostInfoByPrefix(String prefix) throws ParseException, IOException{
		
		logger.info("Search Prefix : " + prefix);	
		
		//set process sequence
		String processSequence="0";
		ResourceSearchRequestFileMapper rsrfm = session.getMapper(ResourceSearchRequestFileMapper.class);
		if(rsrfm.CheckOutstandingProcessStatusCount()>0){
			ResourceSearchRequestFile resource = rsrfm.selectLatestOutstandingProcessRecord();
			processSequence = resource.getProcesssequence();
		}else{
			processSequence = String.valueOf(GlobalSequenceIdProvider.nextVal());
		}
		
		List<JavPostInfo> javPostInfoItemList = new ArrayList<JavPostInfo>();
		
		List<NameValuePair> parameters = new LinkedList<NameValuePair>();
		parameters.add(new BasicNameValuePair("keyword", prefix));
		
		String javSerachPrefix = Config.JAVLIB_SEARCH_URL + "list&"+ URLEncodedUtils.format(parameters, HTTP.UTF_8);
		String replyHtml = processor.getWithHost(javSerachPrefix);
		
		
		Document docPages = Jsoup.parse(replyHtml);
		String pagelast = docPages.select("a.page.last").first().attr("href"); // <a class="page last" href="/...
		String regx = "page=(\\d*.)"; //末尾加. 贪婪匹配		
		String[] pageLastStr = HtmlUtils.getMatcherValue(pagelast, regx);
		int pageLastInt =Integer.parseInt(pageLastStr[0]);
		
		for(int currentPage=1;currentPage<=pageLastInt;currentPage++){
			String templink = Config.JAVLIB_SEARCH_URL + URLEncodedUtils.format(parameters, HTTP.UTF_8) + "&" + page + currentPage; 
//			String templink =  javSerachPrefix + "&" + page + currentPage;
			
			replyHtml = processor.getWithHost(templink);
			docPages = Jsoup.parse(replyHtml);
			Elements postList = docPages.select("div.video"); //li element
			
			if (postList.size() != 0) { //
				for (int j = 0; j < postList.size(); j++) {
					Element postItem = postList.get(j);
					String postTitle = postItem.getElementsByTag("a").attr("title");
					String postLink = postItem.getElementsByTag("a").attr("id");
					String postId = postTitle.split(" ")[0];
					
					JavPostInfo item = new JavPostInfo();
					item.setJavpostid(postId);
					item.setJavpostlink(postLink);
					item.setJavposttitle(postTitle);

					javPostInfoItemList.add(item);
					logger.info(postLink + " - " + postTitle );	
				}
			}
		}
		
		logger.info("Total count to process : " + javPostInfoItemList.size());
		return javPostInfoItemList;
	}
	
	public boolean postJavByFanhao(String fanhao , String downloadLink) throws InterruptedException, ParseException, IOException{
		logger.info(fanhao);	
		
		String javPostText ="[url=" + downloadLink + "][b][color=#ff33ff]无需等待[/color][color=#0000ff]===>点击下载种子<===[/color][color=#00cc00]立即下载 [/color]\n[url=http://www.javlibrary.com/cn/userposts.php?u=ryan4299899]==========更多作品请点击============[/url]";
		
		//=======================================================================		
		JavPostStatusMapper jpsm = session.getMapper(JavPostStatusMapper.class);
		
		List<NameValuePair> parameters = new LinkedList<NameValuePair>();
		parameters.add(new BasicNameValuePair("keyword", fanhao));
		
		String javSerachFanhao = Config.JAVLIB_SEARCH_URL + URLEncodedUtils.format(parameters, HTTP.UTF_8);
		
		String replyHtml = "";
		while(replyHtml.equalsIgnoreCase("") || replyHtml.contains(timeOut522) || replyHtml.contains(unknown520)){
			replyHtml = processor.getWithHost(javSerachFanhao);
		}
		
		//check duplicate result (一个番号可能有多个result)
		if(!replyHtml.contains(listString)){
			
			String javPostLink = HtmlUtils.getVar("\\$videoid", replyHtml);
			String ajaxid = HtmlUtils.getVar("\\$ajaxid", replyHtml);
			//==================================================================
			if(jpsm.selectByPrimaryKey(javPostLink)==null){
				//captcha For post confirm object
				
				int postRetryCount = 0;
				
				boolean postResult = false;
				while(!postResult && postRetryCount <= Config.JAVPOST_RETRY){
					
					String confirmType = getJavCaptcha();
					
					List<NameValuePair> postParm = new LinkedList<NameValuePair>();
					postParm.add(new BasicNameValuePair("confirmobj", confirmType));
					postParm.add(new BasicNameValuePair("lang", "cn"));
					postParm.add(new BasicNameValuePair("text", javPostText));
					postParm.add(new BasicNameValuePair("targetid", ajaxid));
					
					String replyJson = processor.postWithHost(Config.JAVLIB_POST_URL, postParm);
					
					if(replyJson == null || replyJson.equalsIgnoreCase("")){
						continue;
					}
					
					JSONObject result = JSON.parseObject(replyJson);
					
					if(result.getIntValue("ERROR") == -6){
						replyJson = processor.postWithHost(Config.JAVLIB_POST_URL, postParm);
						result = JSON.parseObject(replyJson);
					}

					if(result == null) continue;
					
					if(result.getIntValue("ERROR") == 1){
						postResult = true;
						logger.info(fanhao +" Post成功！");
						
						JavPostStatus jps = new JavPostStatus();
						jps.setJavpostid(result.getString("ID"));
						jps.setJavpostlink(javPostLink);
						jps.setJavpoststatus("CP");
						jps.setJavpoststoragedest("COLAFILE");
						jps.setJavpoststoragelink(downloadLink);
						
						jpsm.insert(jps);
						session.commit();
						
						return true;
					}
					postRetryCount++;
					Thread.sleep(3000);
				}
			}
		}else{
			logger.info(fanhao + MessageDefinePool.ERR0001);
		}

		return false;
	}

}
