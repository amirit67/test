package ir.payebash.Models;

import java.io.Serializable;

public class UserItem implements Serializable {

    public static final long serialVersionUID = 1L;
    public String UserId = "";
    public String UserName = "";
    public String Name = "";
    public String ProfileImage = "";
    public String ServicesIds = "";
    public String IsAuthenticate = "";
    public String Instagram = "";
    public String Telegram = "";
    public String Soroosh = "";
    public String Gmail = "";
    public String Email = "";
    public String Message = "";
    public String Mobile = "";
    public String MobileTemp = "";


    public String getUserId() {
        return UserId;
    }

    public String getFullName() {
        return Name;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public String getServicesIds() {
        return ServicesIds;
    }

    public String getIsAuthenticate() {
        return IsAuthenticate;
    }

    public String getInstagram() {
        return Instagram;
    }

    public String getTelegram() {
        return Telegram;
    }

    public String getSoroosh() {
        return Soroosh;
    }

    public String getGmail() {
        return Gmail;
    }

    public String getMessage() {
        return Message;
    }

    public String getMobile() {
        return Mobile;
    }

    public String getMobileTemp() {
        return MobileTemp;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setFullName(String fullName) {
        Name = fullName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
