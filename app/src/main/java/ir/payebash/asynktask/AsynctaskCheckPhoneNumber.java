package ir.payebash.asynktask;


import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.Map;

import ir.payebash.Activities.MainActivity;
import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Models.UserItem;
import ir.payebash.R;
import retrofit2.Call;
import retrofit2.Callback;

public class AsynctaskCheckPhoneNumber {

    private Activity ac;
    private Map<String, String> params;
    private ProgressBar pb;
    private Button bt_go;

    public AsynctaskCheckPhoneNumber(Activity ac, Map<String, String> params, ProgressBar pb, Button bt_go) {
        this.ac = ac;
        this.params = params;
        this.pb = pb;
        this.bt_go = bt_go;
    }

    public void getData() {
        params.put(ac.getString(R.string.UserId), Application.preferences.getString(ac.getString(R.string.UserId), ""));
        Call<UserItem> call =
                ApiClient.getClient().create(ApiInterface.class).checkPhoneNumber(params);
        call.enqueue(new Callback<UserItem>() {
            @Override
            public void onResponse(Call<UserItem> call, retrofit2.Response<UserItem> response) {
                try {
                    UserItem user = response.body();
                    String UserId = user.getUserId();
                    if (UserId.equals("0")) {
                        String Message = user.getMessage();
                        HSH.showtoast(ac, Message);
                    } else {
                        try {
                            HSH.editor(ac.getString(R.string.UserId), user.getUserId());
                            HSH.editor(ac.getString(R.string.mobile), params.get(ac.getString(R.string.mobile)));
                            HSH.editor(ac.getString(R.string.FullName), user.getFullName());
                            if (!user.getProfileImage().contains("https://"))
                                HSH.editor(ac.getString(R.string.ProfileImage), ac.getString(R.string.url) + "Images/Users/" + user.getProfileImage() + ".jpg");
                            else
                                HSH.editor(ac.getString(R.string.ProfileImage), user.getProfileImage());
                            //HSH.editor(ac.getString(R.string.ServicesIds), user.getServicesIds().trim());
                            HSH.editor(ac.getString(R.string.IsAuthenticate), "true");
                            Intent intent = new Intent(ac, MainActivity.class);
                            ac.startActivity(intent);
                            ac.finish();
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e) {
                }
                try {
                    bt_go.setEnabled(true);
                    pb.setVisibility(View.GONE);
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<UserItem> call, Throwable t) {
                HSH.showtoast(ac, "خطا در ارتباط با سرور");
                try {
                    pb.setVisibility(View.GONE);
                    bt_go.setEnabled(true);
                } catch (Exception e) {
                }
            }
        });
    }


}


