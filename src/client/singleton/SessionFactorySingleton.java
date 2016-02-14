/**
 *  @Author: 	Ryan K K Lam
 *  @Links: 	https://github.com/ryankklam
 *  @Email: 	ryan4299899@126.com
 *
 */
package client.singleton;

/**
 * @ClassName: SessionFactorySingleton  
 * @Description: Singleton For manage MyBatis sql session  
 * @date 2015-1-4 
 */
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
  
public class SessionFactorySingleton {  
  
    private final static Log log = LogFactory.getLog(SessionFactorySingleton.class); 
    
//	private static SqlSessionFactory sqlSessionFactory;
	private static SqlSession session;
  
    private volatile long updateTime = 0L;// 更新缓存时记录的时间  
  
    private volatile boolean updateFlag = true;// 正在更新时的阀门，为false时表示当前没有更新缓存，为true时表示当前正在更新缓存  
  
    private volatile static SessionFactorySingleton mapCacheObject;// 缓存实例对象  
  
//    private static Map<String, String> cacheMap = new ConcurrentHashMap<String, String>();// 缓存容器  
  
    private SessionFactorySingleton() throws IOException {  
        this.LoadCache();// 加载缓存  
        updateTime = System.currentTimeMillis();// 缓存更新时间  
    }  
  
    /** 
     * 采用单例模式获取缓存对象实例 
     *  
     * @return 
     * @throws IOException 
     */  
    public static SessionFactorySingleton getInstance() throws IOException {  
        if (null == mapCacheObject) {  
            synchronized (SessionFactorySingleton.class) {  
                if (null == mapCacheObject) {  
                    mapCacheObject = new SessionFactorySingleton();
                }  
            }  
        }  
        return mapCacheObject;  
    }  

  
    /** 
     * 装载缓存 
     * @throws IOException 
     */  
    private void LoadCache() throws IOException {  
  
        this.updateFlag = true;// 正在更新  
  
        /********** 数据处理，将数据放入cacheMap缓存中 **begin ******/  

		SqlSessionFactory sqlSessionFactory = null;
		
		Reader reader;
		
		try {
			reader = Resources.getResourceAsReader("Configuration.xml");
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		} catch (Exception e) {
			e.printStackTrace();
		}

		session = sqlSessionFactory.openSession();

        /********** 数据处理，将数据放入cacheMap缓存中 ***end *******/  
  
        this.updateFlag = false;// 更新已完成  
  
    }  
  
    /** 
     * 返回缓存对象 
     *  
     * @return 
     * @throws IOException 
     */  
    public SqlSession getOpenSession() throws IOException {  
  
        if (this.updateFlag) {// 前缓存正在更新  
            log.info("cache is Instance .....");  
            return null;  
        }    
  
        return session;  
    }
    
    public SqlSession getNewOpenSession() throws IOException {  

		SqlSessionFactory sqlSessionFactory = null;
		
		Reader reader;
		
		try {
			reader = Resources.getResourceAsReader("Configuration.xml");
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sqlSessionFactory.openSession();
    } 

}  