package ir.payebash.Fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.payebash.Adapters.PayeAdapter;
import ir.payebash.Adapters.RoomsAdapter;
import ir.payebash.Adapters.StoryAdapter;
import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.Classes.ItemDecorationAlbumColumns;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.Interfaces.IWebservice.OnLoadMoreListener;
import ir.payebash.Interfaces.IWebservice.TitleMain;
import ir.payebash.Models.PayeItem;
import ir.payebash.R;
import ir.payebash.asynktask.AsynctaskGetPost;

import static android.app.Activity.RESULT_OK;


public class RoomsFragment extends Fragment {


    private final int REQ_CODE_SPEECH_INPUT = 100;

    AsynctaskGetPost getPost;
    IWebservice m;
    private RecyclerView rv;
    private boolean isLoading;
    private Map<String, String> params = new HashMap<>();
    private int Cnt = 0;
    private SwipeRefreshLayout swipeContainer;
    private ProgressWheel pb;
    private RoomsAdapter adapter;
    private Activity ac;
    private int MY_DATA_CHECK_CODE = 0;
    private EditText etSearch;
    private LinearLayoutManager layoutManager;
    private OnLoadMoreListener mOnLoadMoreListener;
    private int visibleThreshold = 1, lastVisibleItem, totalItemCount = 0;

    private View rootView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_rooms, container, false);

            DeclareElements();
            ac = getActivity();
            getEvents();

            swipeContainer.setOnRefreshListener(() -> {
                Cnt = 0;
                params.clear();
                adapter.ClearFeed();
                params.put(getString(R.string.Skip), String.valueOf(Cnt));
                getPost.getData();
            });

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
        return rootView;
    }


    private void getEvents() {
        params.put(getString(R.string.Skip), String.valueOf(Cnt));
        m = new IWebservice() {
            @Override
            public void getResult(retrofit2.Response<List<PayeItem>> list) throws Exception {
                try {
                    isLoading = false;
                    swipeContainer.setRefreshing(false);
                    pb.setVisibility(View.GONE);
                    adapter.addItems(list.body());
                } catch (Exception e) {
                }
            }

            @Override
            public void getError() throws Exception {
                HSH.showtoast(ac, "خطا در اتصال به اینترنت");
                swipeContainer.setRefreshing(false);
                pb.setVisibility(View.GONE);
            }
        };
        getPost = new AsynctaskGetPost(getActivity(), params, m);
        getPost.getData();

    }


    @Override
    public void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_DATA_CHECK_CODE) {
            {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //et_search.setText(result.get(0));
                    Search(result.get(0));
                }
                break;
            }
            case 456:
                if (null != data) {
                    try {
                        final Cursor cr = Application.database.rawQuery("SELECT name from categories where id = '" + data.getStringExtra(getString(R.string.CategoryId)) + "'", null);
                        if (cr.moveToFirst()) {
                            //btnCategories.setText(cr.getString(cr.getColumnIndex("name")));
                            //btnCategories.setTag(data.getStringExtra(getString(R.string.CategoryId)));

                            params.put("Activity", data.getStringExtra(getString(R.string.CategoryId)));
                        }
                    } catch (Exception e) {
                    }
                }
                break;
        }
    }

    public void DeclareElements() {
        //llSearch = rootView.findViewById(R.id.ll_search);
        swipeContainer = rootView.findViewById(R.id.swipeContainer);
        pb = rootView.findViewById(R.id.pb);
        rv = rootView.findViewById(R.id.rv_paye);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(new ItemDecorationAlbumColumns(getActivity(), ItemDecorationAlbumColumns.VERTICAL_LIST));
        adapter = new RoomsAdapter(getActivity());
        rv.setAdapter(adapter);

        etSearch = rootView.findViewById(R.id.et_search);
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Search(etSearch.getText().toString().trim());
                return true;
            }
            return false;
        });
    }

    public void Search(final String text) {
        try {
            Cnt = 0;
            params.clear();
            //adapter.ClearFeed();
            //pb.setVisibility(View.VISIBLE);
            params.put("ContentSearch", text);
            params.put(getString(R.string.Skip), String.valueOf(Cnt));
            /*getPost = new AsynctaskGetPost(getActivity(),
                    params,
                    m);
            getPost.getData();*/
            InputMethodManager inputManager =
                    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(
                    getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

        } catch (Exception e) {
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

}
