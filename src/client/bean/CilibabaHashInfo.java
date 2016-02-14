/**
 *  @Author: 	Ryan K K Lam
 *  @Links: 	https://github.com/ryankklam
 *  @Email: 	ryan4299899@126.com
 *
 */
package client.bean;

/**
 * @ClassName: CilibabaHashInfo  
 * @Description: TODO(Override class description here)  
 * @date 2015-10-9 
 */
public class CilibabaHashInfo {
	
	private String id;
	private String category;
	private String comment;
	private String data_hash;
	private String name;
	private String extension;
	
	private String creator;
	private String classified;
	private String source_ip;
	private String tagged;
	private String info_hash;
	private String length;
	private String create_time;
	private String requests;
	private String last_seen;
	
	public String getFullHashLink(){
		return "magnet:?xt=urn:btih:" + info_hash;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * @return the data_hash
	 */
	public String getData_hash() {
		return data_hash;
	}
	/**
	 * @param data_hash the data_hash to set
	 */
	public void setData_hash(String data_hash) {
		this.data_hash = data_hash;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the extension
	 */
	public String getExtension() {
		return extension;
	}
	/**
	 * @param extension the extension to set
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}
	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}
	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	/**
	 * @return the classified
	 */
	public String getClassified() {
		return classified;
	}
	/**
	 * @param classified the classified to set
	 */
	public void setClassified(String classified) {
		this.classified = classified;
	}
	/**
	 * @return the source_ip
	 */
	public String getSource_ip() {
		return source_ip;
	}
	/**
	 * @param source_ip the source_ip to set
	 */
	public void setSource_ip(String source_ip) {
		this.source_ip = source_ip;
	}
	/**
	 * @return the tagged
	 */
	public String getTagged() {
		return tagged;
	}
	/**
	 * @param tagged the tagged to set
	 */
	public void setTagged(String tagged) {
		this.tagged = tagged;
	}
	/**
	 * @return the info_hash
	 */
	public String getInfo_hash() {
		return info_hash;
	}
	/**
	 * @param info_hash the info_hash to set
	 */
	public void setInfo_hash(String info_hash) {
		this.info_hash = info_hash;
	}
	/**
	 * @return the length
	 */
	public String getLength() {
		return length;
	}
	/**
	 * @param length the length to set
	 */
	public void setLength(String length) {
		this.length = length;
	}
	/**
	 * @return the create_time
	 */
	public String getCreate_time() {
		return create_time;
	}
	/**
	 * @param create_time the create_time to set
	 */
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	/**
	 * @return the requests
	 */
	public String getRequests() {
		return requests;
	}
	/**
	 * @param requests the requests to set
	 */
	public void setRequests(String requests) {
		this.requests = requests;
	}
	/**
	 * @return the last_seen
	 */
	public String getLast_seen() {
		return last_seen;
	}
	/**
	 * @param last_seen the last_seen to set
	 */
	public void setLast_seen(String last_seen) {
		this.last_seen = last_seen;
	}	
	
	
}
