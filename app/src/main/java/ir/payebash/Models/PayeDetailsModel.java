package ir.payebash.Models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PayeDetailsModel{

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
	private List<String> applicants;

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
	private List<String> comments;

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

	public void setGmail(String gmail){
		this.gmail = gmail;
	}

	public String getGmail(){
		return gmail;
	}

	public void setCity(int city){
		this.city = city;
	}

	public int getCity(){
		return city;
	}

	public void setEndDate(String endDate){
		this.endDate = endDate;
	}

	public String getEndDate(){
		return endDate;
	}

	public void setSubject(int subject){
		this.subject = subject;
	}

	public int getSubject(){
		return subject;
	}

	public void setLatitude(String latitude){
		this.latitude = latitude;
	}

	public String getLatitude(){
		return latitude;
	}

	public void setApplicants(List<String> applicants){
		this.applicants = applicants;
	}

	public List<String> getApplicants(){
		return applicants;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setInstagram(String instagram){
		this.instagram = instagram;
	}

	public String getInstagram(){
		return instagram;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setIsWoman(boolean isWoman){
		this.isWoman = isWoman;
	}

	public boolean isIsWoman(){
		return isWoman;
	}

	public void setTimeToJoin(String timeToJoin){
		this.timeToJoin = timeToJoin;
	}

	public String getTimeToJoin(){
		return timeToJoin;
	}

	public void setSoroosh(String soroosh){
		this.soroosh = soroosh;
	}

	public String getSoroosh(){
		return soroosh;
	}

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
		return state;
	}

	public void setCreateDate(String createDate){
		this.createDate = createDate;
	}

	public String getCreateDate(){
		return createDate;
	}

	public void setLongitude(String longitude){
		this.longitude = longitude;
	}

	public String getLongitude(){
		return longitude;
	}

	public void setImages(String images){
		this.images = images;
	}

	public String getImages(){
		return images;
	}

	public void setComments(List<String> comments){
		this.comments = comments;
	}

	public List<String> getComments(){
		return comments;
	}

	public void setCost(String cost){
		this.cost = cost;
	}

	public String getCost(){
		return cost;
	}

	public void setMobile(String mobile){
		this.mobile = mobile;
	}

	public String getMobile(){
		return mobile;
	}

	public void setTelegram(String telegram){
		this.telegram = telegram;
	}

	public String getTelegram(){
		return telegram;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return userId;
	}

	public void setProfileimage(String profileimage){
		this.profileimage = profileimage;
	}

	public String getProfileimage(){
		return profileimage;
	}

	public void setToken(String token){
		this.token = token;
	}

	public String getToken(){
		return token;
	}

	public void setNumberFollowers(String numberFollowers){
		this.numberFollowers = numberFollowers;
	}

	public String getNumberFollowers(){
		return numberFollowers;
	}

	public void setStartDate(String startDate){
		this.startDate = startDate;
	}

	public String getStartDate(){
		return startDate;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public String getUsername(){
		return username;
	}
}