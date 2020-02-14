package ir.payebash.viewmodel;

import android.content.Context;
import android.content.Intent;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ir.payebash.Adapters.PayeAdapter;
import ir.payebash.Classes.ItemDecorationAlbumColumns;
import ir.payebash.activities.PostDetailsActivity;
import ir.payebash.models.event.EventModel;
import ir.payebash.models.general.FollwoersItem;

public class EventViewModel extends BaseObservable implements Serializable {

	private ArrayList<EventViewModel> eventModels = new ArrayList<>();

    private Context context;
	public EventViewModel(Context context) {
		this.context = context;
	}

	@Bindable
    public ArrayList<EventViewModel> getEventModels() {
        return eventModels;
    }

	/*public void setEventModels(ArrayList<EventViewModel> eventModels) {
		this.eventModels = eventModels;
		notifyPropertyChanged(BR.eventModels);
	}*/
	/*@BindingAdapter("bind:listitem")
	public static void recyclerViewBinder(final RecyclerView recyclerView, List<EventModel> eventModels)
	{
		//ada
	}*/

}
