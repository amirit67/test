package ir.payebash.Models;

import java.io.Serializable;

public class UserItem implements Serializable {

    public static final long serialVersionUID = 1L;
    public String UserId = "";
    public String FullName = "";
    public String ProfileImage = "";
    public String ServicesIds = "";
    public String IsAuthenticate = "";
    public String Instagram = "";
    public String Telegram = "";
    public String Soroosh = "";
    public String Gmail = "";
    public String Message = "";
    public String Mobile = "";
    public String MobileTemp = "";


    public String getUserId() {
        return UserId;
    }

    public String getFullName() {
        return FullName;
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
}
