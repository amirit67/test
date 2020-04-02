package ir.payebash.fragments;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.List;

import ir.payebash.Application;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.Interfaces.IWebservice.TitleMain;
import ir.payebash.R;
import ir.payebash.activities.PostDetailsActivity;
import ir.payebash.adapters.PayeAdapter;
import ir.payebash.remote.user.AsynctaskGetMyEvents;
import ir.payebash.classes.HSH;
import ir.payebash.classes.ItemDecorationAlbumColumns;
import ir.payebash.models.event.EventModel;


public class MyPayeFragment extends Fragment {

    private RecyclerView rv;
    /* private TextView txt_myads, txt_wanted;*/
    private SwipeRefreshLayout swipeContainer;
    private ProgressWheel pb;
    private PayeAdapter adapter;
    private LinearLayoutManager layoutManager;
    private Bundle bundle = null;
    private IWebservice m;
    private AsynctaskGetMyEvents getPost;
    private View rootView = null;

    public void FavoriteOrRecent(Activity ac, String state) {
        pb.setVisibility(View.GONE);
        Cursor cr = null;
        if (state.equals("Favorite")) {
            ((TitleMain) getContext()).FragName("نشان شده ها");
            cr = Application.database.rawQuery("SELECT * from RecentVisit where IsFavorite = 'true' ORDER BY Id DESC  limit 100", null);
        } else if (state.equals("History")) {
            ((TitleMain) getContext()).FragName("بازدیدهای اخیر");
            cr = Application.database.rawQuery("SELECT * from RecentVisit ORDER BY Id DESC limit 100", null);
        } /*else if (state.equals("Wanted")) {
            ((TitleMain) getContext()).FragName("رویدادهای درخواستی من");
            cr = Application.database.rawQuery("SELECT * from RecentVisit where IsWanted = 'true' ORDER BY Id DESC limit 100", null);
        }*/
        cr.close();
        if (cr.getCount() == 0) {
            HSH.showtoast(ac, "موردی یافت نشد.");
            getFragmentManager().popBackStack();
        } else {
            adapter.ClearFeed();
            String postIds = "";
            for (int i = 0; i < cr.getCount(); i++) {
                try {
                    cr.moveToPosition(i);
                    postIds += cr.getString(cr.getColumnIndex("Id")) + ",";

                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
           /* if (null == bundle)
                Application.myAds = 42907631;
            else
                Application.myAds = 1;*/
            try {
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
            }
        } else {
            // nothing to do
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            if (rootView != null) {
                ViewGroup parent = (ViewGroup) rootView.getParent();
                if (parent != null) {
                    parent.removeView(rootView);
                }
            }
        } catch (Exception e) {
        }

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_my_event, container, false);
            DeclareElements();
            bundle = getArguments();
            if (null != bundle) {
                swipeContainer.setEnabled(false);
                FavoriteOrRecent(getActivity(), bundle.getString("FavoriteOrRecent"));
            } else {
                ((TitleMain) getContext()).FragName("رویدادها");
                pb.setVisibility(View.VISIBLE);
                m = new IWebservice() {
                    @Override
                    public void getResult(List<EventModel> list) throws Exception {
                        try {
                            swipeContainer.setRefreshing(false);
                            pb.setVisibility(View.GONE);
                            adapter.addItems(list);
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void getError(String s) throws Exception {
                        HSH.showtoast(getActivity(), "خطا در اتصال به اینترنت");
                        swipeContainer.setRefreshing(false);
                        pb.setVisibility(View.GONE);
                    }
                };
                getPost = new AsynctaskGetMyEvents(getActivity(), m);
                getPost.getData();
                swipeContainer.setOnRefreshListener(() -> {
                    adapter.ClearFeed();
                    getPost.getData();
                });
            }
        }
        return rootView;
    }

    public void DeclareElements() {
        swipeContainer = rootView.findViewById(R.id.swipeContainer);
        pb = rootView.findViewById(R.id.pb);
        rv = rootView.findViewById(R.id.rv_paye);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rv.addItemDecoration(new ItemDecorationAlbumColumns(getActivity(), ItemDecorationAlbumColumns.VERTICAL_LIST));
        rv.setLayoutManager(layoutManager);
        adapter = new PayeAdapter(getActivity(), eventModel -> {
            Intent intent;
            intent = new Intent(getActivity(), PostDetailsActivity.class);
            intent.putExtra("feedItem", eventModel);
            getActivity().startActivity(intent);
        });
        rv.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321 && null != data) {
            getPost = new AsynctaskGetMyEvents(getActivity(), m);
        }

    }
}
