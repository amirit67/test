package ir.payebash.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ir.payebash.models.FilterFeedItem;
import ir.payebash.R;


public class FilterAdapter extends BaseAdapter {

    List<FilterFeedItem> feed;
    private LayoutInflater layoutInflater;

    public FilterAdapter(Context fragmentActivity, List<FilterFeedItem> feed) {

        try {
            layoutInflater = (LayoutInflater) fragmentActivity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.feed = feed;
        } catch (Exception e) {
        }
    }

    @Override
    public int getCount() {

        try {
            return feed.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        try {
            return position;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public long getItemId(int position) {
        try {
            return position;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            View listItem = convertView;
            final int pos = position;
            if (listItem == null) {
                listItem = layoutInflater.inflate(R.layout.item_filter, null);
            }
            final TextView tvTitle = listItem.findViewById(R.id.title);
            tvTitle.setText(feed.get(pos).getName());

            if (feed.get(pos).getHasChild().equals("true"))
                tvTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chevron_left, 0, 0, 0);
            else
                tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            return listItem;
        } catch (Exception e) {
            return null;
        }
    }

}