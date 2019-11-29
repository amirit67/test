package ir.payebash.Adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.islamkhsh.CardSliderAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.Holders.CommentRowHolder;
import ir.payebash.Interfaces.IWebservice.setListenerCity;
import ir.payebash.Models.CityItem;
import ir.payebash.Moudle.CircleImageView;
import ir.payebash.R;


public class BannerAdapter extends CardSliderAdapter<String> {

    ImageLoader imageLoader;
    ArrayList<String> feed;

    public BannerAdapter(ArrayList<String> feed, ImageLoader imageLoader){
        super(feed);
        this.feed = feed;
        this.imageLoader = imageLoader;
    }

    @Override
    public void bindView(int position, View itemContentView, String item) {
        //TODO bind item object with item layout view
        BannerRowHolder mh = new BannerRowHolder(itemContentView);
        imageLoader.displayImage(feed.get(position), mh.img);
    }

    @Override
    public int getItemContentLayout(int position) {

        return R.layout.render_type_text;

    }

    public class BannerRowHolder extends RecyclerView.ViewHolder {
        //public TextView txt_title;
        public ImageView img;

        public BannerRowHolder(View view) {
            super(view);
            img = view.findViewById(R.id.slider_image);
            //this.txt_title = view.findViewById(R.id.txt1);
        }

    }
}