package ir.payebash.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ir.payebash.Activities.PostDetailsActivity;
import ir.payebash.Activities.ViewPagerActivity;
import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.Fragments.SearchFragment;
import ir.payebash.Holders.PayeHolder;
import ir.payebash.Models.PayeItem;
import ir.payebash.Moudle.CircleImageView;
import ir.payebash.R;
import ir.payebash.utils.roundedimageview.InsLoadingView;


public class StoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 1;
    ImageLoader imageLoader;
    private List<PayeItem> feed = new ArrayList<>();
    private Context mContext;

    public StoryAdapter(Context context, ImageLoader imageLoader) {
        this.mContext = context;
        this.imageLoader = imageLoader;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        if (i == VIEW_TYPE_ITEM) {
            View v;
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_story, null);
            return new StoryHolder(v);
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

        if (holder instanceof StoryHolder) {
            final StoryHolder Holder;
            try {
                Holder = (StoryHolder) holder;
                Holder.setIsRecyclable(false);
                Holder.txtName.setText(feed.get(i).getTitle());
                imageLoader.displayImage(mContext.getString(R.string.url) + "Images/payebash/Thumbnail/" + feed.get(i).getImages().split(",")[0] + ".jpg", Holder.imgStory, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                       /* if (!imageUri.contains("/.jpg") && !imageUri.contains("/null.jpg"))
                            img_map(i, Holder.img_post);*/
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                    }
                });
            } catch (Exception e) {
            }
        }

        holder.itemView.setOnClickListener(v -> {
            final int pos = i;
            Intent intent;
            intent = new Intent(mContext, PostDetailsActivity.class);
            intent.putExtra("feedItem", feed.get(pos));
            intent.putExtra(mContext.getString(R.string.myAds), Application.myAds);
          /*  if (Application.myAds == 42907631) {
                ((Activity) mContext).startActivityForResult(intent, 321);
            } else
                mContext.startActivity(intent);*/
        });
    }

    @Override
    public int getItemCount() {
        return (null != feed ? feed.size() : 0);
    }

    public void addItems(List<PayeItem> posts) {
        this.feed.addAll(posts);
        notifyDataSetChanged();
    }

    public class StoryHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public InsLoadingView imgStory;

        public StoryHolder(View view) {
            super(view);
            imgStory = view.findViewById(R.id.img_story);
            this.txtName = view.findViewById(R.id.txt_name);
        }

    }
}