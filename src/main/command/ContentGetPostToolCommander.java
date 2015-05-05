/**
 *  @Author: 	Ryan K K Lam
 *  @Links: 	https://github.com/ryankklam
 *  @Email: 	ryan4299899@126.com
 *
 */
package main.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uuid.GlobalSequenceIdProvider;
import client.config.Config;
import client.controller.JavPostProcessor;
import client.controller.MagnetConvertProcessor;
import client.entity.MessageDefinePool;
import client.singleton.SessionFactorySingleton;
import client.utils.ToolBoxUtility;

import client.controller.ColaFileProcessor;
import com.mybatis.inter.ResourceSearchRequestDetailFileMapper;
import com.mybatis.inter.ResourceSearchRequestFileMapper;
import com.mybatis.model.JavPostInfo;
import com.mybatis.model.MagnetInfo;
import com.mybatis.model.ResourceSearchRequestDetailFile;
import com.mybatis.model.ResourceSearchRequestDetailFileKey;
import com.mybatis.model.ResourceSearchRequestFile;

/**
 * @ClassName: ContentGetPostToolCommander  
 * @Description: Main method & Error handling
 * @date 2015-4-22 
 */
public class ContentGetPostToolCommander {
	
	private static final Logger logger = LoggerFactory.getLogger(ContentGetPostToolCommander.class);
	
	public static void main(String[] args) throws Exception{
		
		int index = 3;
		String prefix = "JUSD";
		
		switch(index){
		
			case 1:  downloadResourceByPrefix(prefix); break;// download resource
			case 2:  checkOutstandingUpload(prefix); break;//check outstanding
			case 3:  PostJavByColaFile(prefix);		break;//post jav
			
			default: logger.info("Goto dummy default method!!!");
		}
		
		
	}
	
	public static void downloadResourceByPrefix(String prefix) throws Exception {
		
		int stage = 1;
		
//		String prefix = "SHKD";
//		String prefix = "NASS";
//		String prefix = "MILF";
//		String prefix = "SCOP";
//		String prefix = "BMD";
		
		
//		String prefix = "UPSM";
//		String prefix = "TYOD";
//		String prefix = "FSET";
//		String prefix = "IPTD";
//		String prefix = "HND";
//		String prefix = "ATFB";
//		String prefix = "KTDS";
//		String prefix = "GAS";
//		String prefix = "BOMN";
//		String prefix = "RKI";
//		String prefix = "ONSD";
//		String prefix = "VENU";
//		String prefix = "MVSD";
//		String prefix = "IPZ";
//		String prefix = "ABP";
//		String prefix = "SDMU";
//		String prefix = "GVG";
//		String prefix = "SDDE";
//		String prefix = "IESP";
//		String prefix = "RBD";
//		String prefix = "PPPD";
//		String prefix = "OKSN";
//		String prefix = "SAMA";
//		String prefix = "JUSD";
//		String prefix = "JUX";
//		String prefix = "DANDY";
//		String prefix = "EKDV";
//		String prefix = "SNIS";
//		String prefix = "WANZ";
//		String prefix = "AUKG";
//		String prefix = "MXGS";
//		String prefix = "DASD";
//		String prefix = "EBOD";
//		String prefix = "MIAD";
		//WANZ
		
		
		//varibale
		ResourceSearchRequestDetailFileMapper resRqDtlFM = SessionFactorySingleton.getInstance().getOpenSession().getMapper(ResourceSearchRequestDetailFileMapper.class);
		ResourceSearchRequestFileMapper rsrfm = SessionFactorySingleton.getInstance().getOpenSession().getMapper(ResourceSearchRequestFileMapper.class);
		
		JavPostProcessor jpsr = new JavPostProcessor();
		MagnetConvertProcessor tcProcessor = new MagnetConvertProcessor();
		
		//Stage 1 : search & download content
		String processSequence = "";	
		
		if(stage == 1) {
			
//			MagnetConvertProcessor tcProcessor = new MagnetConvertProcessor();
//			
			ResourceSearchRequestFile reqItem = rsrfm.selectLatestProcessRecordByPrefix(prefix);

			if(reqItem==null){
				
				ResourceSearchRequestFile tempItem = new  ResourceSearchRequestFile();
				long l = GlobalSequenceIdProvider.nextVal();
				processSequence = String.valueOf(l);
				tempItem.setProcesssequence(processSequence);
				tempItem.setRequestprocessstatus("Pending");
				tempItem.setSearchkeyword(prefix);
				rsrfm.insert(tempItem);
				logger.info("New Sequence: " + processSequence);
				
				//rebuild pending list
				List<JavPostInfo> javList = jpsr.getPostInfoByPrefix(prefix);
				Iterator<JavPostInfo>  its = javList.iterator();
						
//				ResourceSearchRequestDetailFileMapper resRqDtlFM = SessionFactorySingleton.getInstance().getOpenSession().getMapper(ResourceSearchRequestDetailFileMapper.class);
				
				//build download request list
				while(its.hasNext()){
					
					JavPostInfo javItem = its.next();
					
					ResourceSearchRequestDetailFileKey detailKey = new ResourceSearchRequestDetailFileKey();
					detailKey.setProcesssequence(processSequence);
					detailKey.setSearchkeyword(javItem.getJavpostid());
					
					if(resRqDtlFM.selectByPrimaryKey(detailKey)==null){
						ResourceSearchRequestDetailFile detailFile = new ResourceSearchRequestDetailFile();
						detailFile.setProcesssequence(processSequence);
						detailFile.setRequestprocessstatus("Pending");
						detailFile.setSearchkeyword(javItem.getJavpostid());
						resRqDtlFM.insert(detailFile);
					}
				}
				
			}else{
				processSequence = reqItem.getProcesssequence();
				logger.info("Existing Sequence: " + processSequence);
			}
			
			SessionFactorySingleton.getInstance().getOpenSession().commit();
			stage++;
		}
		
		//step 2 , serach fanhao & get torrent/magnet
		if(stage == 2) {
			//get pending item list
//			ResourceSearchRequestDetailFileMapper resRqDtlFM = SessionFactorySingleton.getInstance().getOpenSession().getMapper(ResourceSearchRequestDetailFileMapper.class);
			List<ResourceSearchRequestDetailFile> pendingList = resRqDtlFM.selectPendingProcessRecordBySequence(processSequence);
		
			List<String> doneList = new ArrayList<String>();
			
			ResourceSearchRequestDetailFileKey detailKey = new ResourceSearchRequestDetailFileKey();
			detailKey.setProcesssequence(processSequence);
			
			for(ResourceSearchRequestDetailFile item : pendingList){
				
				detailKey.setSearchkeyword(item.getSearchkeyword());
				List<MagnetInfo> magList= tcProcessor.serachMagnetInfo(item.getSearchkeyword());
				
				//搜索引擎无发现
				if(magList.size()==0){
					item.setRequestprocessstatus(MessageDefinePool.STS0001);
					resRqDtlFM.updateByPrimaryKey(item);
					continue;
				}
				
				Iterator<MagnetInfo>  itm = magList.iterator();
				while(itm.hasNext()){
					MagnetInfo magItem = itm.next();
					
					if(doneList.contains(magItem.getResourcename())) break; //Skip duplicate fanhao search
					
					if(magItem!=null){
						
						if(tcProcessor.processTorrentConvert(magItem,prefix)){
							doneList.add(magItem.getResourcename());
							item.setRequestprocessstatus(MessageDefinePool.STS0002);
							item.setMagnetinfohashinhex(magItem.getMagnetinfohashinhex());
							resRqDtlFM.updateByPrimaryKey(item);
						}else{
							item.setRequestprocessstatus(MessageDefinePool.STS0004);
							item.setMagnetinfohashinhex(magItem.getMagnetinfohashinhex());
							resRqDtlFM.updateByPrimaryKey(item);
						}
					} 
				}
				
				SessionFactorySingleton.getInstance().getOpenSession().commit();
			}
			stage++;
		}
		
		//Process Completed , update sequence status
		ResourceSearchRequestFile reqItem = rsrfm.selectByPrimaryKey(processSequence);
		reqItem.setRequestprocessstatus("Completed");
		rsrfm.updateByPrimaryKey(reqItem);
		
		SessionFactorySingleton.getInstance().getOpenSession().commit();
		
		//copy download folder to HEX folder for backup
		String sourceDir = Config.UPLOAD_DONWLOAD_MAIN_PATH + "\\" + prefix;
		String targetDir = Config.UPLOAD_DONWLOAD_HEX_BACKUP_PATH + "\\" + prefix + "_" + processSequence;
		
		File tempFile = new File(sourceDir); 
		if(tempFile.exists()){
			ToolBoxUtility.copyDirectiory(sourceDir, targetDir);
			
			//Rename from HEX to fanhao
			tcProcessor.renameFromHexToFanhao(sourceDir);
		}
	}
	
	public static void PostJavByColaFile(String prefix) throws ParseException, IOException, InterruptedException{
		String respHtml;
		
		ColaFileProcessor cfProcessor = new ColaFileProcessor();
		cfProcessor.login();
//		String respHtml = cfProcessor.getSpaceFileMain();
		
		String path = "sth\\" + prefix;
		String[] folderLevel = path.split("\\\\");
//		List<String> folderlist = Arrays.asList(folderLevel); // don't use this as 这个ArrayList并不是java.util包下面的ArrayList，而是java.util.Arrays类中的一个内部类, 继承自AbstractList , add和remove都是throw UnsupportedOperationException
		List<String> folderlist = new ArrayList();
		Collections.addAll(folderlist,folderLevel);

		Map itemMap = cfProcessor.lookUpFolder(folderlist);
		
		JavPostProcessor jpProcessor = new JavPostProcessor();
		
		jpProcessor.recoverPostJavByMapping(itemMap);
		
		logger.info("PostJavByColaFile Completed! ");
	}
	
	public static void checkOutstandingUpload(String prefix) throws Exception {
		
		ColaFileProcessor cfProcessor = new ColaFileProcessor();
		cfProcessor.login();
		cfProcessor.compareLocal("sth\\"+prefix, Config.CAPTCHA_TMP_PATH+"\\" + prefix);
		
		logger.info("checkOutstandingUpload Completed! ");
	}
}
