package ir.payebash.models.event.detail;

import android.text.TextUtils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EventOwnerItem extends BaseObservable implements Serializable {

	@SerializedName("id")
	private String id;

	@SerializedName("profileImage")
	private String profileImage;

	@SerializedName("userName")
	private String userName;

	@SerializedName("token")
	private String token;

	@SerializedName("vote")
	private String vote;

	@SerializedName("rate")
	private String rate;

	@SerializedName("trustPercentage")
	private double trustPercentage;

	@SerializedName("Name")
	private String name;

	@SerializedName("AboutMe")
	private String aboutMe;

	@SerializedName("VerifiedAccount")
	private boolean verifiedAccount;

	public String getVote() {
		return vote;
	}

	public String getId(){
		return id;
	}

	@Bindable
	public String getProfileImage(){
		return profileImage;
	}

	@Bindable
	public String getUserName(){
		return userName;
	}

	public String getToken() {
		return token;
	}

	@Bindable
	public double getTrustPercentage() {
		return trustPercentage;
	}

	public String getRate() {
		return TextUtils.isEmpty(rate) ? "0.0" : rate;
	}

	@Bindable
	public String getName(){
		return name;
	}

	public String getAboutMe() {
		return aboutMe;
	}

	public boolean getVerifiedAccount() {
		return verifiedAccount;
	}

}