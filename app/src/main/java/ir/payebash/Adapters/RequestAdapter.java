package ir.payebash.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.models.RequestItem;
import ir.payebash.R;


public class RequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 1;
    //private final int VIEW_TYPE_LOADING = 2;
    @Inject
    ImageLoader imageLoader;
    @Inject
    DisplayImageOptions options;
    private IGetRequest iGetRequest ;
    private Context mContext;
    List<RequestItem> feed = new ArrayList<>();
    //private Roozh jCal = new Roozh();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());

    public RequestAdapter(Context context, IGetRequest iGetRequest) {
        this.mContext = context;
        this.iGetRequest = iGetRequest;
        Application.getComponent().Inject(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        if (i == VIEW_TYPE_ITEM) {
            View v;
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_request, null);
            return new RequestHolder(v);
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

    public interface IGetRequest {
        void Request(RequestItem requestItem);
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {

        if (holder instanceof RequestHolder) {
            /*if (i == 0)
                details.clear();*/
            final RequestHolder Holder;
            try {
                Holder = (RequestHolder) holder;
                Holder.setIsRecyclable(false);
                Holder.tvEventName.setText(feed.get(i).getEventTitle());
                Holder.tvApplicantName.setText(feed.get(i).getApplicantName());
                Holder.tvApplicantUsername.setText(feed.get(i).getApplicantUsername());
                //String[] date = feed.get(i).getCreateDate().split("-");
                //jCal.GregorianToPersian(Integer.parseInt(date[0]),Integer.parseInt(date[1]), Integer.parseInt(date[2].substring(0, date[2].lastIndexOf("T"))));
                Holder.tvDatetime.setText(HSH.printDifference(simpleDateFormat.parse(feed.get(i).getCreateDate()
                        .replace("T", " ")), new Date()) + " پیش");
                imageLoader.displayImage(feed.get(i).getProfileImage(), Holder.imgApplicant);

            } catch (Exception e) {
            }
        }

        holder.itemView.setOnClickListener(v -> {
            iGetRequest.Request(feed.get(i));
        });
    }

    @Override
    public int getItemCount() {
        return (null != feed ? feed.size() : 0);
    }

    public void addItems(List<RequestItem> posts) {
        feed.addAll(posts);
        notifyDataSetChanged();
    }

    public void ClearFeed() {
        /*Tempfeed.clear();
        Tempfeed.addAll(feed);*/
        feed.clear();
        notifyDataSetChanged();
    }

    public class RequestHolder extends RecyclerView.ViewHolder {
        public TextView tvEventName, tvApplicantName, tvApplicantUsername, tvDatetime;
        public ImageView imgApplicant;

        public RequestHolder(View view) {
            super(view);
            this.tvEventName = view.findViewById(R.id.tv_event_name);
            this.tvApplicantName = view.findViewById(R.id.tv_applicant_name);
            this.tvApplicantUsername = view.findViewById(R.id.tv_applicant_username);
            this.tvDatetime = view.findViewById(R.id.tv_datetime);
            this.imgApplicant = view.findViewById(R.id.img_applicant);
        }

    }
}