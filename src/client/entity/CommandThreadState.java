/**
 *  @Author: 	Ryan K K Lam
 *  @Links: 	https://github.com/ryankklam
 *  @Email: 	ryan4299899@126.com
 *
 */
package client.entity;

/**
 * @ClassName: CommandThreadState  
 * @Description: TODO(Override class description here)  
 * @date 2016-2-14 
 */
public class CommandThreadState {
	
    public static final int STOPPED = 0;  
    public static final int RUNNING = 1;  
    public static final int STARTING = 2;  
    public static final int ENDING = 3;  
    
    private int status = STOPPED;
    
    public void setStatus(int status){
    	synchronized(this){
    		this.status = status;
    	}
    }
    
    public int getStatus(){
    	return status;
    }
    
    public boolean isAlive(){
    	switch(status){
    		case RUNNING:case STARTING:
    			return true;
    	}
    	return false;
    }
    
}
