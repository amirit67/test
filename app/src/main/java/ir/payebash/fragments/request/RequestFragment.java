package ir.payebash.fragments.request;


import android.app.Activity;
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

import ir.payebash.Interfaces.IWebservice;
import ir.payebash.Interfaces.IWebservice.TitleMain;
import ir.payebash.R;
import ir.payebash.adapters.RequestAdapter;
import ir.payebash.remote.AsynctaskRequestToJoin;
import ir.payebash.classes.HSH;
import ir.payebash.classes.ItemDecorationAlbumColumns;
import ir.payebash.models.RequestItem;


public class RequestFragment extends Fragment implements View.OnClickListener {

    AsynctaskRequestToJoin asynctaskRequestToJoin;
    IWebservice.IRequestJoin m;
    private RecyclerView rv;
    private SwipeRefreshLayout swipeContainer;
    private ProgressWheel pb;
    private RequestAdapter adapter;
    private Activity ac;
    private LinearLayoutManager layoutManager;
    private View rootView = null;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_request, container, false);

            DeclareElements();
            ac = getActivity();
            getRequest();
            swipeContainer.setOnRefreshListener(() -> {
                adapter.ClearFeed();
                asynctaskRequestToJoin.getData();
            });
        }
        ((TitleMain) getContext()).FragName("خانه");
        return rootView;
    }

    private void getRequest() {
        m = new IWebservice.IRequestJoin() {
            @Override
            public void getResult(List<RequestItem> list) throws Exception {
                try {
                    pb.setVisibility(View.GONE);
                    adapter.addItems(list);
                    swipeContainer.setRefreshing(false);
                } catch (Exception e) {
                }
            }

            @Override
            public void getError(String error) throws Exception {
                HSH.showtoast(ac, "خطا در اتصال به اینترنت");
                pb.setVisibility(View.GONE);
            }
        };
        asynctaskRequestToJoin = new AsynctaskRequestToJoin(getActivity(), m);
        asynctaskRequestToJoin.getData();

    }

    public void DeclareElements() {
        swipeContainer = rootView.findViewById(R.id.swipeContainer);
        pb = rootView.findViewById(R.id.pb);
        rv = rootView.findViewById(R.id.rv_request);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(new ItemDecorationAlbumColumns(getActivity(), ItemDecorationAlbumColumns.VERTICAL_LIST));
        adapter = new RequestAdapter(getActivity(), room -> {

        });
        rv.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

    }
}
