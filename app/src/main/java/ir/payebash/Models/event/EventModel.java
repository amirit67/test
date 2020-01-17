package ir.payebash.Models.event;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.SerializedName;

import ir.payebash.Models.event.detail.FollwoersItem;

public class EventModel implements Serializable {

	@SerializedName("isWoman")
	private boolean isWoman;

	@SerializedName("images")
	private String images;

	@SerializedName("cost")
	private String cost;

	@SerializedName("city")
	private int city;

	@SerializedName("users")
	private List<FollwoersItem> follwes;

	@SerializedName("isImmediate")
	private boolean isImmediate;

	@SerializedName("service")
	private int service;

	@SerializedName("Id")
	private String id;

	@SerializedName("title")
	private String title;

	@SerializedName("createDate")
	private String createDate;

	@SerializedName("numberFollowers")
	private String numberFollowers;


	public void setPostId(String id) {
		this.id = id;
	}

	public boolean IsWoman(){
		return isWoman;
	}

	public String getImages(){
		return images;
	}

	public String getCost(){
		return cost;
	}

	public int getCity(){
		return city;
	}

	public List<FollwoersItem> getFollowers(){
		return follwes;
	}

	public boolean IsImmediate(){
		return isImmediate;
	}

	public int getService(){
		return service;
	}

	public String getPostId(){
		return id;
	}

	public String getTitle(){
		return title;
	}

	public String getCreateDate(){
		return createDate;
	}

	public String getNumberFollowers() {
		return numberFollowers;
	}

	@Override
 	public String toString(){
		return 
			"EventModel{" + 
			"isWoman = '" + isWoman + '\'' + 
			",images = '" + images + '\'' + 
			",cost = '" + cost + '\'' + 
			",city = '" + city + '\'' + 
			",usersImages = '" + follwes + '\'' +
			",isImmediate = '" + isImmediate + '\'' + 
			",service = '" + service + '\'' + 
			",id = '" + id + '\'' +
			",title = '" + title + '\'' + 
			",createDate = '" + createDate + '\'' + 
			"}";
		}
}