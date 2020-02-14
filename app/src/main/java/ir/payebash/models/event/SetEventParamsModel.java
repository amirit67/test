package ir.payebash.models.event;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SetEventParamsModel implements Serializable {

	@SerializedName("images")
	private String images;

	@SerializedName("cost")
	private String cost;

	@SerializedName("city")
	private int city;

	@SerializedName("endDate")
	private String endDate;

	@SerializedName("subject")
	private String subject;

	@SerializedName("isImmediate")
	private boolean isImmediate;

	@SerializedName("latitude")
	private String latitude;

	@SerializedName("description")
	private String description;

	@SerializedName("title")
	private String title;

	@SerializedName("Name")
	private String name;

	@SerializedName("isWoman")
	private boolean isWoman;

	@SerializedName("timeToJoin")
	private String timeToJoin;

	@SerializedName("state")
	private String state;

	@SerializedName("numberFollowers")
	private String numberFollowers;

	@SerializedName("startDate")
	private String startDate;

	@SerializedName("startTime")
	private String startTime;

	@SerializedName("createDate")
	private String createDate;

	@SerializedName("longitude")
	private String longitude;

	public void setImages(String images) {
		this.images = images;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public void setCity(int city) {
		this.city = city;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setImmediate(boolean immediate) {
		isImmediate = immediate;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWoman(boolean woman) {
		isWoman = woman;
	}

	public void setTimeToJoin(String timeToJoin) {
		this.timeToJoin = timeToJoin;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setNumberFollowers(String numberFollowers) {
		this.numberFollowers = numberFollowers;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}