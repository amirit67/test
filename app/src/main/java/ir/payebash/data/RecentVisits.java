package ir.payebash.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alireza Eskandarpour Shoferi on 11/8/2017.
 */

@Entity
public class RecentVisits {
    /*@Ignore
    @SerializedName(value = "contacts", alternate = {"contact"})
    private List<Contact> contacts;*/
    @NonNull
    @PrimaryKey
    private String id;
    @ColumnInfo(name = "is_beup")
    private String isBeup;
    @ColumnInfo(name = "is_favorite")
    private String isFavorite;
    @ColumnInfo(name = "is_wanted")
    private String isWanted;
    @ColumnInfo(name = "is_paied")
    private String isPaied;
    @ColumnInfo(name = "is_successed")
    private String isSuccessed;
    @ColumnInfo(name = "is_mine")
    private String isMine;
    @ColumnInfo(name = "ref_id")
    private String refId;


    public RecentVisits(String id, String isBeup, String isFavorite, String isWanted, String isPaied, String isSuccessed, String isMine, String refId) {
        this.id = id;
        this.isBeup = isBeup;
        this.isFavorite = isFavorite;
        this.isWanted = isWanted;
        this.isPaied = isPaied;
        this.isSuccessed = isSuccessed;
        this.isMine = isMine;
        this.refId = refId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsBeup() {
        return isBeup;
    }

    public void setIsBeup(String isBeup) {
        this.isBeup = isBeup;
    }

    public String getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(String isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getIsWanted() {
        return isWanted;
    }

    public void setIsWanted(String isWanted) {
        this.isWanted = isWanted;
    }

    public String getIsPaied() {
        return isPaied;
    }

    public void setIsPaied(String isPaied) {
        this.isPaied = isPaied;
    }

    public String getIsSuccessed() {
        return isSuccessed;
    }

    public void setIsSuccessed(String isSuccessed) {
        isSuccessed = isSuccessed;
    }

    public String getIsMine() {
        return isMine;
    }

    public void setIsMine(String isMine) {
        isMine = isMine;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    @Ignore
    public RecentVisits() {
    }

    /*public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }*/


}
