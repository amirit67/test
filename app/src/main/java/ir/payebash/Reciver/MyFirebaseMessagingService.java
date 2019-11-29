package ir.payebash.Reciver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

import androidx.core.app.NotificationCompat;
import ir.payebash.Activities.MainActivity;
import ir.payebash.Activities.PostDetailsActivity;
import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            //Object f = remoteMessage;
            sendNotification2(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        } else if (remoteMessage.getData() != null) {
            //Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            //Object f = remoteMessage;
            Map<String, String> data = remoteMessage.getData();
            try {
                sendNotification(data);
            } catch (Exception e) {
            }
        }
    }
    // [END receive_message]

    private void sendNotification(final Map<String, String> data) {

        Intent intent = new Intent(this, PostDetailsActivity.class);
        intent.putExtra(getString(R.string.PostId), data.get("id"));
        intent.putExtra(getString(R.string.Title), data.get("title"));
        intent.putExtra(getString(R.string.Deadline), data.get("deadLine"));
        intent.putExtra(getString(R.string.Subject), data.get("subject"));
        intent.putExtra(getString(R.string.city), data.get("city"));
        intent.putExtra(getString(R.string.CityDate), data.get("cityDate"));
        intent.putExtra(getString(R.string.Tag), data.get("tag"));
        intent.putExtra(getString(R.string.Cost), data.get("cost"));
        intent.putExtra(getString(R.string.Images), data.get("imgUrl"));

        try {
            if (data.get("type").equals("EventRequest")) {
                JSONArray jArray = new JSONArray();// /ItemDetail jsonArray
                JSONObject jGroup = new JSONObject();
                jGroup.put(getString(R.string.PostId), data.get("id"));
                jGroup.put(getString(R.string.Title), data.get("title"));
                jGroup.put(getString(R.string.Deadline), data.get("deadLine"));
                jGroup.put(getString(R.string.Subject), data.get("subject"));
                jGroup.put(getString(R.string.city), data.get("city"));
                jGroup.put(getString(R.string.CityDate), data.get("cityDate"));
                jGroup.put(getString(R.string.Tag), data.get("tag"));
                jGroup.put(getString(R.string.Cost), data.get("cost"));
                jGroup.put(getString(R.string.Images), data.get("imgUrl"));
                jArray.put(jGroup);
                Cursor cr = Application.database.rawQuery("SELECT Id from RecentVisit WHERE Id='" + data.get("id").trim() + "' and IsMine = 'true'", null);
                if (cr.getCount() < 1) {
                    String query = "INSERT INTO RecentVisit(Id,IsFavorite,IsWanted) VALUES " +
                            "('" + data.get("id").trim() + "','false','true') ";
                    Application.database.execSQL(query);
                } else {
                    String query = "UPDATE RecentVisit SET " +
                            "IsWanted = 'true' " +
                            "WHERE Id = '" + data.get("id").trim() + "' ";
                    Application.database.execSQL(query);
                }
            }
        } catch (Exception e) {
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Random randomGenerator = new Random();
        final int randomInt = randomGenerator.nextInt(100);

        Call<ResponseBody> call = ApiClient.getClient().create(ApiInterface.class).fetchImage(getString(R.string.url) + "Images/payebash/Thumbnail/" + data.get("imgUrl") + ".jpg");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        Bitmap bm = BitmapFactory.decodeStream(response.body().byteStream());
                        notificationBuilder.setStyle(
                                new NotificationCompat.BigPictureStyle()
                                        .setBigContentTitle(data.get("title"))
                                        .setSummaryText("یک نفر شما را به پایه باش فراخواند")
                                        .bigPicture(bm));
                        if ((data.get("type").equals("Comment") && Application.preferences.getString("CommentNotif", "true").equals("true"))
                                ||
                                (data.get("type").equals("Beup") && Application.preferences.getString("BeupNotif", "true").equals("true"))
                                ||
                                (data.get("type").equals("EventRequest") && Application.preferences.getString("EventRequestNotif", "true").equals("true"))
                                ||
                                (data.get("type").equals("Cancel"))) {
                            notificationManager.notify(new Random().nextInt(1000) /* ID of notification */, notificationBuilder.build());
                        }
                    } else {
                        if ((data.get("type").equals("Comment") && Application.preferences.getString("CommentNotif", "true").equals("true"))
                                ||
                                (data.get("type").equals("Beup") && Application.preferences.getString("BeupNotif", "true").equals("true"))
                                ||
                                (data.get("type").equals("EventRequest") && Application.preferences.getString("EventRequestNotif", "true").equals("true"))
                                ||
                                (data.get("type").equals("Cancel"))) {
                            notificationBuilder.setContentTitle(data.get("title")).setContentText("یک نفر شما را به پایه باش فراخواند");
                            notificationManager.notify(randomInt /* ID of notification */, notificationBuilder.build());
                        }
                    }
                } else {
                    if ((data.get("type").equals("Comment") && Application.preferences.getString("CommentNotif", "true").equals("true"))
                            ||
                            (data.get("type").equals("Beup") && Application.preferences.getString("BeupNotif", "true").equals("true"))
                            ||
                            (data.get("type").equals("EventRequest") && Application.preferences.getString("EventRequestNotif", "true").equals("true"))
                            ||
                            (data.get("type").equals("Cancel"))) {
                        notificationBuilder.setContentTitle(data.get("title")).setContentText("یک نفر شما را به پایه باش فراخواند");
                        notificationManager.notify(randomInt /* ID of notification */, notificationBuilder.build());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if ((data.get("type").equals("Comment") && Application.preferences.getString("CommentNotif", "true").equals("true"))
                        ||
                        (data.get("type").equals("Beup") && Application.preferences.getString("BeupNotif", "true").equals("true"))
                        ||
                        (data.get("type").equals("EventRequest") && Application.preferences.getString("EventRequestNotif", "true").equals("true"))
                        ||
                        (data.get("type").equals("Cancel"))) {
                    notificationBuilder.setContentTitle(data.get("title")).setContentText("یک نفر شما را به پایه باش فراخواند");
                    notificationManager.notify(randomInt /* ID of notification */, notificationBuilder.build());
                }
            }
        });
    }


    private void sendNotification2(final String messageTitle, final String messageBody) {

        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Random randomGenerator = new Random();
        final int randomInt = randomGenerator.nextInt(100);

        notificationBuilder.setContentTitle(messageTitle).setContentText(messageBody);
        if (messageTitle.contains(getString(R.string.notifSign)) && messageBody.toLowerCase().contains("http")) {
            HSH.editor(getString(R.string.baseUrl), messageBody);
        } else
            notificationManager.notify(randomInt /* ID of notification */, notificationBuilder.build());
    }
}