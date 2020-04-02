package ir.payebash.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.BuildConfig;
import ir.payebash.classes.HSH;
import ir.payebash.models.event.EventModel;
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
    //private Map<Integer, LinearLayout> details = new HashMap<>();
    //private int s = 0;
    IGetEvent iGetEvent;
    private Context mContext;

    public PayeAdapter(Context context, IGetEvent iGetEvent) {
        this.mContext = context;
        currentDate = new Date();
        this.iGetEvent = iGetEvent;
        Application.getComponent().Inject(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        if (i == VIEW_TYPE_ITEM) {
            ViewDataBinding item = (DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_main2, viewGroup, false));
            return new Paye2Holder(item);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_ITEM;
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {

        if (holder instanceof Paye2Holder) {
            final Paye2Holder Holder;
            try {
                Holder = (Paye2Holder) holder;
                Holder.setIsRecyclable(false);
                Object obj = feed.get(i);
                Holder.bind(obj);

                Holder.txtLoc.setText(HSH.printDifference(simpleDateFormat.parse(feed.get(i).getCreateDate()
                        .replace("T", " ")), currentDate) + " پیش در " + feed.get(i).getCity());

                Holder.rv.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, true);
                Holder.rv.setLayoutManager(layoutManager);
                Holder.rv.addItemDecoration(new OverlapRecyclerViewDecoration(mContext, 0));
                PersonAddedAdapter adapterFriends = new PersonAddedAdapter(mContext, imageLoader);

                Holder.rv.setAdapter(adapterFriends);
                adapterFriends.addItems(feed.get(i).getFollowers());

                imageLoader.displayImage(BuildConfig.BaseUrl + "/Images/payebash/Thumbnail/" + feed.get(i).getImages().split(",")[0] + ".jpg", Holder.imgContent, new ImageLoadingListener() {
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
            iGetEvent.Event(feed.get(i));
        });
    }

    public interface IGetEvent {
        void Event(EventModel eventModel);
    }

    @Override
    public int getItemCount() {
        return (null != feed ? feed.size() : 0);
    }

    public void addItems(List<EventModel> posts) {
        this.feed.addAll(posts);
        notifyItemRangeChanged(feed.size() - posts.size() - 1, posts.size());
    }

    public void setCurrentDate() {
        currentDate = new Date();
    }

    public void ClearFeed() {
        feed.clear();
        notifyDataSetChanged();
    }


    public class Paye2Holder extends RecyclerView.ViewHolder {


        ViewDataBinding binding;

        public TextView txtLoc;
        public RecyclerView rv;
        public ImageView imgContent;

        public Paye2Holder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.txtLoc = binding.getRoot().findViewById(R.id.txt_location);
            this.rv = binding.getRoot().findViewById(R.id.rv);
            this.imgContent = binding.getRoot().findViewById(R.id.img_content);
        }
        public void bind(Object obj) {
            binding.setVariable(BR.eventItem, obj);
            binding.executePendingBindings();
        }
    }
}