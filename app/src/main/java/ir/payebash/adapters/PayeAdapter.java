package ir.payebash.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.BuildConfig;
import ir.payebash.R;
import ir.payebash.classes.HSH;
import ir.payebash.models.event.EventModel;
import ir.payebash.models.user.UserInfoModel;
import ir.payebash.utils.OverlapRecyclerViewDecoration;


public class PayeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
    Date currentDate;
    private final int VIEW_TYPE_ITEM = 1;
    @Inject
    ImageLoader imageLoader;
    private List<EventModel> feed = new ArrayList<>();
    private UserInfoModel item = null;
    //public List<PayeItem> Tempfeed = new ArrayList<>();
    //private Map<Integer, LinearLayout> details = new HashMap<>();
    //private int s = 0;
    IGetEvent iGetEvent;
    IEditProfile iEditProfile;
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
        } else {
            ViewDataBinding item = (DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_user_info, viewGroup, false));
            return new UserInfoHolder(item);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (null != item && position == 0)
            return 2;
        else
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

                holder.itemView.setOnClickListener(v -> {
                    iGetEvent.Event(feed.get(i));
                });
            } catch (Exception e) {
            }
        } else if (holder instanceof UserInfoHolder) {
            /*if (i == 0)
                details.clear();*/
            final UserInfoHolder Holder;
            Holder = (UserInfoHolder) holder;
            Holder.setIsRecyclable(false);
            Object obj = item;
            Holder.bind(obj);
            if (item.getEventOwner().getId().equals(Application.preferences.getString(mContext.getString(R.string.UserId), "")))
                Holder.tvFollow.setVisibility(View.VISIBLE);
            else
                Holder.tvEditProfile.setVisibility(View.VISIBLE);

            imageLoader.displayImage(item.getEventOwner().getProfileImage(), Holder.imgProfile);
            Holder.imgProfile.setOnClickListener(v -> iEditProfile.changePhoto(Holder, 1));
            Holder.tvEditProfile.setOnClickListener(v -> iEditProfile.changePhoto(Holder, 2));
            Holder.tvFollow.setOnClickListener(v -> iEditProfile.changePhoto(Holder, 3));
            holder.itemView.setOnClickListener(v -> {
                iGetEvent.Event(feed.get(i));
            });
        }
    }

    public interface IGetEvent {
        void Event(EventModel eventModel);
    }

    public void EditProfile(IEditProfile iEditProfile) {
        this.iEditProfile = iEditProfile;
    }

    public interface IEditProfile {
        void changePhoto(UserInfoHolder userInfoHolder, int x);
    }

    @Override
    public int getItemCount() {
        return ((null != item && feed.size() == 0) ? 1 : feed.size() > 0 ? feed.size() : 0);
    }

    public void addItems(List<EventModel> posts) {
        this.feed.addAll(posts);
        notifyItemRangeChanged(feed.size() - posts.size() - 1, posts.size());
    }

    public void addUserInfo(UserInfoModel item) {
        this.item = item;
        notifyItemRangeChanged(0, 1);
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

    public class UserInfoHolder extends RecyclerView.ViewHolder {

        ViewDataBinding binding;
        public TextView tvEditProfile, tvFollow;
        public ImageView imgProfile;
        public ProgressBar pb;

        public UserInfoHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.imgProfile = binding.getRoot().findViewById(R.id.img_profile);
            this.tvEditProfile = binding.getRoot().findViewById(R.id.tv_edit_profile);
            this.tvFollow = binding.getRoot().findViewById(R.id.tv_follow);
            this.pb = binding.getRoot().findViewById(R.id.pb);
        }

        public void bind(Object obj) {
            binding.setVariable(BR.userInfoItem, obj);
            binding.executePendingBindings();
        }
    }
}