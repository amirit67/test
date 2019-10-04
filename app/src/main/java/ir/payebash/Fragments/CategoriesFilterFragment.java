package ir.payebash.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ir.payebash.Adapters.FilterAdapter;
import ir.payebash.Application;
import ir.payebash.Models.FilterFeedItem;
import ir.payebash.R;


public class CategoriesFilterFragment extends Fragment {

    private EditText editTextSearch;
    private View rootView = null;
    private FilterAdapter adapter;
    private List<FilterFeedItem> feed = new ArrayList<>();
    private ListView lv;
    private Cursor cr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_filter, container, false);

        feed.clear();
        editTextSearch = rootView.findViewById(R.id.editTextSearch);
        lv = rootView.findViewById(R.id.listView);
        adapter = new FilterAdapter(getActivity(), feed);
        lv.setAdapter(adapter);

        try {
            cr = Application.database.rawQuery("SELECT * from categories where state = 'true' and parentId = '" + getArguments().getString("filter_item_pos") + "'  order by sort", null);
        } catch (Exception e) {
            cr = Application.database.rawQuery("SELECT * from categories where state = 'true' and parentId = 'null' order by sort", null);
        }

        while (cr.moveToNext()) {
            FilterFeedItem item = new FilterFeedItem();
            item.setId(cr.getString(cr.getColumnIndex("id")));
            item.setName(cr.getString(cr.getColumnIndex("name")));
            item.setHasChild(cr.getString(cr.getColumnIndex("hasChild")));
            feed.add(item);
        }
        adapter.notifyDataSetChanged();
        cr.close();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (feed.get(position).getHasChild().equals("true")) {
                    Cursor cr = Application.database.rawQuery("SELECT id from categories where parentId = '" + feed.get(position).getId() + "' ", null);
                    if (cr.getCount() > 0) {

                        CategoriesFilterFragment fragment = new CategoriesFilterFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("filter_item_pos", feed.get(position).getId());
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction ftx = fragmentManager.beginTransaction();
                        ftx.setCustomAnimations(R.anim.slide_in_right,
                                R.anim.slide_out_left, R.anim.slide_in_left,
                                R.anim.slide_out_right);
                        ftx.addToBackStack(fragment.getClass().getSimpleName());
                        ftx.replace(R.id.layoutFragment, fragment);
                        ftx.commit();

                    }
                } else {
                    Intent resultData = new Intent();
                    resultData.putExtra(getString(R.string.CategoryId), feed.get(position).getId());
                    getActivity().setResult(Activity.RESULT_OK, resultData);
                    getActivity().finish();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    feed.clear();
                    Cursor cr = Application.database.rawQuery("SELECT id,name from categories where name LIKE '%" + ((EditText) rootView.findViewById(R.id.editTextSearch)).getText().toString().trim() + "%'  order by name", null);
                    while (cr.moveToNext()) {
                        FilterFeedItem item = new FilterFeedItem();
                        item.setId(cr.getString(cr.getColumnIndex("id")));
                        item.setName(cr.getString(cr.getColumnIndex("name")));
                        feed.add(item);
                    }

                    adapter.notifyDataSetChanged();
                    lv.setAdapter(adapter);
                } catch (Exception e) {
                }

            }
        });

    }
}
