package ir.payebash.Models.event;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StoryModel implements Serializable {


	@SerializedName("city")
	private int city;

	@SerializedName("endDate")
	private String endDate;

	@SerializedName("subject")
	private int subject;

	@SerializedName("title")
	private String title;

	@SerializedName("isWoman")
	private boolean isWoman;

	@SerializedName("isImmediate")
	private boolean isImmediate;

	@SerializedName("timeToJoin")
	private String timeToJoin;

	@SerializedName("state")
	private String state;

	@SerializedName("createDate")
	private String createDate;

	@SerializedName("images")
	private String images;

	@SerializedName("cost")
	private String cost;

	@SerializedName("userId")
	private String userId;

	@SerializedName("ownerImage")
	private String profileimage;

	@SerializedName("numberFollowers")
	private String numberFollowers;

	@SerializedName("startDate")
	private String startDate;

	@SerializedName("owner")
	private String owner;

	@SerializedName("imageOwner")
	private String imageOwner;

	public int getCity(){
		return city;
	}

	public String getEndDate(){
		return endDate;
	}

	public int getSubject(){
		return subject;
	}

	public String getTitle(){
		return title;
	}

	public boolean IsWoman(){
		return isWoman;
	}

	public boolean isImmediate() {
		return isImmediate;
	}

	public String getTimeToJoin(){
		return timeToJoin;
	}

	public String getState(){
		return state;
	}

	public String getCreateDate(){
		return createDate;
	}

	public String getImages(){
		return images;
	}

	public String getCost(){
		return cost;
	}

	public String getUserId(){
		return userId;
	}

	public String getProfileimage(){
		return profileimage;
	}

	public String getNumberFollowers(){
		return numberFollowers;
	}

	public String getStartDate(){
		return startDate;
	}

	public String getOwner() {
		return owner;
	}

	public String getImageOwner() {
		return imageOwner;
	}
}