package ir.payebash.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import ir.payebash.Activities.PostDetailsActivity;
import ir.payebash.Application;
import ir.payebash.Fragments.SearchFragment;
import ir.payebash.Models.CustomGallery;
import ir.payebash.Models.PayeItem;
import ir.payebash.R;
import ir.payebash.utils.OverlapRecyclerViewDecoration;


public class ImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CustomGallery> feed = new ArrayList<>();
    private IGetImage iGetImage;
    private Context mContext;
    @Inject
    ImageLoader imageLoader;
    @Inject
    DisplayImageOptions options;

    public ImagesAdapter(Context context, IGetImage iGetImage) {
        this.mContext = context;
        this.iGetImage = iGetImage;
        Application.getComponent().Inject(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gallery, null);
        return new ImageHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {

        if (holder instanceof ImageHolder) {

            final ImageHolder Holder;
            try {
                Holder = (ImageHolder) holder;
                Holder.setIsRecyclable(false);

                if (i == 0)
                    Holder.imgQueue.setBackgroundResource(R.drawable.ic_add_image);
                else if (feed.size() > (i - 1))
                    imageLoader.displayImage("file://" + feed.get(i - 1).sdcardPath, ((ImageHolder) holder).imgQueue, options);
                else
                    Holder.imgQueue.setBackgroundResource(R.drawable.ic_default_image);

                holder.itemView.setOnClickListener(v ->
                        iGetImage.getImage(i));

            } catch (Exception e) {
            }
        }
    }

    @Override
    public int getItemCount() {
        return ((null != feed && feed.size() > 3) ? (feed.size() + 1) : 4);
    }

    public void addItems(CustomGallery posts) {
        this.feed.add(posts);
        notifyDataSetChanged();
    }

    public void ClearFeed() {
        feed.clear();
        notifyDataSetChanged();
    }

    public interface IGetImage {
        void getImage(int position);
    }


    public class ImageHolder extends RecyclerView.ViewHolder {
        ImageView imgQueue;
        ImageView imgQueueMultiSelected;

        public ImageHolder(View view) {
            super(view);
            this.imgQueue = view.findViewById(R.id.imgQueue);
            this.imgQueueMultiSelected = view.findViewById(R.id.imgQueueMultiSelected);
        }

    }
}