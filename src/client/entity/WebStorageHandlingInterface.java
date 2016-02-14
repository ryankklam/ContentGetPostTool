/**
 *  @Author: 	Ryan K K Lam
 *  @Links: 	https://github.com/ryankklam
 *  @Email: 	ryan4299899@126.com
 *
 */
package client.entity;

import java.io.IOException;
import java.util.List;


/**
 * @ClassName: webStorageHandlingInterface  
 * @Description: TODO(Override class description here)  
 * @date 2016-1-9 
 */
public interface WebStorageHandlingInterface {
	public boolean login();
	public void upload();
	@SuppressWarnings("rawtypes")
	public List selectPendingPostItem();
	public int getOutstandingItemCount() throws IOException;
}
