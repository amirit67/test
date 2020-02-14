package ir.payebash.Adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.payebash.Application;
import ir.payebash.Interfaces.IWebservice.setListenerCity;
import ir.payebash.models.CityItem;
import ir.payebash.R;


public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.CityHolder> {

    private List<CityItem> feedItemList;
    private setListenerCity onItemCheckListener;
    private Context context;
    private Cursor cr;

    public CitiesAdapter(Context context, List<CityItem> feed, @NonNull setListenerCity onItemCheckListener) {

        try {
            this.feedItemList = feed;
            this.onItemCheckListener = onItemCheckListener;
            this.context = context;
        } catch (Exception e) {
        }
    }


    @Override
    public CityHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_city, null);
        return new CityHolder(v);
    }

    @Override
    public void onBindViewHolder(CityHolder holder, final int i) {
            /*if (i == 0)
                details.clear();*/
        holder.setIsRecyclable(false);
        holder.imgChevronLeft.setVisibility(feedItemList.get(i).getHasChild() == 1 ? View.VISIBLE : View.GONE);
        holder.tvTitle.setText(feedItemList.get(i).getCityNameFa());
        holder.itemView.setOnClickListener(vv -> onItemCheckListener.onItemCheck(feedItemList.get(i)));
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

   /* @Override
    public long getItemId(int position) {
        return feedItemList.get(position).getId().hashCode();
    }*/


    public class CityHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public ImageView imgChevronLeft;

        public CityHolder(View view) {
            super(view);

            tvTitle = view.findViewById(R.id.lbl_city);
            imgChevronLeft = view.findViewById(R.id.img_chevron_left);
        }

    }


    public void filter(final String text) {

        new Thread(() -> {
            try {
                feedItemList.clear();
                cr = Application.database.rawQuery("SELECT * from cities where Name like '%" + text + "%' order by Name", null);
                while (cr.moveToNext()) {
                    CityItem item = new CityItem();
                    item.setId(cr.getString(cr.getColumnIndex("Id")));
                    item.setCityNameFa(cr.getString(cr.getColumnIndex("Name")));
                    item.setHasChild(cr.getInt(cr.getColumnIndex("HasChild")));
                    item.setParentCode(cr.getInt(cr.getColumnIndex("ParentCode")));
                    feedItemList.add(item);
                }
                cr.close();
                // Set on UI Thread
                ((Activity) context).runOnUiThread(() -> notifyDataSetChanged());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

}