package ir.payebash.Models.event;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class EventModel {

	@SerializedName("gmail")
	private String gmail;

	@SerializedName("city")
	private int city;

	@SerializedName("endDate")
	private String endDate;

	@SerializedName("subject")
	private int subject;

	@SerializedName("latitude")
	private String latitude;

	@SerializedName("applicants")
	private List<ApplicantsItem> applicants;

	@SerializedName("description")
	private String description;

	@SerializedName("instagram")
	private String instagram;

	@SerializedName("title")
	private String title;

	@SerializedName("isWoman")
	private boolean isWoman;

	@SerializedName("timeToJoin")
	private String timeToJoin;

	@SerializedName("soroosh")
	private String soroosh;

	@SerializedName("state")
	private String state;

	@SerializedName("createDate")
	private String createDate;

	@SerializedName("longitude")
	private String longitude;

	@SerializedName("images")
	private String images;

	@SerializedName("comments")
	private List<Object> comments;

	@SerializedName("cost")
	private String cost;

	@SerializedName("mobile")
	private String mobile;

	@SerializedName("telegram")
	private String telegram;

	@SerializedName("userId")
	private String userId;

	@SerializedName("profileimage")
	private String profileimage;

	@SerializedName("token")
	private String token;

	@SerializedName("numberFollowers")
	private String numberFollowers;

	@SerializedName("startDate")
	private String startDate;

	@SerializedName("username")
	private String username;

	public String getGmail(){
		return gmail;
	}

	public int getCity(){
		return city;
	}

	public String getEndDate(){
		return endDate;
	}

	public int getSubject(){
		return subject;
	}

	public String getLatitude(){
		return latitude;
	}

	public List<ApplicantsItem> getApplicants(){
		return applicants;
	}

	public String getDescription(){
		return description;
	}

	public String getInstagram(){
		return instagram;
	}

	public String getTitle(){
		return title;
	}

	public boolean isIsWoman(){
		return isWoman;
	}

	public String getTimeToJoin(){
		return timeToJoin;
	}

	public String getSoroosh(){
		return soroosh;
	}

	public String getState(){
		return state;
	}

	public String getCreateDate(){
		return createDate;
	}

	public String getLongitude(){
		return longitude;
	}

	public String getImages(){
		return images;
	}

	public List<Object> getComments(){
		return comments;
	}

	public String getCost(){
		return cost;
	}

	public String getMobile(){
		return mobile;
	}

	public String getTelegram(){
		return telegram;
	}

	public String getUserId(){
		return userId;
	}

	public String getProfileimage(){
		return profileimage;
	}

	public String getToken(){
		return token;
	}

	public String getNumberFollowers(){
		return numberFollowers;
	}

	public String getStartDate(){
		return startDate;
	}

	public String getUsername(){
		return username;
	}
}