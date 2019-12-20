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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
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
import ir.payebash.helpers.Globals;
import ir.payebash.modelviewsChat.RoomViewModel;
import ir.payebash.utils.OverlapRecyclerViewDecoration;


public class RoomsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 1;
    //private final int VIEW_TYPE_LOADING = 2;
    @Inject
    ImageLoader imageLoader;
    @Inject
    DisplayImageOptions options;
    private IGetRoom iGetRoom;
    private Context mContext;

    public RoomsAdapter(Context context, IGetRoom iGetRoom) {
        this.mContext = context;
        this.iGetRoom = iGetRoom;
        Application.getComponent().Inject(this);
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

    public interface IGetRoom
    {
        void Room(RoomViewModel room);
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {

        if (holder instanceof RoomHolder) {
            /*if (i == 0)
                details.clear();*/
            final RoomHolder Holder;
            try {
                Holder = (RoomHolder) holder;
                Holder.setIsRecyclable(false);
                Holder.txtTitle.setText(Globals.Rooms.get(i).name);
                Holder.txtHost.setText(Globals.Rooms.get(i).host);
                Holder.txtTimetojoin.setText(Globals.Rooms.get(i).timeToJoin);
                imageLoader.displayImage(Globals.Rooms.get(i).image, Holder.imgEvent);

            } catch (Exception e) {
            }
        }

        holder.itemView.setOnClickListener(v -> {
            iGetRoom.Room(Globals.Rooms.get(i));
        });
    }

    @Override
    public int getItemCount() {
        return (null !=  Globals.Rooms ?  Globals.Rooms.size() : 0);
    }

    public void addItems(ArrayList<RoomViewModel> posts) {
        Globals.Rooms.addAll(posts);
        notifyDataSetChanged();
    }

    public void ClearFeed() {
        /*Tempfeed.clear();
        Tempfeed.addAll(feed);*/
        Globals.Rooms.clear();
        notifyDataSetChanged();
    }

    public class RoomHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle, txtHost, txtTimetojoin;
        public ImageView imgEvent;
                public RoomHolder(View view) {
            super(view);
            this.txtTitle = view.findViewById(R.id.txt_title);
            this.txtHost = view.findViewById(R.id.txt_host);
            this.txtTimetojoin = view.findViewById(R.id.txt_time_to_join);
            this.imgEvent = view.findViewById(R.id.img_event);
        }

    }
}