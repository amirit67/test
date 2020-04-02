package ir.payebash.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.models.CustomGallery;
import ir.payebash.R;


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

                if (i == 0) {
                    Holder.imgQueue.setBackgroundResource(R.drawable.ic_add_image);
                    Holder.imgQueueMultiSelected.setVisibility(View.GONE);
                }
                else if (feed.size() > (i - 1))
                    imageLoader.displayImage("file://" + feed.get(i - 1).sdcardPath, ((ImageHolder) holder).imgQueue, options);
                else{
                    Holder.imgQueue.setBackgroundResource(R.drawable.ic_default_image);
                    Holder.imgQueueMultiSelected.setVisibility(View.GONE);
                }


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

    public List<CustomGallery> getData() {
        return feed;
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