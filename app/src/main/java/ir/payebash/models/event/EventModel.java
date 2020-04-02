package ir.payebash.models.event;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.SerializedName;

import ir.payebash.R;
import ir.payebash.models.general.FollwoersItem;

public class EventModel extends BaseObservable implements Serializable {

	@SerializedName("isWoman")
	private boolean isWoman;

	@SerializedName("images")
	private String images;

	@SerializedName("cost")
	private String cost;

	@SerializedName("city")
	private String city;

	@SerializedName("users")
	private List<FollwoersItem> follwes;

	@SerializedName("isImmediate")
	private boolean isImmediate;

	@SerializedName("service")
	private int service;

	@SerializedName("Id")
	private String id;

	@SerializedName("title")
	private String title;

	@SerializedName("createDate")
	private String createDate;

	@SerializedName("numberFollowers")
	private String numberFollowers;

	Context context;



	/*public EventModel(Context context) {
		this.context = context;
	}*/

	public void setPostId(String id) {
		this.id = id;
	}

	@Bindable
	public boolean getIsWoman(){
		return isWoman;
	}

	public String getImages(){
		return images;
	}

	@Bindable
	public String getCost(){
		return cost;
	}

	public void setCity(String city) {
		this.city = city;
		notifyPropertyChanged(BR.eventItem);
	}

	public String getCity(){
		return city;
	}

	public List<FollwoersItem> getFollowers(){
		return null != follwes ? follwes : new ArrayList<>();
	}

	@Bindable
	public boolean getIsImmediate(){
		return isImmediate;
	}

	public int getService(){
		return service;
	}

	public String getPostId(){
		return id;
	}

	@Bindable
	public String getTitle(){
		return title;
	}

	public String getCreateDate(){
		return createDate;
	}

	@Bindable
	public String getNumberFollowers() {
		return numberFollowers;
	}
}
