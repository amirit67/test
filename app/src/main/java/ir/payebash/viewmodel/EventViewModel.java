package ir.payebash.viewmodel;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;
import java.util.ArrayList;

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
