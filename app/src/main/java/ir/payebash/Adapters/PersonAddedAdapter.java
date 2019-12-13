package ir.payebash.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import ir.payebash.Activities.PostDetailsActivity;
import ir.payebash.Application;
import ir.payebash.Models.PayeItem;
import ir.payebash.Models.event.ApplicantsItem;
import ir.payebash.R;
import ir.payebash.utils.roundedimageview.InsLoadingView;


public class PersonAddedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 1;
    ImageLoader imageLoader;
    private List<ApplicantsItem> feed = new ArrayList<>();
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
                        imageLoader.displayImage(mContext.getString(R.string.url) + "Images/Users/" + feed.get(i).getProfileImage() + ".jpg", Holder.iv_image);
                    else
                        imageLoader.displayImage(feed.get(i).getProfileImage(), Holder.iv_image);

                } catch (Exception e) {
                    Holder.iv_image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_paye));
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != feed ? feed.size() < 4 ? feed.size() : 3 : 0);
    }

    public void addItems(List<ApplicantsItem> posts) {
        this.feed.addAll(posts);
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