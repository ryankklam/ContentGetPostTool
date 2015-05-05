/**
 *  @Author: 	Ryan K K Lam
 *  @Links: 	https://github.com/ryankklam
 *  @Email: 	ryan4299899@126.com
 *
 */
package client.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import org.apache.http.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.config.Config;
import client.singleton.SessionFactorySingleton;
import client.utils.HttpUtils;
import client.utils.ToolBoxUtility;

import com.mybatis.inter.MagnetInfoMapper;
import com.mybatis.model.MagnetInfo;

/**
 * @ClassName: MagnetConvertProcessor  
 * @Description: Get resource from target server and convert as Magnet link/torrent 
 * @date 2015-2-27 
 */
public class MagnetConvertProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(MagnetConvertProcessor.class);

	HttpProcessor processor;
	byte[] notFound404Byte;
	
	//link
	//torcachePrefix
	//String btBoxPrefix
	
	//For Btspread setup
	int timeout = 2000;
	//String btSpreadSearch
	
	//for magnet txt
	private static final String txtHeader1 = "磁力链/Magnet Link";
	private static final String txtFooter1 = "如果发现内容有误，请私信告知，有专人复查.";
	private static final String txtFooter2 = "If the attachment is not match the post , pls pm , team will go & check it out.";
	
	public MagnetConvertProcessor() throws IOException {
		
		processor = new HttpProcessor();
		notFound404Byte = ToolBoxUtility.getFileBytes(Config.CAPTCHA_TMP_PATH + "\\404NotFound.torrent");
	}

	
	//Reporting
	int downloadCount = 0;

	public boolean saveMagnetInfoTXT(MagnetInfo item , String fanhao){
		
		String txtHeader = item.getResourcename() + " " + txtHeader1;
		
		
		File codeFile = null;

		if (Config.UPLOAD_DONWLOAD_MAIN_PATH != "") {
			
			//check if output folder exist or not , to avoid file not found exception
			String outputPath = Config.UPLOAD_DONWLOAD_MAIN_PATH + "\\" + fanhao;
			File outputPathFile = new File(outputPath);
			if(!outputPathFile.exists() && !outputPathFile.isDirectory()){
			    logger.info(outputPathFile + " 不存在 , 立即创建！");  
			    outputPathFile .mkdir(); 
			}
			
			codeFile = new File(outputPath + "\\" + item.getMagnetinfohashinhex() + ".txt");
		} else {
			codeFile = new File(FileSystemView.getFileSystemView()
					.getHomeDirectory().getPath()+ "\\" + fanhao + "\\" + item.getMagnetinfohashinhex() + ".txt");
		}
		
		//check if file already exist , and skip download
		if(codeFile.exists()){
			logger.info(codeFile.getName() + " 已经存在！");  
			return false;
		}
		
		//output Magnet info txt
		ToolBoxUtility.putStrToTXT(codeFile, txtHeader);
		ToolBoxUtility.putStrToTXT(codeFile, item.getResourcedescription());
		ToolBoxUtility.putStrToTXT(codeFile, item.getFullmagnetinfolink());
		ToolBoxUtility.putStrToTXT(codeFile, "");
		ToolBoxUtility.putStrToTXT(codeFile, txtFooter1);
		ToolBoxUtility.putStrToTXT(codeFile, txtFooter2);
		
		return false;
	}
	
	public void processDownloadByFanHao(String fanhao) throws ParseException, IOException, InterruptedException{

		List<MagnetInfo> processList = serachMagnetInfo(fanhao);
		logger.info("Total count to process : " + processList.size());
		Iterator<MagnetInfo>  it = processList.iterator();
		while(it.hasNext()){
			processTorrentConvert(it.next() , fanhao);
		}
		logger.info("processDownloadByFanHao : " + downloadCount);	
	}
	
	public void renameFromHexToFanhao(String localFolder) throws IOException{

		MagnetInfoMapper mi = SessionFactorySingleton.getInstance().getOpenSession().getMapper(MagnetInfoMapper.class);
		
		File uploadDataDir = new File(localFolder);
		
		for (File Filedata : uploadDataDir.listFiles()) {
			
			String extension = "." + ToolBoxUtility.getExtensionName(Filedata.getName());
			
			String hexInfo = Filedata.getName().replaceAll(extension, "");
			
			MagnetInfo magnetInfo = mi.selectByPrimaryKey(hexInfo);
			if(magnetInfo!=null){
				String fanhaoName = magnetInfo.getResourcename().concat(extension);
				ToolBoxUtility.renameFile(localFolder,Filedata.getName(),fanhaoName);
			}
		}
	}
	

	
	public List<MagnetInfo> serachMagnetInfo(String fanhao) throws ParseException, IOException, InterruptedException {
		
		HttpProcessor processor = new HttpProcessor();
		
		List<MagnetInfo> magItemList = new ArrayList<MagnetInfo>();
		
		// Way2 by interface
		MagnetInfoMapper mi = SessionFactorySingleton.getInstance().getOpenSession().getMapper(MagnetInfoMapper.class);

			String serachLink = Config.btSpreadSearch_URL.concat(fanhao);

			logger.info(serachLink);
			
			String respHtml = processor.getWithHost(serachLink);
			Thread.sleep(timeout);
			
			Document docPages = Jsoup.parse(respHtml);
			
			Elements torrentTable = docPages.getElementsByTag("table");

			if (torrentTable.size() != 0) { // when size = 0 means no result
											// return
				
				Elements torrentList = docPages.getElementsByTag("table").first().getElementsByTag("tr");
				for (int j = 1; j < torrentList.size(); j++) {
					Element torrentGroup = torrentList.get(j);
					String torrentLink = torrentGroup.getElementsByTag("a")
							.attr("href");	//http://www.btspread.com/magnet/detail/hash/70B0F6B1596CC295A5E65DCE32D9984D44B00A1C
					String torrentTitle = torrentGroup.getElementsByTag("a")
							.attr("title");

					String magnetInfohash = torrentLink.replaceAll("http://www.btspread.com/magnet/detail/hash/", "");
					
					if (!torrentTitle.contains(fanhao)){
						continue; // 没有精确匹配到，继续proceed下个结果
					}
						
					logger.info(torrentTitle);

					// get magnet link
					String magnetHtml = processor.getWithHost(torrentLink);
					Thread.sleep(timeout);
							
					Document magnetPages = Jsoup.parse(magnetHtml);

					String magnetLink = magnetPages.getElementsByClass("magnet-link").text(); // magnet:?xt=urn:btih:27A541E7AB3A740CB53B7B396107115F75507EFE&dn=1009-ebod-402
					logger.info(magnetLink);
					
					MagnetInfo magnetInfo = new MagnetInfo();
					magnetInfo.setResourcename(fanhao);
					magnetInfo.setResourcedescription(torrentTitle);
					magnetInfo.setMagnetinfohashinhex(magnetInfohash);
					magnetInfo.setFullmagnetinfolink(magnetLink);

					if(mi.selectByPrimaryKey(magnetInfohash)==null){
						try{
							mi.insert(magnetInfo);
							// 测试增加,增加后，必须提交事务，否则不会写入到数据库.
							SessionFactorySingleton.getInstance().getOpenSession().commit();
							
							}catch(Exception e){
								e.printStackTrace();
								SessionFactorySingleton.getInstance().getOpenSession().rollback();
							}
					}
					magItemList.add(magnetInfo);
				}
			}
			
			return magItemList;
	}
	

	
	public boolean processTorrentConvert(MagnetInfo item , String fanhao) throws IOException{
		
		logger.info(item.getResourcedescription());	
		
		try{	
			
			String btlink = Config.btBoxPrefix_URL +  item.getMagnetinfohashinhex().substring(0, 2) + "/" 
							+ item.getMagnetinfohashinhex().substring(item.getMagnetinfohashinhex().length()-2, item.getMagnetinfohashinhex().length())
							+ "/" + item.getMagnetinfohashinhex() + ".torrent";
			
			
//			String downlink = Config.torcachePrefix_URL.concat(item.getMagnetinfohashinhex()).concat(".torrent");
			
//			byte[] torrentBytes = HttpUtils.get2byteWithHostGzip(processor.getHttpClient(), btlink);
			byte[] torrentBytes = HttpUtils.get2byteWithHost(processor.getHttpClient(), btlink);
			
			if (torrentBytes.length != 0
					&& !Arrays.equals(torrentBytes,notFound404Byte)) { // no torrent found

				logger.info("MagnetInfo found in btBox");	
				
				File codeFile = null;

					if (Config.UPLOAD_DONWLOAD_MAIN_PATH != "") {
						
						//check if output folder exist or not , to avoid finenotfound exception
						String outputPath = Config.UPLOAD_DONWLOAD_MAIN_PATH + "\\" + fanhao;
						File outputPathFile = new File(outputPath);
						if(!outputPathFile.exists() && !outputPathFile.isDirectory()){
						    logger.info(outputPathFile + " 不存在 , 立即创建！");  
						    outputPathFile .mkdir(); 
						}
						
						codeFile = new File(outputPath + "\\" + item.getMagnetinfohashinhex() + ".torrent");
					} else {
						codeFile = new File(FileSystemView.getFileSystemView()
								.getHomeDirectory().getPath()+ "\\" + fanhao + "\\" + item.getMagnetinfohashinhex() + ".torrent");
					}
					
					//check if file already exist , and skip download
					if(codeFile.exists()){
						logger.info(codeFile.getName() + " 已经存在！");  
						return false;
					}

					FileOutputStream fos = new FileOutputStream(codeFile);
					fos.write(torrentBytes);
					fos.flush();
					fos.close();
					downloadCount++;
					
					return true;
	
			}else{
				logger.info("Not found in btBox!!! Try output magnet link txt");
				saveMagnetInfoTXT(item,fanhao);
			}
		}catch(Exception e){
			logger.info("Catch Exception - Rollback");	
			SessionFactorySingleton.getInstance().getOpenSession().rollback();
		}
		
		return false;
	}
}
