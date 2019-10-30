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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import androidx.cardview.widget.CardView;
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

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment {


    private final int REQ_CODE_SPEECH_INPUT = 100;
    @Inject
    ImageLoader imageLoader;
    AsynctaskGetPost getPost;
    IWebservice m;
    private RecyclerView rv;
    private boolean isLoading;
    private Map<String, String> params = new HashMap<>();
    private int Cnt = 0;
    private SwipeRefreshLayout swipeContainer;
    private ProgressWheel pb;
    private PayeAdapter adapter;
    private Activity ac;
    private int MY_DATA_CHECK_CODE = 0;
    private EditText et_search;
    private Dialog dialog_filter = null;
    private CardView ll_search;
    private Button btn_categories, btn_location;
    private LinearLayoutManager layoutManager;
    private OnLoadMoreListener mOnLoadMoreListener;
    private int visibleThreshold = 1, lastVisibleItem, totalItemCount = 0;
    private SearchFragment fragobj = null;
    private View rootView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);

            DaggerMainComponent.builder()
                    .imageLoaderMoudle(new ImageLoaderMoudle(getActivity()))
                    .build()
                    .Inject(this);
            DeclareElements();
            ac = getActivity();
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

            swipeContainer.setOnRefreshListener(() -> {
                Cnt = 0;
                params.clear();
                Application.myAds = 1;
                adapter.ClearFeed();
                params.put(getString(R.string.Skip), String.valueOf(Cnt));
                getPost.getData();
                et_search.setHint(
                        String.format(getString(R.string.searchHint),
                                "همه رویدادها",
                                "سراسر کشور"));
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
        ((TitleMain) getContext()).FragName("خانه");
        Application.myAds = 1;
        ll_search.setVisibility(View.VISIBLE);
        return rootView;
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
                            btn_categories.setText(cr.getString(cr.getColumnIndex("name")));
                            btn_categories.setTag(data.getStringExtra(getString(R.string.CategoryId)));

                            params.put("Activity", data.getStringExtra(getString(R.string.CategoryId)));
                        }
                    } catch (Exception e) {
                    }
                }
                break;
        }
    }

    public void DeclareElements() {
        ll_search = rootView.findViewById(R.id.ll_search);
        swipeContainer = rootView.findViewById(R.id.swipeContainer);
        pb = rootView.findViewById(R.id.pb);
        rv = rootView.findViewById(R.id.rv_paye);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        adapter = new PayeAdapter(getActivity(), pb, Cnt, params, imageLoader);
        rv.setAdapter(adapter);

        ImageView mic = rootView.findViewById(R.id.mic);
        ImageButton btn_search = rootView.findViewById(R.id.btn_search);
        et_search = rootView.findViewById(R.id.et_search);
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Search(et_search.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
        btn_search.setOnClickListener(view -> Filter(getActivity()));
        mic.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa");
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    "دنبال چه هستید؟");
            try {
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            } catch (ActivityNotFoundException a) {
                Toast.makeText(getActivity(),
                        "خطا",
                        Toast.LENGTH_SHORT).show();
            }
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
            TransToSearchFrag();
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

    private void TransToSearchFrag() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("HashMap", (Serializable) params);
        //set Fragmentclass Arguments
        fragobj = new SearchFragment();
        fragobj.setArguments(bundle);
        HSH.openFragment(getActivity(), fragobj);
    }

    public void Filter(final Context cn) {
        try {
            if (dialog_filter == null) {
                dialog_filter = new Dialog(cn);
                dialog_filter.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_filter.setContentView(R.layout.dialog_filter_posts);
                btn_categories = dialog_filter.findViewById(R.id.btn_categories);
                btn_location = dialog_filter.findViewById(R.id.btn_location);

                Button btn_cancel = dialog_filter.findViewById(R.id.btn_cancel);
                Button btn_submit = dialog_filter.findViewById(R.id.btn_submit);
                btn_categories.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HSH.selectSubject(getActivity(), btn_categories);
                    }
                });

                btn_location.setOnClickListener(v -> HSH.selectLocation(getActivity(), 0, btn_location));
                btn_cancel.setOnClickListener(v -> {
                    et_search.setHint(String.format(getString(R.string.searchHint), "همه رویدادها", "سراسر کشور"));
                    params.remove(getString(R.string.SubjectCode));
                    params.remove(getString(R.string.CityCode));
                    adapter.ClearFeed();
                    swipeContainer.setRefreshing(true);
                    //TransToSearchFrag();
                    getPost.getData();
                    dialog_filter.dismiss();
                });
                btn_submit.setOnClickListener(v -> {
                    try {
                        Cnt = 0;
                        params.clear();
                        //adapter.ClearFeed();
                        //pb.setVisibility(View.VISIBLE);
                        dialog_filter.dismiss();
                        params.put(getString(R.string.SubjectCode), btn_categories.getTag().toString());
                        params.put(getString(R.string.CityCode), btn_location.getTag().toString());
                        params.put(getString(R.string.Skip), String.valueOf(Cnt));
                        et_search.setHint(
                                String.format(getString(R.string.searchHint),
                                        btn_categories.getText().toString().trim(),
                                        btn_location.getText().toString().trim()));
                        TransToSearchFrag();
                        /*getPost = new AsynctaskGetPost(getActivity(),
                                params,
                                m);
                        getPost.getData();*/

                    } catch (Exception e) {
                    }
                });
            }
            final RelativeLayout ll_dialog = dialog_filter.findViewById(R.id.ll_dialog);
            HSH.dialog(dialog_filter);
            dialog_filter.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    HSH.display(getActivity(), ll_dialog);
                }
            }, 50);
        } catch (Exception e) {
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

}
