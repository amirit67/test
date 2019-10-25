package ir.payebash.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ir.payebash.Adapters.PayeAdapter;
import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.DI.DaggerMainComponent;
import ir.payebash.DI.ImageLoaderMoudle;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.Interfaces.IWebservice.OnLoadMoreListener;
import ir.payebash.Interfaces.IWebservice.TitleMain;
import ir.payebash.Models.PayeItem;
import ir.payebash.R;
import ir.payebash.asynktask.AsynctaskGetPost;


public class MyPayeFragment extends Fragment {

    @Inject
    ImageLoader imageLoader;
    private RecyclerView rv;
    private boolean isLoading;
    private Map<String, String> params = new HashMap<>();
    /* private TextView txt_myads, txt_wanted;*/
    private SwipeRefreshLayout swipeContainer;
    private ProgressWheel pb;
    private PayeAdapter adapter;
    private LinearLayoutManager layoutManager;
    private OnLoadMoreListener mOnLoadMoreListener;
    private int visibleThreshold = 1, lastVisibleItem, totalItemCount = 0, Cnt = 0;
    private Bundle bundle = null;
    private IWebservice m;
    private AsynctaskGetPost getPost;
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

        if (cr.getCount() == 0) {
            HSH.showtoast(ac, "موردی یافت نشد.");
            ac.getFragmentManager().popBackStack();
        } else {
            adapter.ClearFeed();
            for (int i = 0; i < cr.getCount(); i++) {
                try {
                    cr.moveToPosition(i);
                    JSONArray result = new JSONArray(cr.getString(cr.getColumnIndex("data")));
                    try {
                        Gson gson = new Gson();
                        if (result != null) {
                            PayeItem[] payeItems = gson.fromJson(result.toString(), PayeItem[].class);
                            if (payeItems != null && payeItems.length > 0) {
                                for (PayeItem item : payeItems) {
                                    adapter.addItem(item);
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (null == bundle)
                Application.myAds = 42907631;
            else
                Application.myAds = 1;
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
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        DaggerMainComponent.builder()
                .imageLoaderMoudle(new ImageLoaderMoudle(getActivity()))
                .build()
                .Inject(this);
        DeclareElements();
        bundle = getArguments();
        if (null != bundle) {
            swipeContainer.setEnabled(false);
            FavoriteOrRecent(getActivity(), bundle.getString("FavoriteOrRecent"));
        } else {
            ((TitleMain) getContext()).FragName("رویدادها");
            params.put(getString(R.string.UserId), Application.preferences.getString(getString(R.string.UserId), "0"));
            params.put(getString(R.string.Skip), String.valueOf(Cnt));
            pb.setVisibility(View.VISIBLE);
            m = new IWebservice() {
                @Override
                public void getResult(retrofit2.Response<List<PayeItem>> list) throws Exception {
                    try {
                        isLoading = false;
                        swipeContainer.setRefreshing(false);
                        pb.setVisibility(View.GONE);
                        /*try {
                            feed.remove(feed.size() - 1);
                            adapter.notifyItemRemoved(feed.size());
                        } catch (Exception e) {
                        }*/
                        String s;
                        for (PayeItem m : list.body()) {
                            try {
                                s = getActivity().getResources().getStringArray(R.array.Citys)[m.getCity() - 2];
                                m.setCityDate(m.getCreateDate() + " در " + s.substring(s.indexOf("-") + 2));
                                adapter.addItem(m);
                            } catch (Exception e) {
                                if (m.getCity() == 1) {
                                    m.setCityDate("سراسر کشور");
                                    adapter.addItem(m);
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void getError() throws Exception {
                    HSH.showtoast(getActivity(), "خطا در اتصال به اینترنت");
                    swipeContainer.setRefreshing(false);
                    pb.setVisibility(View.GONE);
                }
            };
            getPost = new AsynctaskGetPost(getActivity(),
                    params,
                    m);
            getPost.getData();
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Cnt = 0;
                    params.clear();
                    Application.myAds = 42907631;
                    adapter.ClearFeed();
                    params.put(getString(R.string.Skip), String.valueOf(Cnt));
                    params.put(getString(R.string.UserId), Application.preferences.getString(getString(R.string.UserId), "0"));
                    getPost.getData();
                }
            });
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    /*feed.add(null);
                    adapter.notifyItemInserted(feed.size() - 1);*/
                    swipeContainer.setRefreshing(true);
                    if (HSH.isNetworkConnection(getActivity())) {
                        Cnt++;
                        params.put(getString(R.string.Skip), String.valueOf(Cnt));
                        getPost.getData();
                    }
                }
            });

            rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    if (dy > 0) //check for scroll down
                    {
                        totalItemCount = layoutManager.getItemCount();
                        lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                        if (!isLoading && adapter.getItemCount() > 19 && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            if (mOnLoadMoreListener != null) {
                                mOnLoadMoreListener.onLoadMore();
                            }
                            isLoading = true;
                        }
                    }
                }
            });
        }
        return rootView;
    }

    public void DeclareElements() {
        swipeContainer = rootView.findViewById(R.id.swipeContainer);
        pb = rootView.findViewById(R.id.pb);
        rv = rootView.findViewById(R.id.rv_paye);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        adapter = new PayeAdapter(getActivity(), pb, Cnt, params, imageLoader);
        rv.setAdapter(adapter);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321 && null != data) {
            getPost = new AsynctaskGetPost(getActivity(),
                    params,
                    m);
        }

    }
}
