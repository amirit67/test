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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import ir.payebash.Activities.PostDetailsActivity;
import ir.payebash.Activities.ViewPagerActivity;
import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.DI.ImageLoaderMoudle;
import ir.payebash.Fragments.SearchFragment;
import ir.payebash.Holders.PayeHolder;
import ir.payebash.Models.PayeItem;
import ir.payebash.Moudle.CircleImageView;
import ir.payebash.Moudle.TriangleLabelView;
import ir.payebash.R;
import ir.payebash.utils.roundedimageview.OverlapRecyclerViewDecoration;


public class PayeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 1;
    //private final int VIEW_TYPE_LOADING = 2;
    @Inject
    ImageLoader imageLoader;
    private List<PayeItem> feed = new ArrayList<>();
    //public List<PayeItem> Tempfeed = new ArrayList<>();
    private Map<String, String> params;
    //private Map<Integer, LinearLayout> details = new HashMap<>();
    //private int s = 0;
    private SearchFragment fragobj = null;
    private Context mContext;

    public PayeAdapter(Context context, Map<String, String> params) {
        this.mContext = context;
        this.params = params;
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

                Holder.rv.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, true);
                Holder.rv.setLayoutManager(layoutManager);
                Holder.rv.addItemDecoration(new OverlapRecyclerViewDecoration(mContext, 0));
                PersonAddedAdapter adapterFriends = new PersonAddedAdapter(mContext, imageLoader);

                Holder.rv.setAdapter(adapterFriends);
                //adapterFriends.addItems(feed);

                imageLoader.displayImage(mContext.getString(R.string.url) + "Images/payebash/Thumbnail/" + feed.get(i).getImages().split(",")[0] + ".jpg", Holder.img_content, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                    }
                });
               /* String city = mContext.getResources().getStringArray(R.array.Citys)[feed.get(i).getCity() - 2];
                Holder.txt_loc.setText(feed.get(i).getCreateDate() + " در " + city.substring(city.indexOf("-") + 2));
                Holder.txt_cost.setText(feed.get(i).getCost());
                Holder.txt_title.setText(feed.get(i).getTitle());
                Holder.txt_deadline.setText(feed.get(i).getTimeToJoin());
                HSH.vectorRight(mContext, Holder.txt_cost, R.drawable.ic_coins);
                HSH.vectorRight(mContext, Holder.txt_deadline, R.drawable.ic_hourglass);

                //s = 0;
                *//*try {
                    s = details.get(i).getChildCount();
                } catch (Exception e) {
                }*//*
                if (*//*s == 0 && *//*feed.get(i).getTag().length() > 3) {
                    String[] temp = feed.get(i).getTag().split("#");
                    for (int j = 0; j < temp.length; j++) {
                        if (!temp[j].equals(""))
                            TagLayout(temp[j], Holder.mTagLayout, j, i);
                    }
                } *//*else if (s > 0) {
                    LinearLayout v;
                    v = details.get(i);
                    ((TagLayout2) v.getParent()).removeViewAt(0);
                    Holder.mTagLayout.addView(v);
                }*//* else
                    Holder.rl_tag.setVisibility(View.GONE);

                if (Application.myAds == 1) {
                    if (feed.get(i).IsImmediate()) {
                        Holder.lbl_state.setVisibility(View.VISIBLE);
                        Holder.lbl_state.setTriangleBackgroundColor(Color.parseColor("#C8FF0004"));
                        Holder.lbl_state.setPrimaryText("فوری");
                    } else
                        Holder.lbl_state.setVisibility(feed.get(i).IsWoman() == true ? View.VISIBLE : View.GONE);
                } else {
                    Holder.lbl_state.setVisibility(View.VISIBLE);
                    Holder.lbl_state.setPrimaryText(feed.get(i).getState().split("-")[0]);
                    Holder.lbl_state.setTriangleBackgroundColor(Color.parseColor(feed.get(i).getState().split("-")[2]));
                }


                if (!feed.get(i).getImages().equals("null"))
                    Holder.img_post.setOnClickListener(v -> {
                        String s = feed.get(i).getImages().split(",")[0];
                        if (!s.equals("")) {
                            feed.get(i).setImages(mContext.getString(R.string.url) + "Images/payebash/Thumbnail/" + s + ".jpg");
                            final Bundle bundle = new Bundle();
                            bundle.putSerializable("feed", feed.get(i));
                            Intent in = new Intent(mContext, ViewPagerActivity.class);
                            in.putExtras(bundle);
                            mContext.startActivity(in);
                            feed.get(i).setImages(s);
                        }
                    });*/
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

    private void TagLayout(final String title, final LinearLayout mTagLayout, final int j, final int i) {
        try {
            final TextView tagView = new TextView(mContext);
            tagView.setText("#" + title);
            tagView.setBackgroundResource(R.drawable.tagview_shape2);
            tagView.setTextColor(Color.BLACK);
            tagView.setPadding(2, 2, 2, 2);
            tagView.setTextSize(11);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            param.setMargins(0, 0, 5, 0);
            tagView.setLayoutParams(param);
            mTagLayout.addView(tagView);
            /*try {
                details.remove(i);
            } catch (Exception e) {
            }*/
            //details.put(i, (LinearLayout) mTagLayout.getChildAt(0));
                   /* tagView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.checked,0,0);
                    tagView.setCompoundDrawablePadding(50);*/
            tagView.setOnClickListener(v -> {
                //details.clear();
                //pb.setVisibility(View.VISIBLE);



                /*Cnt = 0;
                params.clear();
                params.put("ContentSearch", tagView.getText().toString().replace("#", "").trim());
                params.put(mContext.getString(R.string.Skip), String.valueOf(Cnt));*/

                Bundle bundle = new Bundle();
                bundle.putSerializable("HashMap", (Serializable) params);
                //set Fragmentclass Arguments
                fragobj = new SearchFragment();
                fragobj.setArguments(bundle);
                HSH.openFragment((Activity) mContext, fragobj);
                //GetPosts();
            });

        } catch (Exception e) {
        }
    }

    /*public void img_map(int i, ImageView img_map) {
        if (!feed.get(i).getLatitude().equals("00")) {
            String img_url = "https://maps.googleapis.com/maps/api/staticmap?center=" + feed.get(i).getLatitude() + "," + feed.get(i).getLongitude() + "&maptype=roadmap&zoom=14&size=512x512&language=FA&markers=size:small%7Ccolor:0xff0000%7Clabel:1%7C" + feed.get(i).getLatitude() + "," + feed.get(i).getLongitude();
            imageLoader.displayImage(img_url, img_map);
        }
    }*/


    public class Paye2Holder extends RecyclerView.ViewHolder {
        public RecyclerView rv;
        public ImageView img_content;
                public Paye2Holder(View view) {
                    super(view);
            this.rv = view.findViewById(R.id.rv);
            this.img_content = view.findViewById(R.id.img_content);
        }

    }
}