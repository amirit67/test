package ir.payebash.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.Interfaces.IWebservice.OnLoadMoreListener;
import ir.payebash.Interfaces.IWebservice.TitleMain;
import ir.payebash.R;
import ir.payebash.adapters.PayeAdapter;
import ir.payebash.remote.AsynctaskUncomingGetPost;
import ir.payebash.classes.HSH;
import ir.payebash.classes.NetworkUtils;
import ir.payebash.models.event.EventModel;


public class UncomingEventsFragment extends Fragment {

    @Inject
    ImageLoader imageLoader;
    private int Cnt = 0;
    private Activity ac;
    private Map<String, String> params = new HashMap<>();
    private ProgressWheel pb;
    private RecyclerView rv;
    private boolean isLoading;
    private SwipeRefreshLayout swipeContainer;
    private PayeAdapter adapter;
    private View rootView = null;
    private LinearLayoutManager layoutManager;
    private int totalItemCount = 0;
    private OnLoadMoreListener mOnLoadMoreListener;
    private int visibleThreshold = 1;
    private int lastVisibleItem;
    private IWebservice m;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && null != adapter) {
            adapter.notifyDataSetChanged();
        } else {
            // nothing to do
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
            ac = getActivity();
            Application.getComponent().Inject(this);
            DeclareElements();
            params.put(getString(R.string.UserId), Application.preferences.getString(getString(R.string.UserId), "0"));
            params.put(getString(R.string.Skip), String.valueOf(Cnt));
            m = new IWebservice() {
                @Override
                public void getResult(List<EventModel> list) throws Exception {
                    try {
                        isLoading = false;
                        swipeContainer.setRefreshing(false);
                        pb.setVisibility(View.GONE);
                        adapter.addItems(list);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void getError(String s) throws Exception {
                    HSH.showtoast(ac, "خطا در اتصال به اینترنت");
                    swipeContainer.setRefreshing(false);
                    pb.setVisibility(View.GONE);
                }
            };
            final AsynctaskUncomingGetPost getPost = new AsynctaskUncomingGetPost(getActivity(),
                    params,
                    m);
            getPost.getData();

            swipeContainer.setOnRefreshListener(() -> {
                if (NetworkUtils.getConnectivity(getActivity()) != false) {
                    Cnt = 0;
                    params.clear();
                    adapter.ClearFeed();
                    params.put(getString(R.string.UserId), Application.preferences.getString(getString(R.string.UserId), "0"));
                    params.put(getString(R.string.Skip), String.valueOf(Cnt));
                    getPost.getData();
                } else {
                    swipeContainer.setRefreshing(false);
                    HSH.showtoast(getActivity(), "خطا در اتصال به اینترنت");
                }
            });
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            setOnLoadMoreListener(() -> {
                /*feed.add(null);
                adapter.notifyItemInserted(feed.size() - 1);*/
                swipeContainer.setRefreshing(true);
                if (HSH.isNetworkConnection(getActivity())) {
                    Cnt++;
                    params.put(getString(R.string.Skip), String.valueOf(Cnt));
                    getPost.getData();
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
        ((TitleMain) getContext()).FragName("رویدادها");
        return rootView;
    }

    public void DeclareElements() {
        swipeContainer = rootView.findViewById(R.id.swipeContainer);
        pb = rootView.findViewById(R.id.pb);
        rv = rootView.findViewById(R.id.rv_paye);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        adapter = new PayeAdapter(getActivity(), eventModel -> {

        });
        rv.setAdapter(adapter);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }
}
