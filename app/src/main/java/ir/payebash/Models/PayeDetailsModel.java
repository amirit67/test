package ir.payebash.Models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PayeDetailsModel{

	@SerializedName("Telegram")
	private String telegram;

	@SerializedName("Soroosh")
	private String soroosh;

	@SerializedName("Gmail")
	private String gmail;

    @SerializedName("PhoneNumber")
    private String phoneNumber;

	@SerializedName("Comments")
	private List<String> comments;

	@SerializedName("Images")
	private String images;

	@SerializedName("applicants")
	private List<String> applicants;

	@SerializedName("Title")
	private String title;

	@SerializedName("Token")
	private String token;

	@SerializedName("Latitude")
	private String latitude;

	@SerializedName("City")
	private String city;

	@SerializedName("userId")
	private String userId;

	@SerializedName("Mobile")
	private String mobile;

	@SerializedName("CreateDate")
	private String createDate;

	@SerializedName("Longitude")
	private String longitude;

	@SerializedName("profileimage")
	private String profileimage;

	@SerializedName("Subject")
	private String subject;

	@SerializedName("Instagram")
	private String instagram;

	@SerializedName("IsWoman")
	private boolean isWoman;

	@SerializedName("state")
	private String state;

	@SerializedName("BaseProperty")
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

	public void setCity(String city){
		this.city = city;
	}

	public String getCity(){
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

	public void setSubject(String subject){
		this.subject = subject;
	}

	public String getSubject(){
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