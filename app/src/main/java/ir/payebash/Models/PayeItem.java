package ir.payebash.Models;

import java.io.Serializable;

public class PayeItem implements Serializable {

    public static final long serialVersionUID = 1L;
    public String PostId = "";
    public String Title = "";
    public String Subject = "";
    public int City;
    public boolean IsWoman = false;
    public boolean IsImmediate = false;
    public String Cost = "";
    public String Images = "";
    public String CreateDate = "";
    public String Deadline = "";
    public String Tag = "";
    public String Longitude = "";
    public String Latitude = "";
    public String CityDate = "";
    public String State = "";

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public int getCity() {
        return City;
    }

    public void setCity(int city) {
        City = city;
    }

    public boolean getIsWoman() {
        return IsWoman;
    }

    public boolean getIsImmediate() {
        return IsImmediate;
    }

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
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

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public String getDeadline() {
        return Deadline;
    }

    public void setDeadline(String deadline) {
        Deadline = deadline;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getCityDate() {
        return CityDate;
    }

    public void setCityDate(String cityDate) {
        CityDate = cityDate;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

}
