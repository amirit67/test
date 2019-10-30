package ir.payebash.Fragments;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.payebash.Adapters.PayeAdapter;
import ir.payebash.Application;
import ir.payebash.DI.DaggerMainComponent;
import ir.payebash.DI.ImageLoaderMoudle;
import ir.payebash.Interfaces.IWebservice.TitleMain;
import ir.payebash.Models.PayeItem;
import ir.payebash.R;


public class EventsWantedFragment extends Fragment {


    @Inject
    ImageLoader imageLoader;
    private RecyclerView rv;
    private Map<String, String> params = new HashMap<>();
    private ProgressWheel pb;
    private PayeAdapter adapter;
    private LinearLayoutManager layoutManager;
    private View rootView = null;

    public void FavoriteOrRecent(Activity ac) {
        pb.setVisibility(View.GONE);
        Cursor cr = null;
        ((TitleMain) getContext()).FragName("رویدادها");
        cr = Application.database.rawQuery("SELECT * from RecentVisit where IsWanted = 'true' ORDER BY Id DESC limit 100", null);

        if (cr.getCount() == 0) {
           /* HSH.showtoast(ac, "موردی یافت نشد.");
            ac.getFragmentManager().popBackStack();*/
        } else {
            adapter.ClearFeed();
            String postIds = "";
            for (int i = 0; i < cr.getCount(); i++) {
                try {
                    cr.moveToPosition(i);
                    postIds += cr.getString(cr.getColumnIndex("Id")) + ",";

                    //JSONArray result = new JSONArray(cr.getString(cr.getColumnIndex("data")));
                    try {
                        /*Gson gson = new Gson();
                        if (result != null) {
                            PayeItem[] payeItems = gson.fromJson(result.toString(), PayeItem[].class);
                            adapter.addItems(Arrays.asList(payeItems));
                        }*/
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
        if (isVisibleToUser && null != adapter) {
            Application.myAds = 1;
            FavoriteOrRecent(getActivity());
        } else {
            // nothing to do
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Application.myAds = 1;
        FavoriteOrRecent(getActivity());
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

        return rootView;
    }

    public void DeclareElements() {
        rootView.findViewById(R.id.swipeContainer).setEnabled(false);
        pb = rootView.findViewById(R.id.pb);
        rv = rootView.findViewById(R.id.rv_paye);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        adapter = new PayeAdapter(getActivity(), pb, 0, params, imageLoader);
        rv.setAdapter(adapter);
    }
}
