package ir.payebash.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PayeDetailsModel implements Serializable {

    private static final long serialVersionUID = 1L;
    public List<String> BaseProperty = new ArrayList<>();
    public List<String> applicants = new ArrayList<>();
    public int Subject;
    public String state;
    public String Token;
    public String Images = "";
    public String CreateDate = "";
    public String profileimage = "";
    public String Title = "";
    public String City = "";
    public String Telegram = "";
    public String Instagram = "";
    public String Soroosh = "";
    public String Gmail = "";
    public String Mobile = "";
    public String username = "";
    public String userId = "";
    public String Longitude = "";
    public String Latitude = "";

    public String getSoroosh() {
        return Soroosh;
    }

    public List<String> getBaseProperty() {
        return BaseProperty;
    }

    public int getSubject() {
        return Subject;
    }

    public void setSubject(int subject) {
        Subject = subject;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getImages() {
        return Images;
    }

    public void setImages(String images) {
        Images = images;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getTelegram() {
        return Telegram;
    }

    public String getInstagram() {
        return Instagram;
    }

    public String getPhoneNumber() {
        return Mobile;
    }

    public String getGmail() {
        return Gmail;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getLatitude() {
        return Latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public List<String> getApplicants() {
        return applicants;
    }

    public String getToken() {
        return Token;
    }
}
