package ir.payebash.Models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PayeDetailsModel{

	@SerializedName("telegram")
	private String telegram;

	@SerializedName("soroosh")
	private String soroosh;

	@SerializedName("gmail")
	private String gmail;

    @SerializedName("phoneNumber")
    private String phoneNumber;

	@SerializedName("comments")
	private List<String> comments;

	@SerializedName("images")
	private String images;

	@SerializedName("applicants")
	private List<String> applicants;

	@SerializedName("title")
	private String title;

	@SerializedName("token")
	private String token;

	@SerializedName("latitude")
	private String latitude;

	@SerializedName("city")
	private int city;

	@SerializedName("userId")
	private String userId;

	@SerializedName("mobile")
	private String mobile;

	@SerializedName("createDate")
	private String createDate;

	@SerializedName("longitude")
	private String longitude;

	@SerializedName("profileimage")
	private String profileimage;

	@SerializedName("subject")
	private int subject;

	@SerializedName("instagram")
	private String instagram;

	@SerializedName("isWoman")
	private boolean isWoman;

	@SerializedName("state")
	private String state;

	@SerializedName("baseProperty")
	private List<String> baseProperty;

	@SerializedName("username")
	private String username;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

	public void setTelegram(String telegram){
		this.telegram = telegram;
	}

	public String getTelegram(){
		return telegram;
	}

	public void setSoroosh(String soroosh){
		this.soroosh = soroosh;
	}

	public String getSoroosh(){
		return soroosh;
	}

	public void setGmail(String gmail){
		this.gmail = gmail;
	}

	public String getGmail(){
		return gmail;
	}

	public void setComments(List<String> comments){
		this.comments = comments;
	}

	public List<String> getComments(){
		return comments;
	}

	public void setImages(String images){
		this.images = images;
	}

	public String getImages(){
		return images;
	}

	public void setApplicants(List<String> applicants){
		this.applicants = applicants;
	}

	public List<String> getApplicants(){
		return applicants;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setToken(String token){
		this.token = token;
	}

	public String getToken(){
		return token;
	}

	public void setLatitude(String latitude){
		this.latitude = latitude;
	}

	public String getLatitude(){
		return latitude;
	}

	public void setCity(int city){
		this.city = city;
	}

	public int getCity(){
		return city;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return userId;
	}

	public void setMobile(String mobile){
		this.mobile = mobile;
	}

	public String getMobile(){
		return mobile;
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

	public void setProfileimage(String profileimage){
		this.profileimage = profileimage;
	}

	public String getProfileimage(){
		return profileimage;
	}

	public void setSubject(int subject){
		this.subject = subject;
	}

	public int getSubject(){
		return subject;
	}

	public void setInstagram(String instagram){
		this.instagram = instagram;
	}

	public String getInstagram(){
		return instagram;
	}

	public void setIsWoman(boolean isWoman){
		this.isWoman = isWoman;
	}

	public boolean isIsWoman(){
		return isWoman;
	}

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
		return state;
	}

	public void setBaseProperty(List<String> baseProperty){
		this.baseProperty = baseProperty;
	}

	public List<String> getBaseProperty(){
		return baseProperty;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public String getUsername(){
		return username;
	}
}