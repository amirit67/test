package ir.payebash.chat;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import ir.payebash.adapters.MessageAdapter;
import ir.payebash.Application;
import ir.payebash.BuildConfig;
import ir.payebash.R;
import ir.payebash.helpers.Globals;
import ir.payebash.helpers.PrefsManager;
import ir.payebash.helpers.User;
import ir.payebash.modelviewsChat.MessageViewModel;
import ir.payebash.modelviewsChat.RoomViewModel;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

public class ChatService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private HubConnection connection;
    private HubProxy proxy;
    private Handler handler;

    private String serverUrl = BuildConfig.BaseUrl;
    private String hubName = "ChatHub";

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // We are using binding. do we really need this...?
        if (!StartHubConnection()) {
            ExitWithMessage("Chat Service failed to start!");
        }
        if (!RegisterEvents()) {
            ExitWithMessage("End-point error: Failed to register Events!");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        connection.stop();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (!StartHubConnection()) {
            ExitWithMessage("Chat Service failed to start!");
        }

        if (!RegisterEvents()) {
            ExitWithMessage("End-point error: Failed to register Events!");
        }

        return mBinder;
    }

    // https://developer.android.com/guide/components/bound-services.html
    public class LocalBinder extends Binder {
        public ChatService getService() {
            return ChatService.this;
        }
    }

    private boolean StartHubConnection() {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        // Create Connection
        connection = new HubConnection(serverUrl);
        connection.setCredentials(/*User.loginCredentials*/PrefsManager.loadAuthCookie(this));
        connection.getHeaders().put("Device", "Mobile");
        connection.getHeaders().put(Application.resources.getString(R.string.UserId),
                Application.preferences.getString(Application.resources.getString(R.string.UserId), "0000"));
        connection.getHeaders().put("Authorization",
                "bearer " + Application.preferences.getString(Application.resources.getString(R.string.Token), "0000"));

        // Create Proxy
        proxy = connection.createHubProxy(hubName);

        // Establish Connection
        ClientTransport clientTransport = new ServerSentEventsTransport(connection.getLogger());
        SignalRFuture<Void> signalRFuture = connection.start(clientTransport);

        try {
            signalRFuture.get();
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException e) {
            return false;
        }

        return true;
    }

    private boolean RegisterEvents() {

        Handler mHandler = new Handler(Looper.getMainLooper());
        try {
            proxy.on("newMessage", msg -> mHandler.post(() -> {
                msg.IsMine = msg.From.equals(User.DisplayName) ? 1 : 0;
                Globals.Messages.add(msg);
                sendBroadcast(new Intent().setAction("notifyAdapter"));
            }), MessageViewModel.class);

            proxy.on("addChatRoom", room -> mHandler.post(() -> {
                Globals.Rooms.add(/*room.Name*/room);
                sendBroadcast(new Intent().setAction("notifyAdapter"));
            }), RoomViewModel.class);

            proxy.on("removeChatRoom", room -> mHandler.post(() -> {
                Globals.Rooms.remove(/*room.Name*/room);
                sendBroadcast(new Intent().setAction("notifyAdapter"));
            }), RoomViewModel.class);

            proxy.on("getProfileInfo", (displayName, avatar) -> mHandler.post(() -> {
                User.DisplayName = displayName;
                User.Avatar = avatar;
            }), String.class, String.class);

            proxy.on("onError", error -> mHandler.post(() -> handler.post(() -> Toast.makeText(ChatService.this, error, Toast.LENGTH_SHORT).show())), String.class);

            proxy.on("onRoomDeleted", info -> mHandler.post(() -> handler.post(() -> {
                Toast.makeText(ChatService.this, info, Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent().setAction("joinLobby"));
            })), String.class);

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public void Send(String roomName, String message, MessageAdapter adapter) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                proxy.invoke("Send", roomName, message).done(aVoid -> {

                    MessageViewModel messageViewModels = new MessageViewModel();
                    messageViewModels.Content = message;
                    messageViewModels.Timestamp = "";
                    messageViewModels.IsMine = 1;
                    Globals.Messages.add(messageViewModels);
                    adapter.notifyItemInserted(Globals.Messages.size() - 1);

                });
                return null;
            }
        }.execute();
    }

    public void Join(String roomName) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                User.CurrentRoom = roomName;
                proxy.invoke("Join", roomName);
                return null;
            }
        }.execute();
    }

    public void CreateRoom(String roomName) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                proxy.invoke("CreateRoom", roomName);
                return null;
            }
        }.execute();
    }

    public void DeleteRoom(String roomName) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                proxy.invoke("DeleteRoom", roomName);
                return null;
            }
        }.execute();
    }

    public void GetMessageHistory(String roomName) {
        User.UserName = Application.preferences.getString(getString(R.string.UserName), "");
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                proxy.invoke(MessageViewModel[].class, "GetMessageHistory", roomName).done(
                        messageViewModels -> {
                            Globals.Messages.clear();
                            Globals.Messages.addAll(Arrays.asList(messageViewModels));

                            for (MessageViewModel m : Globals.Messages) {
                                try {
                                    m.IsMine = m.Username.equals(User.UserName) ? 1 : 0;
                                } catch (Exception e) {
                                }
                            }
                            sendBroadcast(new Intent().setAction("notifyAdapter"));
                        });
                return null;
            }
        }.execute();
    }

    public void GetRooms() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                proxy.invoke(RoomViewModel[].class, "GetRooms").done(
                        rooms -> {
                            Globals.Rooms.clear();

                            for (RoomViewModel room : rooms)
                                Globals.Rooms.add(room);

                            sendBroadcast(new Intent().setAction("notifyAdapter"));
                        });
                return null;
            }
        }.execute();
    }

    private void ExitWithMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> stopSelf(), 3000);
    }

}
