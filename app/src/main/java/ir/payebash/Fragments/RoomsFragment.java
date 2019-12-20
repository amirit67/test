package ir.payebash.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
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
import android.widget.TextView;
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
import ir.payebash.chat.ChatActivity;
import ir.payebash.chat.ChatService;
import ir.payebash.chat.MainActivity;
import ir.payebash.modelviewsChat.RoomViewModel;

import static android.app.Activity.RESULT_OK;


public class RoomsFragment extends Fragment {


    // Used to receive messages from ChatService
    MyReceiver myReceiver;
    // Chat Service
    ChatService chatService;
    boolean mBound = false;
    private RecyclerView rv;
    private Map<String, String> params = new HashMap<>();
    private int Cnt = 0;
    private SwipeRefreshLayout swipeContainer;
    private ProgressWheel pb;
    private RoomsAdapter adapter;
    private EditText etSearch;
    private LinearLayoutManager layoutManager;

    private View rootView = null;

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), ChatService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        //Register events we want to receive from Chat Service
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("notifyAdapter");
        getActivity().registerReceiver(myReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_rooms, container, false);

            DeclareElements();
        }
        return rootView;
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
        adapter = new RoomsAdapter(getActivity(), new RoomsAdapter.IGetRoom() {
            @Override
            public void Room(RoomViewModel room) {
                //final String text = ((TextView) view).getText().toString();
                chatService.Join(room.name);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("room", room);
                startActivity(intent);
            }
        });
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

    //https://developer.android.com/guide/components/bound-services.html
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            ChatService.LocalBinder binder = (ChatService.LocalBinder) service;
            chatService = binder.getService();
            chatService.GetRooms();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case "notifyAdapter":
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    } // MyReceiver
}
