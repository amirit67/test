package ir.payebash.chat;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ir.payebash.Activities.BaseActivity;
import ir.payebash.Adapters.MessageAdapter;
import ir.payebash.Classes.ItemDecorationAlbumColumns;
import ir.payebash.R;
import ir.payebash.helpers.Globals;
import ir.payebash.helpers.User;
import ir.payebash.modelviewsChat.MessageViewModel;
import ir.payebash.modelviewsChat.RoomViewModel;

public class ChatActivity extends BaseActivity {

    // Used to receive messages from ChatService
    MyReceiver myReceiver;

    TextView txtFullNameHost, txtRoomName, txtTimeToJoin, tvFollwers;
    ImageView imgOwner;
    // Chat Service
    ChatService chatService;
    boolean mBound = false;

    private RecyclerView rv;
    private MessageAdapter adapter;
    private RoomViewModel roomViewModel;
    int check = 0;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ChatService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        //Register events we want to receive from Chat Service
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("newMessage");
        intentFilter.addAction("notifyAdapter");
        intentFilter.addAction("joinLobby");
        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(myReceiver);
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(User.CurrentRoom);*/
        initViews();

        roomViewModel = (RoomViewModel) getIntent().getExtras().getSerializable("room");
        txtFullNameHost.setText(roomViewModel.host);
        txtRoomName.setText(roomViewModel.name);
        txtTimeToJoin.setText(roomViewModel.timeToJoin);
        tvFollwers.setText(roomViewModel.membersCount);
    }

    private void initViews() {
        imgOwner = findViewById(R.id.img_owner);
        txtFullNameHost = findViewById(R.id.txt_fullname_host);
        txtRoomName = findViewById(R.id.txt_room_name);
        txtTimeToJoin = findViewById(R.id.txt_time_to_join);
        tvFollwers = findViewById(R.id.tv_follwers);
        // Setup Grid View
        rv = findViewById(R.id.gvMessages);
        //gridView.setTranscriptMode(GridView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        rv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(new ItemDecorationAlbumColumns(this, ItemDecorationAlbumColumns.VERTICAL_LIST));
        adapter = new MessageAdapter(this, Globals.Messages);
        rv.setAdapter(adapter);

        // Listeners
        EditText editText = findViewById(R.id.editMessage);
        ImageButton btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> {
            chatService.Send(roomViewModel.Id + "", editText.getText().toString(), adapter);
            editText.setText("");
        });
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_delete_room:
                // If he is not the owner of the room, he will get back fail message :)
                chatService.DeleteRoom(User.CurrentRoom);
                break;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onBackPressed() {
        Globals.Messages.clear();
        super.onBackPressed();
    }

    //https://developer.android.com/guide/components/bound-services.html
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            ChatService.LocalBinder binder = (ChatService.LocalBinder) service;
            chatService = binder.getService();
            mBound = true;

            chatService.GetMessageHistory(User.CurrentRoom);
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
                    if (check == 0 && Globals.Messages.size() > 0) {
                        rv.smoothScrollToPosition(Globals.Messages.size() - 1);
                        check++;
                    }
                    break;
                case "joinLobby":
                    User.CurrentRoom = "Lobby";
                    chatService.Join(User.CurrentRoom);
                    chatService.GetMessageHistory(User.CurrentRoom);
                    getSupportActionBar().setTitle("Lobby");
                    break;
            }
        }
    } // MyReceiver

}
