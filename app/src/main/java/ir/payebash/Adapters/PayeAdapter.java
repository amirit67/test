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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import ir.payebash.Activities.MyEventDetailsActivity;
import ir.payebash.Activities.PostDetailsActivity;
import ir.payebash.Activities.ViewPagerActivity;
import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.DI.ImageLoaderMoudle;
import ir.payebash.Fragments.SearchFragment;
import ir.payebash.Holders.PayeHolder;
import ir.payebash.Models.PayeItem;
import ir.payebash.Models.event.EventModel;
import ir.payebash.Moudle.CircleImageView;
import ir.payebash.Moudle.TriangleLabelView;
import ir.payebash.R;
import ir.payebash.utils.OverlapRecyclerViewDecoration;


public class PayeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
    Date currentDate;
    private final int VIEW_TYPE_ITEM = 1;
    @Inject
    ImageLoader imageLoader;
    private List<EventModel> feed = new ArrayList<>();
    //public List<PayeItem> Tempfeed = new ArrayList<>();
    private Map<String, String> params;
    //private Map<Integer, LinearLayout> details = new HashMap<>();
    //private int s = 0;
    private Context mContext;

    public PayeAdapter(Context context, Map<String, String> params) {
        this.mContext = context;
        this.params = params;
        currentDate = new Date();
        Application.getComponent().Inject(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        if (i == VIEW_TYPE_ITEM) {
            View v;
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_main2, null);
            return new Paye2Holder(v);
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

        if (holder instanceof Paye2Holder) {
            /*if (i == 0)
                details.clear();*/
            final Paye2Holder Holder;
            try {
                Holder = (Paye2Holder) holder;
                Holder.setIsRecyclable(false);

                Holder.txtTitle.setText(feed.get(i).getTitle());
                String city = mContext.getResources().getStringArray(R.array.Citys)[feed.get(i).getCity() - 2];
                Holder.txtLoc.setText(HSH.printDifference(simpleDateFormat.parse(feed.get(i).getCreateDate()
                        .replace("T", " ")), currentDate) + " پیش در " + city.substring(city.indexOf("-") + 2));
                Holder.txtCost.setText(feed.get(i).getCost());
                Holder.imgWoman.setVisibility(feed.get(i).IsWoman() ? View.VISIBLE : View.GONE);
                Holder.imgImmadiate.setVisibility(feed.get(i).IsImmediate() ? View.VISIBLE : View.GONE);
                Holder.tvNumberFollowers.setText(feed.get(i).getNumberFollowers());

                Holder.rv.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, true);
                Holder.rv.setLayoutManager(layoutManager);
                Holder.rv.addItemDecoration(new OverlapRecyclerViewDecoration(mContext, 0));
                PersonAddedAdapter adapterFriends = new PersonAddedAdapter(mContext, imageLoader);

                Holder.rv.setAdapter(adapterFriends);
                adapterFriends.addItems(feed.get(i).getFollowers());

                imageLoader.displayImage(mContext.getString(R.string.url) + "Images/payebash/Thumbnail/" + feed.get(i).getImages().split(",")[0] + ".jpg", Holder.imgContent, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        Holder.imgContent.setBackgroundResource(R.drawable.ic_thumbnail);
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

    public void addItems(List<EventModel> posts) {
        this.feed.addAll(posts);
        notifyDataSetChanged();
    }

    public void ClearFeed() {
        /*Tempfeed.clear();
        Tempfeed.addAll(feed);*/
        feed.clear();
        notifyDataSetChanged();
    }


    public class Paye2Holder extends RecyclerView.ViewHolder {
        public TextView txtTitle, txtLoc, txtCost, tvNumberFollowers;
        public RecyclerView rv;
        public ImageView imgContent, imgWoman, imgImmadiate;

        public Paye2Holder(View view) {
            super(view);
            this.txtTitle = view.findViewById(R.id.txt_title);
            this.txtLoc = view.findViewById(R.id.txt_location);
            this.txtCost = view.findViewById(R.id.txt_cost);
            this.tvNumberFollowers = view.findViewById(R.id.tv_number_followers);
            this.rv = view.findViewById(R.id.rv);
            this.imgContent = view.findViewById(R.id.img_content);
            this.imgWoman = view.findViewById(R.id.img_woman);
            this.imgImmadiate = view.findViewById(R.id.img_immadiate);
        }

    }
}