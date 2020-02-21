package ir.payebash.fragments.request;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pnikosis.materialishprogress.ProgressWheel;

import ir.payebash.R;
import ir.payebash.adapters.RoomsAdapter;
import ir.payebash.chat.ChatActivity;
import ir.payebash.chat.ChatService;
import ir.payebash.classes.ItemDecorationAlbumColumns;


public class RoomsFragment extends Fragment {


    // Used to receive messages from ChatService
    MyReceiver myReceiver;
    // Chat Service
    ChatService chatService;
    boolean mBound = false;
    private RecyclerView rv;
    private SwipeRefreshLayout swipeContainer;
    private ProgressWheel pb;
    private RoomsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private Context context;
    private View rootView = null;

    @Override
    public void onStart() {
        super.onStart();
        new LongOperation().execute();
    }

    @Override
    public void onStop() {
        try {
            context.unregisterReceiver(myReceiver);
            super.onStop();
            if (mBound) {
                getActivity().unbindService(mConnection);
                mBound = false;
            }
        } catch (Exception e) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_rooms, container, false);
            DeclareElements();

            swipeContainer.setOnRefreshListener(() -> {
                adapter.ClearFeed();
                //Register events we want to receive from Chat Service
                chatService.GetRooms();
            });
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void DeclareElements() {
        //llSearch = rootView.findViewById(R.id.ll_search);
        swipeContainer = rootView.findViewById(R.id.swipeContainer);
        pb = rootView.findViewById(R.id.pb);
        rv = rootView.findViewById(R.id.rv_paye);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(new ItemDecorationAlbumColumns(context, ItemDecorationAlbumColumns.VERTICAL_LIST));
        adapter = new RoomsAdapter(context, room -> {
            //final String text = ((TextView) view).getText().toString();
            chatService.Join(room.name);
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("room", room);
            context.startActivity(intent);
        });
        rv.setAdapter(adapter);
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
                    swipeContainer.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                    pb.setVisibility(View.GONE);
                    break;
            }
        }
    } // MyReceiver

    private final class LongOperation extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            Intent intent = new Intent(getActivity(), ChatService.class);
            context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

            myReceiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("notifyAdapter");
            context.registerReceiver(myReceiver, intentFilter);
            return "Executed";
        }
    }
}
