package ir.payebash.fragments;


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
import android.util.Log;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ir.payebash.Application;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.Interfaces.IWebservice.OnLoadMoreListener;
import ir.payebash.Interfaces.IWebservice.TitleMain;
import ir.payebash.R;
import ir.payebash.activities.PostDetailsActivity;
import ir.payebash.adapters.PayeAdapter;
import ir.payebash.adapters.StoryAdapter;
import ir.payebash.remote.AsynctaskGetPost;
import ir.payebash.remote.AsynctaskStoryEvents;
import ir.payebash.classes.HSH;
import ir.payebash.classes.ItemDecorationAlbumColumns;
import ir.payebash.models.event.EventModel;
import ir.payebash.models.event.story.StoryModel;
import ir.payebash.remote.repository.RemoteRepository;

import static android.app.Activity.RESULT_OK;
import static ir.payebash.classes.HSH.openFragment;


public class HomeFragment extends Fragment implements View.OnClickListener {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private ImageView imgRooms, imgContact;
    AsynctaskGetPost getPost;
    IWebservice m;
    private RecyclerView rv;
    private RecyclerView rvStory;
    private boolean isLoading;
    private Map<String, String> params = new HashMap<>();
    private int Cnt = 0;
    private SwipeRefreshLayout swipeContainer;
    private ProgressWheel pb;
    private PayeAdapter adapter;
    private StoryAdapter adapterStory;
    private Activity ac;
    private int MY_DATA_CHECK_CODE = 0;
    private EditText etSearch;
    private Dialog dialogFilter = null;
    //private CardView llSearch;
    private Button btnCategories, btnLocation;
    private LinearLayoutManager layoutManager, storyLayoutManager;
    private OnLoadMoreListener mOnLoadMoreListener;
    private int visibleThreshold = 1, lastVisibleItem, totalItemCount = 0;
    private SearchFragment fragobj = null;
    private ContactsFragment contactsFragment;
    private RemoteRepository remoteRepository ;
    private View rootView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);

            remoteRepository = new RemoteRepository();
            DeclareElements();
            ac = getActivity();
            getEvents();
            getStories();

            swipeContainer.setOnRefreshListener(() -> {
                adapter.setCurrentDate();
                Cnt = 0;
                params.clear();
                adapter.ClearFeed();
                params.put(getString(R.string.Skip), String.valueOf(Cnt));
                getEvents();
                etSearch.setHint(
                        String.format(getString(R.string.searchHint),
                                "همه رویدادها",
                                "سراسر کشور"));
            });

            setOnLoadMoreListener(() -> {
                /*feed.add(null);
                adapter.notifyItemInserted(feed.size() - 1);*/
                swipeContainer.setRefreshing(true);
                if (HSH.isNetworkConnection(getActivity())) {
                    Cnt++;
                    params.put(getString(R.string.Skip), String.valueOf(Cnt));
                    getEvents();
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
        //llSearch.setVisibility(View.VISIBLE);
        return rootView;
    }


    private void getEvents() {

        params.put(getString(R.string.Skip), String.valueOf(Cnt));
        params.put("", Application.preferences.getString("stateCode", ""));

        remoteRepository.getAllEvents(params)
                .subscribeWith(new DisposableObserver<List<EventModel>>() {
                    @Override
                    public void onNext(List<EventModel> value) {
                        try {
                            isLoading = false;
                            swipeContainer.setRefreshing(false);
                            pb.setVisibility(View.GONE);
                            adapter.addItems(value);

                            if (layoutManager.getItemCount() > 19)
                                layoutManager.scrollToPosition(layoutManager.findLastVisibleItemPosition() + 1);
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("onError",throwable.toString());
                        HSH.showtoast(ac, "خطا در اتصال به اینترنت");
                        swipeContainer.setRefreshing(false);
                        pb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onComplete() {
                        Log.e("onComplete","onComplete");
                    }
                });

    }

    private void getStories() {

        IWebservice.IStoriesEvents m = new IWebservice.IStoriesEvents() {
            @Override
            public void getResult(List<StoryModel> stories) throws Exception {
                adapterStory.addItems(stories);
            }

            @Override
            public void getError(String error) throws Exception {

            }
        };
        new AsynctaskStoryEvents(m).getData();
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
                        cr.close();
                        if (cr.moveToFirst()) {
                            btnCategories.setText(cr.getString(cr.getColumnIndex("name")));
                            btnCategories.setTag(data.getStringExtra(getString(R.string.CategoryId)));

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
        imgRooms = rootView.findViewById(R.id.img_rooms);
        imgContact = rootView.findViewById(R.id.img_contact);
        imgRooms.setOnClickListener(this);
        imgContact.setOnClickListener(this);
        swipeContainer = rootView.findViewById(R.id.swipeContainer);
        pb = rootView.findViewById(R.id.pb);
        rv = rootView.findViewById(R.id.rv_paye);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(new ItemDecorationAlbumColumns(getActivity(), ItemDecorationAlbumColumns.VERTICAL_LIST));
        adapter = new PayeAdapter(getActivity(), eventModel -> {
            Intent intent;
            intent = new Intent(getActivity(), PostDetailsActivity.class);
            intent.putExtra("feedItem", eventModel);
            getActivity().startActivity(intent);
        });
        rv.setAdapter(adapter);

        rvStory = rootView.findViewById(R.id.rv_story);
        rvStory.setHasFixedSize(true);
        storyLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
        rvStory.setLayoutManager(storyLayoutManager);
        adapterStory = new StoryAdapter(getActivity());
        rvStory.setAdapter(adapterStory);

        ImageView mic = rootView.findViewById(R.id.mic);
        //ImageView btn_search = rootView.findViewById(R.id.btn_search);
        etSearch = rootView.findViewById(R.id.et_search);
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Search(etSearch.getText().toString().trim());
                return true;
            }
            return false;
        });
        //btn_search.setOnClickListener(view -> Filter(getActivity()));
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
            HSH.openFragment(getActivity(), new FilterFragment());
            if (dialogFilter == null) {
                dialogFilter = new Dialog(cn);
                dialogFilter.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogFilter.setContentView(R.layout.dialog_filter_posts);
                btnCategories = dialogFilter.findViewById(R.id.btn_categories);
                btnLocation = dialogFilter.findViewById(R.id.btn_location);

                Button btn_cancel = dialogFilter.findViewById(R.id.btn_cancel);
                Button btn_submit = dialogFilter.findViewById(R.id.btn_submit);
                btnCategories.setOnClickListener(v -> HSH.selectSubject(getActivity(), btnCategories));

                //btnLocation.setOnClickListener(v -> HSH.selectLocation(getActivity(), 0, btnLocation));
                btn_cancel.setOnClickListener(v -> {
                    etSearch.setHint(String.format(getString(R.string.searchHint), "همه رویدادها", "سراسر کشور"));
                    params.remove(getString(R.string.SubjectCode));
                    params.remove(getString(R.string.CityCode));
                    adapter.ClearFeed();
                    swipeContainer.setRefreshing(true);
                    //TransToSearchFrag();
                    getEvents();
                    dialogFilter.dismiss();
                });
                btn_submit.setOnClickListener(v -> {
                    try {
                        Cnt = 0;
                        params.clear();
                        //adapter.ClearFeed();
                        //pb.setVisibility(View.VISIBLE);
                        dialogFilter.dismiss();
                        params.put(getString(R.string.SubjectCode), btnCategories.getTag().toString());
                        params.put(getString(R.string.CityCode), btnLocation.getTag().toString());
                        params.put(getString(R.string.Skip), String.valueOf(Cnt));
                        etSearch.setHint(
                                String.format(getString(R.string.searchHint),
                                        btnCategories.getText().toString().trim(),
                                        btnLocation.getText().toString().trim()));
                        TransToSearchFrag();
                        /*getPost = new AsynctaskGetPost(getActivity(),
                                params,
                                m);
                        getPost.getData();*/

                    } catch (Exception e) {
                    }
                });
            }
            final RelativeLayout ll_dialog = dialogFilter.findViewById(R.id.ll_dialog);
            HSH.dialog(dialogFilter);
            dialogFilter.show();
            Handler handler = new Handler();
            handler.postDelayed(() -> HSH.display(getActivity(), ll_dialog), 50);
        } catch (Exception e) {
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img_contact) {
            if (contactsFragment == null)
                contactsFragment = new /*NewAddressActivity*/ContactsFragment();
            openFragment(getActivity(), contactsFragment);
        }
    }
}
