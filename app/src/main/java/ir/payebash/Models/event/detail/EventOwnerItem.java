package ir.payebash.Models.event.detail;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EventOwnerItem implements Serializable {

	@SerializedName("id")
	private String id;

	@SerializedName("profileImage")
	private String profileImage;

	@SerializedName("userName")
	private String userName;

	@SerializedName("token")
	private String token;

	@SerializedName("vote")
	private String vote;

	@SerializedName("trustPercentage")
	private int trustPercentage;

	public String getVote() {
		return vote;
	}

	public String getId(){
		return id;
	}

	public String getProfileImage(){
		return profileImage;
	}

	public String getUserName(){
		return userName;
	}

	public String getToken() {
		return token;
	}

	public int getTrustPercentage() {
		return trustPercentage;
	}
}