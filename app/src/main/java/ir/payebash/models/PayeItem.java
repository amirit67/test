package ir.payebash.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class PayeItem implements Serializable {

	@SerializedName("isWoman")
	private boolean isWoman;

	@SerializedName("images")
	private String images;

	@SerializedName("cost")
	private String cost;

	@SerializedName("city")
	private int city;

	@SerializedName("subject")
	private String subject;

	@SerializedName("isImmediate")
	private boolean isImmediate;

	@SerializedName("timeToJoin")
	private String timeToJoin;

	@SerializedName("postId")
	private String postId;

	@SerializedName("tag")
	private String tag;

	@SerializedName("state")
	private String state;

	@SerializedName("title")
	private String title;

	@SerializedName("createDate")
	private String createDate;

	public void setIsWoman(boolean isWoman){
		this.isWoman = isWoman;
	}

	public boolean IsWoman(){
		return isWoman;
	}

	public void setImages(String images){
		this.images = images;
	}

	public String getImages(){
		return images;
	}

	public void setCost(String cost){
		this.cost = cost;
	}

	public String getCost(){
		return cost;
	}

	public void setCity(int city){
		this.city = city;
	}

	public int getCity(){
		return city;
	}

	public void setSubject(String subject){
		this.subject = subject;
	}

	public String getSubject(){
		return subject;
	}

	public void setIsImmediate(boolean isImmediate){
		this.isImmediate = isImmediate;
	}

	public boolean IsImmediate(){
		return isImmediate;
	}

	public void setTimeToJoin(String timeToJoin){
		this.timeToJoin = timeToJoin;
	}

	public String getTimeToJoin(){
		return timeToJoin;
	}

	public void setPostId(String postId){
		this.postId = postId;
	}

	public String getPostId(){
		return postId;
	}

	public void setTag(String tag){
		this.tag = tag;
	}

	public String getTag(){
		return tag;
	}

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
		return state;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setCreateDate(String createDate){
		this.createDate = createDate;
	}

	public String getCreateDate(){
		return createDate;
	}

	@Override
 	public String toString(){
		return 
			"PayeItem{" + 
			"isWoman = '" + isWoman + '\'' + 
			",images = '" + images + '\'' + 
			",cost = '" + cost + '\'' + 
			",city = '" + city + '\'' + 
			",subject = '" + subject + '\'' + 
			",isImmediate = '" + isImmediate + '\'' + 
			",timeToJoin = '" + timeToJoin + '\'' + 
			",postId = '" + postId + '\'' + 
			",tag = '" + tag + '\'' + 
			",state = '" + state + '\'' + 
			",title = '" + title + '\'' + 
			",createDate = '" + createDate + '\'' + 
			"}";
		}
}