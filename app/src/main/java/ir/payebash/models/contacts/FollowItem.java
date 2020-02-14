package ir.payebash.models.contacts;

import com.google.gson.annotations.SerializedName;

public class FollowItem {

	@SerializedName("isFollow")
	private String isFollow;

	public String getIsFollow(){
		return isFollow;
	}
}