/**
 *  @Author: 	Ryan K K Lam
 *  @Links: 	https://github.com/ryankklam
 *  @Email: 	ryan4299899@126.com
 *
 */
package client.entity;


/**
 * @ClassName: WebStorageItemEntity  
 * @Description: TODO(Override class description here)  
 * @date 2016-1-22 
 */
public abstract class WebStorageItemEntity {
		
	public abstract String getFileNameWithoutSuffix();
	public abstract String getFullDownloadLinkWithDomain();
	public abstract String getStorageIdentityName();
}
