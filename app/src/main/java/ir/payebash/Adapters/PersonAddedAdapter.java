package ir.payebash.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import ir.payebash.models.general.FollwoersItem;
import ir.payebash.R;


public class PersonAddedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 1;
    ImageLoader imageLoader;
    private List<FollwoersItem> feed = new ArrayList<>();
    private Context mContext;

    public PersonAddedAdapter(Context context, ImageLoader imageLoader) {
        this.mContext = context;
        this.imageLoader = imageLoader;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        if (i == VIEW_TYPE_ITEM) {
            View v;
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_person_added, null);
            return new PersonAddedHolder(v);
        } /*else if (i == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading, null);
            return new LoadingViewHolder(view);
        }*/
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        /*if (feed.get(position) == null)
            return VIEW_TYPE_LOADING;
        else*/
        return VIEW_TYPE_ITEM;
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {

        if (holder instanceof PersonAddedHolder) {
            final PersonAddedHolder Holder;
            try {
                Holder = (PersonAddedHolder) holder;
                Holder.setIsRecyclable(false);

                try {
                    if (!feed.get(i).getProfileImage().contains("https:"))
                        imageLoader.displayImage("http://paye.ariaapps.ir" + "/Images/Users/" + feed.get(i).getProfileImage() + ".jpg", Holder.iv_image);
                    else
                        imageLoader.displayImage(feed.get(i).getProfileImage(), Holder.iv_image);

                } catch (Exception e) {
                    Holder.iv_image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_add_event));
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != feed ? feed.size() < 4 ? feed.size() : 3 : 1);
    }

    public void addItems(List<FollwoersItem> events) {
        if (null != events)
            this.feed.addAll(events);
        notifyDataSetChanged();
    }

    public class PersonAddedHolder extends RecyclerView.ViewHolder {
        public ImageView iv_image;

        public PersonAddedHolder(View view) {
            super(view);
            this.iv_image = view.findViewById(R.id.iv_image);
        }

    }
}