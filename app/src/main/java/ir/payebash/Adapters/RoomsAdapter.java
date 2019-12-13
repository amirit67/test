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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import ir.payebash.Activities.PostDetailsActivity;
import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.Fragments.SearchFragment;
import ir.payebash.Models.PayeItem;
import ir.payebash.R;
import ir.payebash.utils.OverlapRecyclerViewDecoration;


public class RoomsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 1;
    //private final int VIEW_TYPE_LOADING = 2;
    @Inject
    ImageLoader imageLoader;
    private List<PayeItem> feed = new ArrayList<>();
    private SearchFragment fragobj = null;
    private Context mContext;

    public RoomsAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        if (i == VIEW_TYPE_ITEM) {
            View v;
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_room, null);
            return new RoomHolder(v);
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

        if (holder instanceof RoomHolder) {
            /*if (i == 0)
                details.clear();*/
            final RoomHolder Holder;
            try {
                Holder = (RoomHolder) holder;
                Holder.setIsRecyclable(false);

            } catch (Exception e) {
            }
        }

        holder.itemView.setOnClickListener(v -> {
            final int pos = i;
            Intent intent;
            intent = new Intent(mContext, PostDetailsActivity.class);
            intent.putExtra("feedItem", feed.get(pos));
            intent.putExtra(mContext.getString(R.string.myAds), Application.myAds);
            if (Application.myAds == 42907631) {
                ((Activity) mContext).startActivityForResult(intent, 321);
            } else
                mContext.startActivity(intent);
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

    public void ClearFeed() {
        /*Tempfeed.clear();
        Tempfeed.addAll(feed);*/
        feed.clear();
        notifyDataSetChanged();
    }

    public class RoomHolder extends RecyclerView.ViewHolder {
       /* public RecyclerView rv;
        public ImageView img_content;*/
                public RoomHolder(View view) {
            super(view);
            /*this.rv = view.findViewById(R.id.rv);
            this.img_content = view.findViewById(R.id.img_content);*/
        }

    }
}