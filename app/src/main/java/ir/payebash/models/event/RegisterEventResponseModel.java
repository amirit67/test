package ir.payebash.models.event;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ir.payebash.models.general.FollwoersItem;

public class RegisterEventResponseModel extends BaseObservable implements Serializable {

	@SerializedName("postId")
	private String postId;

	@SerializedName("mobileConfirmed")
	private boolean mobileConfirmed;

	public String getPostId() {
		return postId;
	}

	public boolean getMobileConfirmed() {
		return mobileConfirmed;
	}
}
