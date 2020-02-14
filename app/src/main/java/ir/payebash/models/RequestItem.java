package ir.payebash.models;

import com.google.gson.annotations.SerializedName;

public class RequestItem {

	@SerializedName("title")
	private String eventTitle;

	@SerializedName("Id")
	private String id;

	@SerializedName("Name")
	private String applicantName;

	@SerializedName("profileImage")
	private String profileImage;

	@SerializedName("UserName")
	private String applicantUsername;

	@SerializedName("State")
	private String state;

	@SerializedName("CreateDate")
	private String createDate;

	public String getEventTitle() {
		return eventTitle;
	}

	public String getId() {
		return id;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public String getApplicantUsername() {
		return applicantUsername;
	}

	public String getState() {
		return state;
	}

	public String getCreateDate() {
		return createDate;
	}
}