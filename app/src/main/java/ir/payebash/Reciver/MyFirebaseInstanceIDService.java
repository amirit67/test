package ir.payebash.Reciver;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.HashMap;
import java.util.Map;

import ir.payebash.Application;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static ir.payebash.Application.preferences;


public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onNewToken(String s) {
        Log.d(TAG, "Refreshed token: " + s);
        sendRegistrationToServer(s);
    }

    public void sendRegistrationToServer(final String token) {

        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.UserId), preferences.getString(getString(R.string.UserId), "0"));
        params.put(getString(R.string.FbToken), token);
        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).UpdateUserFbToken(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                String[] temp = preferences.getString(getString(R.string.ServicesIds), "").trim().split(",");
                for (int i = 0; i < temp.length; i++)
                    if (!temp[i].equals(""))
                        SubscribeTopic(temp[i]);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    private void SubscribeTopic(String ServiceId) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        //Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    Call<ResponseBody> call = ApiClient.getClient3().create(ApiInterface.class).Subscribe(task.getResult().getToken(), ServiceId);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                        }
                    });
                });

    }
}