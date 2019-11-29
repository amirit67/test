package ir.payebash.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi on 11/8/2017.
 */

@Entity
public class User {
    /*@Ignore
    @SerializedName(value = "contacts", alternate = {"contact"})
    private List<Contact> contacts;*/
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "first_name")
    private String firstName;
    @ColumnInfo(name = "last_name")
    private String lastName;
    @ColumnInfo(name = "n_code")
    @SerializedName("ncode")
    private String nCode;
    @ColumnInfo(name = "picture")
    private String picture;
    @ColumnInfo(name = "birthday")
    private String birthday;
    @ColumnInfo(name = "job")
    private String job;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "mobile")
    private String mobile;
    @ColumnInfo(name = "level")
    private int level;
    @ColumnInfo(name = "date")
    private String date;
    @Ignore
    @ColumnInfo(name = "password")
    private String password;
    @Ignore
    @SerializedName("regkey")
    private String regKey;
    @Ignore
    @ColumnInfo(name = "token_key")
    private String tokenKey;

    public User(long id, String firstName, String lastName, String nCode, String picture, String birthday, String job, String email, String mobile, int level, String date) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nCode = nCode;
        this.picture = picture;
        this.birthday = birthday;
        this.job = job;
        this.email = email;
        this.mobile = mobile;
        this.level = level;
        this.date = date;
    }

    @Ignore
    public User(String firstName, String lastName, String nCode, String birthday, String job, String email, String mobile) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nCode = nCode;
        this.birthday = birthday;
        this.job = job;
        this.email = email;
        this.mobile = mobile;
    }

    @Ignore
    public User(String firstName, String lastName, String mobile, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.mobile = mobile;
    }

    @Ignore
    public User(String mobile, String password) {
        this.mobile = mobile;
        this.password = password;
    }

    @Ignore
    public User(String mobile) {
        this.mobile = mobile;
    }

    @Ignore
    public User() {
    }

    /*public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }*/

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public String getRegKey() {
        return regKey;
    }

    public void setRegKey(String regKey) {
        this.regKey = regKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNCode() {
        return nCode;
    }

    public void setNCode(String nCode) {
        this.nCode = nCode;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
