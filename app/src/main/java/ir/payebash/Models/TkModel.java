package ir.payebash.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TkModel {

    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("token_type")
    @Expose
    private String tokenType;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}