package ir.payebash.Adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import ir.payebash.Holders.CommentRowHolder;
import ir.payebash.Models.CommentModel;
import ir.payebash.R;


public class CommentsAdapter extends RecyclerView.Adapter<CommentRowHolder> {

    private List<CommentModel> feedItemList;
    private ImageLoader imageLoader;
    private Context mContext;

    public CommentsAdapter(Context context, List<CommentModel> feedItemList, ImageLoader imageLoader) {
        this.feedItemList = feedItemList;
        this.imageLoader = imageLoader;
        this.mContext = context;
    }

    @Override
    public CommentRowHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment, null);
        CommentRowHolder mh = new CommentRowHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(CommentRowHolder feedListRowHolder, final int i) {
        if (!feedItemList.get(i).getImage().contains("https:"))
            imageLoader.displayImage(mContext.getString(R.string.url) + "Images/Users/" + feedItemList.get(i).getImage() + ".jpg", feedListRowHolder.img);
        else
            imageLoader.displayImage(feedItemList.get(i).getImage(), feedListRowHolder.img);
        Spannable spannable = new SpannableString(feedItemList.get(i).getName() + " : " + feedItemList.get(i).getDescription());
        int length = feedItemList.get(i).getName().length() + 3;
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#026d37")), length, length + feedItemList.get(i).getDescription().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        feedListRowHolder.txt_title.setText(spannable, TextView.BufferType.SPANNABLE);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }
}
