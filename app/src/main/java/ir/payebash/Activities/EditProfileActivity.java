package ir.payebash.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.widget.SwitchCompat;
import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.Classes.NetworkUtils;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class EditProfileActivity extends AppCompatActivity {

    private Map<String, String> params = new HashMap<>();
    private LinearLayout llMainn;
    private ImageButton imgBack;
    private SwitchCompat compatSwitch;
    private EditText etAbout;
    private EditText etFavorites, etTelegram, etInstagram, etSoroosh, etGmail;
    private ProgressWheel cpv;
    private ProgressBar pb;
    private Button btnProfileUpdate;
    private ScrollView sv;

    private void assignViews() {
        llMainn = findViewById(R.id.ll_main);
        imgBack = findViewById(R.id.img_back);
        compatSwitch = findViewById(R.id.compatSwitch);
        etAbout = findViewById(R.id.et_about);
        etFavorites = findViewById(R.id.et_favorites);
        etTelegram = findViewById(R.id.et_telegram);
        etInstagram = findViewById(R.id.et_insta);
        etSoroosh = findViewById(R.id.et_soroosh);
        etGmail = findViewById(R.id.et_gmail);
        cpv = findViewById(R.id.cpv);
        pb = findViewById(R.id.pb);
        sv = findViewById(R.id.sv);
        btnProfileUpdate = findViewById(R.id.btn_profile_update);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        assignViews();
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (NetworkUtils.getConnectivity(EditProfileActivity.this) != false)
            getProfileInfo();
        else
            HSH.showtoast(EditProfileActivity.this, "خطا در اتصال به اینترنت");

        btnProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTelegram.getText().toString().trim().length() > 4 ||
                        etSoroosh.getText().toString().trim().length() > 4 ||
                        etInstagram.getText().toString().trim().length() > 4) {
                    pb.setVisibility(View.VISIBLE);
                    btnProfileUpdate.setEnabled(false);
                    btnProfileUpdate.setText("");
                    params.put(getString(R.string.UserId), Application.preferences.getString(getString(R.string.UserId), "0"));
                    params.put(getString(R.string.AboutMe), etAbout.getText().toString().trim());
                    params.put(getString(R.string.Favorites), etFavorites.getText().toString().trim());
                    params.put(getString(R.string.Telegram), etTelegram.getText().toString().trim());
                    params.put(getString(R.string.Instagram), etInstagram.getText().toString().trim());
                    params.put(getString(R.string.Soroosh), etSoroosh.getText().toString().trim());
                    params.put(getString(R.string.Gmail), etGmail.getText().toString().trim());
                    params.put(getString(R.string.IsShowMobile), String.valueOf(compatSwitch.isChecked()));
                    if (NetworkUtils.getConnectivity(EditProfileActivity.this) != false)
                        SendUserInfo();
                    else
                        HSH.showtoast(EditProfileActivity.this, "خطا در اتصال به اینترنت");
                } else {
                    HSH.showtoast(EditProfileActivity.this, "لطفا راه های ارتباطی خود را تکمیل نمایید");
                }
            }
        });
    }

    private void getProfileInfo() {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.VoterUserId), Application.preferences.getString(getString(R.string.UserId), "0"));
        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).getProfileDetails(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse
                    (Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200)
                    try {
                        JSONObject obj = new JSONObject(response.body().string());
                        setMainInfo(obj);
                        if (etGmail.getText().toString().length() > 10)
                            etGmail.setEnabled(false);
                        sv.setVisibility(View.VISIBLE);
                        cpv.setVisibility(View.GONE);
                        btnProfileUpdate.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                    }
                else
                    getProfileInfo();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                getProfileInfo();
            }
        });
    }

    private void SendUserInfo() {
        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).UpdateProfile(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {
                    HSH.editor(getString(R.string.Telegram), params.get(getString(R.string.Telegram)));
                    HSH.editor(getString(R.string.Instagram), params.get(getString(R.string.Instagram)));
                    HSH.editor(getString(R.string.Soroosh), params.get(getString(R.string.Soroosh)));
                    HSH.editor(getString(R.string.Gmail), params.get(getString(R.string.Gmail)));
                    HSH.showtoast(EditProfileActivity.this, "اطلاعات شما ثبت گردید");
                    finish();
                } else
                    SendUserInfo();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                SendUserInfo();
            }
        });
    }

    private void setMainInfo(JSONObject obj) {
        int a = llMainn.getChildCount();
        for (int i = 0; i < a; i++) {
            if (llMainn.getChildAt(i) instanceof EditText) {
                try {
                    View v = llMainn.getChildAt(i);
                    String s = obj.getString(v.getTag().toString()).trim();
                    ((EditText) v).setText(s.toLowerCase().equals("null") ? "" : s);
                } catch (Exception e) {
                }
            } else if (llMainn.getChildAt(i) instanceof Button) {
                try {
                    View v = llMainn.getChildAt(i);
                    compatSwitch.setChecked(obj.getBoolean(v.getTag().toString()));
                } catch (Exception e) {
                }
            }
        }
    }
}
