package ir.payebash.models;

import java.io.Serializable;

public class ProfileItem implements Serializable {

    public static final long serialVersionUID = 1L;
    public String Id = "";
    public String Name = "";
    public String Family = "";
    public String ActivityState = "";
    public String Age = "";
    public String City = "";
    public String IsTrust = "";
    public String Telegram = "";
    public String Instagram = "";
    public String Soroosh = "";
    public String Favorites = "";
    public String TrustedVoteCount = "";
    public String UserAge = "";
    public String AboutMe = "";

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFamily() {
        return Family;
    }

    public String getAge() {
        return Age;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getIsTrust() {
        return IsTrust;
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

    public String getFavorites() {
        return Favorites;
    }

    public String getTrustedVoteCount() {
        return TrustedVoteCount;
    }

    public String getUserAge() {
        return UserAge;
    }

    public String getAboutMe() {
        return AboutMe;
    }

    public String getActivityState() {
        return ActivityState;
    }
}
