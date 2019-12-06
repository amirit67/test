package ir.payebash.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.R;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {

    private final List<String> mValues;
    private Context fragment;
    @Inject
    ImageLoader imageLoader;

    public BannerAdapter(List<String> mValues, Context fragment) {
        this.mValues = mValues;
        this.fragment = fragment;
        Application.getComponent().Inject(this);
    }


    @NonNull
    @Override
    public BannerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slideshow, parent, false);
        return new BannerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerAdapter.ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);
        holder.bindTo(holder.mItem);
        holder.itemView.setOnClickListener(v -> {
//                FragmentStack fragmentStack = new FragmentStack(fragment.getActivity(), fragment.getFragmentManager(), R.id.fragment_container);
//                fragmentStack.replace(VideoDetailFragment.newInstance(similarContentsItem.getId() ,similarContentsItem.getMediaType() ));
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
        }

        void bindTo(String responseItem) {
            imageLoader.displayImage(responseItem, (ImageView) mView.findViewById(R.id.imgView));

        }


    }


}
