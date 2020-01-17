package ir.payebash.Activities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.Classes.NetworkUtils;
import ir.payebash.DI.MainComponent;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Models.PayeItem;
import ir.payebash.Models.ProfileItem;
import ir.payebash.Moudle.CircleImageView;
import ir.payebash.Moudle.CustomTextViewBold;
import ir.payebash.Moudle.TagLayout;
import ir.payebash.Moudle.TagView;
import ir.payebash.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressWheel pb;
    @Inject
    ImageLoader imageLoader;
    @Inject
    DisplayImageOptions options;
    private CustomTextViewBold txtName;
    private CircleImageView imgProfile;
    private TextView btnVote, txtTrustcount, txtActivityState, txtUserAge, txtAge, txtCity, txtFavorite, txtAbout;
    private TagLayout mTagLayout;
    private Button btnSoroosh, btnTelegram, btnInstagram;
    private ImageButton btnReport;
    private ProfileItem obj;
    private int Trustcount = 0, checkid = 0;

    private void assignViews() {
        txtName = findViewById(R.id.txt_name);
        imgProfile = findViewById(R.id.img_profile);
        btnVote = findViewById(R.id.btn_vote);
        txtActivityState = findViewById(R.id.txt_ActivityState);
        txtTrustcount = findViewById(R.id.txt_trust_count);
        txtUserAge = findViewById(R.id.txt_user_age);
        txtAge = findViewById(R.id.txt_age);
        txtCity = findViewById(R.id.txt_city);
        txtFavorite = findViewById(R.id.txt_favorites);
        txtAbout = findViewById(R.id.txt_about);
        mTagLayout = findViewById(R.id.mTagLayout);
        btnSoroosh = findViewById(R.id.btn_soroosh);
        btnTelegram = findViewById(R.id.btn_telegram);
        btnInstagram = findViewById(R.id.btn_instagram);
        btnReport = findViewById(R.id.btnReport);
        pb = findViewById(R.id.pb);

        btnSoroosh.setOnClickListener(this);
        btnTelegram.setOnClickListener(this);
        btnInstagram.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        assignViews();
        Application.getComponent().Inject(this);

        try {
            Cursor cr = Application.database.rawQuery("SELECT IsMine from RecentVisit WHERE Id='" + getIntent().getExtras().getString("PostId") + "' and IsMine = 'true'", null);
            cr.close();
            if (cr.getCount() > 0) {
                findViewById(R.id.txt_contact).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_contact_ways).setVisibility(View.VISIBLE);
            }
            if (getIntent().getExtras().getString("AdvertiserId").equals(Application.preferences.getString(getString(R.string.UserId), "0")))
                btnVote.setVisibility(View.GONE);
        } catch (Exception e) {
        }

        if (NetworkUtils.getConnectivity(UserProfileActivity.this) != false)
            GetProfileInfo();
        else
            HSH.showtoast(UserProfileActivity.this, "خطا در اتصال به اینترنت");

        HSH.setTextViewDrawableColor(btnVote, 150, 150, 150);
        try {
            MainComponent component = Application.get(UserProfileActivity.this).getComponent();
            imageLoader.displayImage(getIntent().getExtras().getString("profileimage").trim(), imgProfile, options);
        } catch (Exception e) {
        }
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btnVote.setOnClickListener(view -> Vote());

        imgProfile.setOnClickListener(this);
        btnReport.setOnClickListener(this);
    }

    private void GetProfileInfo() {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.VoteReciverUserId), getIntent().getExtras().getString("AdvertiserId"));
        params.put(getString(R.string.VoterUserId), Application.preferences.getString(getString(R.string.UserId), "0"));
        Call<ProfileItem> call = ApiClient.getClient().create(ApiInterface.class).getProfileDetails2(params);
        call.enqueue(new Callback<ProfileItem>() {
            @Override
            public void onResponse
                    (Call<ProfileItem> call, retrofit2.Response<ProfileItem> response) {
                if (response.code() == 200)
                    try {
                        findViewById(R.id.sv).setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);
                        obj = response.body();
                        Trustcount = Integer.parseInt(obj.getTrustedVoteCount());
                        txtName.setText(obj.getName() + " " + obj.getFamily());
                        txtUserAge.setText(String.format(getString(R.string.user_age), obj.getUserAge()));
                        txtTrustcount.setText(String.format(getString(R.string.trusted_vote), obj.getTrustedVoteCount()));
                        txtAge.setText((!obj.getAge().equals("") && null != obj.getAge()) ? String.format(getString(R.string.age), obj.getAge()) : "ذکر نشده است");
                        String tmp = obj.getAboutMe();
                        if (!tmp.equals("null") && !tmp.equals("")) {
                            txtAbout.setVisibility(View.VISIBLE);
                            txtAbout.setText(String.format(getString(R.string.aboutme), tmp));
                        }
                        String[] tmp1 = obj.getActivityState().split(",");
                        if (!tmp.equals("null") && !tmp.equals("")) {
                            txtActivityState.setVisibility(View.VISIBLE);
                            txtActivityState.setText(
                                    String.format(getString(R.string.host), tmp1[0])
                                            + "\n" +
                                            String.format(getString(R.string.takePart), tmp1[1]));
                        }
                        tmp = obj.getFavorites();
                        if (null != tmp && !tmp.equals("")) {
                            txtFavorite.setVisibility(View.VISIBLE);
                            String[] temp = tmp.split("#");
                            for (int j = 0; j < temp.length; j++) {
                                if (!temp[j].equals(""))
                                    TagLayout(temp[j], mTagLayout, j);
                            }
                        }

                        if (obj.getIsTrust().toLowerCase().equals("true"))
                            HSH.setTextViewDrawableColor(btnVote, 165, 220, 135);
                        else
                            HSH.setTextViewDrawableColor(btnVote, 150, 150, 150);

                        try {
                            Cursor cr = Application.database.rawQuery("SELECT * from Citys where id = '" + Integer.parseInt(obj.getCity()) + "'", null);
                            cr.close();
                            cr.moveToFirst();
                            txtCity.setText(String.format(getString(R.string.location), cr.getString(cr.getColumnIndex("StateCity"))));
                        } catch (Exception e) {
                            if (null != obj.getCity() && !obj.getCity().equals(""))
                                txtCity.setText(String.format(getString(R.string.location), obj.getCity()));
                            else
                                txtCity.setVisibility(View.GONE);
                        }

                        if (!obj.getTelegram().equals("") && !obj.getTelegram().equals("null"))
                            btnTelegram.setVisibility(View.VISIBLE);

                        if (!obj.getInstagram().equals("") && !obj.getInstagram().equals("null"))
                            btnInstagram.setVisibility(View.VISIBLE);

                        if (!obj.getSoroosh().equals("") && !obj.getSoroosh().equals("null"))
                            btnSoroosh.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                    }
                else {
                    pb.setVisibility(View.VISIBLE);
                    GetProfileInfo();
                }
            }

            @Override
            public void onFailure(Call<ProfileItem> call, Throwable t) {
                pb.setVisibility(View.VISIBLE);
                //GetProfileInfo();
            }
        });
    }

    private void Vote() {
        final Dialog loading = HSH.onProgress_Dialog(UserProfileActivity.this, "شکیبا باشید ...");
        loading.show();
        loading.setCancelable(true);
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.VoterUserId), Application.preferences.getString(getString(R.string.UserId), "0"));
        params.put(getString(R.string.VoteReciverUserId), getIntent().getExtras().getString("AdvertiserId"));
        Call<ResponseBody> call = ApiClient.getClient().create(ApiInterface.class).Vote(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse
                    (Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200)
                    try {
                        String s = response.body().string();
                        loading.dismiss();
                        if (s.toLowerCase().equals("true")) {
                            HSH.setTextViewDrawableColor(btnVote, 165, 220, 135);
                            Trustcount += 1;
                            txtTrustcount.setText(String.format(getString(R.string.trusted_vote), String.valueOf(Trustcount)));
                        } else {
                            HSH.setTextViewDrawableColor(btnVote, 150, 150, 150);
                            Trustcount -= 1;
                            txtTrustcount.setText(String.format(getString(R.string.trusted_vote), String.valueOf(Trustcount)));
                        }
                    } catch (Exception e) {
                    }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loading.dismiss();
                HSH.showtoast(UserProfileActivity.this, getString(R.string.retry));
            }
        });
    }

    private void SendingReportService(final int checkid, final Dialog dialog_wait) {

        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.ComplainantId), Application.preferences.getString(getString(R.string.UserId), "0"));
        params.put(getString(R.string.UserId), getIntent().getExtras().getString("AdvertiserId"));
        params.put("Type", String.valueOf(checkid));
        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).SendingReportService(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                dialog_wait.dismiss();
                HSH.showtoast(UserProfileActivity.this, "گزارش شما با موفقیت ثبت گردید");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                HSH.showtoast(UserProfileActivity.this, "مجدد تلاش کنید /مشکل در دریافت اطلاعات");
            }
        });
    }


    private void TagLayout(final String title, final TagLayout mTagLayout, final int i) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    final TagView tagView = new TagView(UserProfileActivity.this);
                    tagView.setText("#" + title);
                    tagView.setBackgroundResource(R.drawable.tagview_shape2);
                    tagView.setTextColor(Color.WHITE);
                    mTagLayout.addTagView(tagView);
                } catch (Exception e) {
                }
            }
        }, 100 * i);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_telegram:
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=" + obj.getTelegram()));
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://web.telegram.org/#/im?p=@" + obj.getTelegram())));
                }
                break;
            case R.id.btn_instagram:
                Uri uri = Uri.parse("http://instagram.com/_u/" + obj.getInstagram());
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                likeIng.setPackage("com.instagram.android");
                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/" + obj.getInstagram())));
                }
                break;
            case R.id.btn_soroosh:
                try {
                    uri = Uri.parse("https://sapp.ir/" + obj.getSoroosh());
                    likeIng = new Intent(Intent.ACTION_VIEW, uri);
                    likeIng.setPackage("mobi.mmdt.ott");
                    try {
                        startActivity(likeIng);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://sapp.ir/" + obj.getSoroosh())));
                    }
                } catch (Exception e) {
                }
                break;
            case R.id.img_profile: {
                try {
                    final Bundle bundle = new Bundle();
                    Intent i = new Intent(UserProfileActivity.this, ViewPagerActivity.class);
                    PayeItem item = new PayeItem();
                    item.setImages(getIntent().getExtras().getString("profileimage").trim());
                    bundle.putSerializable("feed", item);
                    i.putExtras(bundle);
                    startActivity(i);
                } catch (Exception e) {
                }
                break;
            }
            case R.id.btnReport: {
                final Dialog dialog = new Dialog(UserProfileActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_report_user);
                TextView txt_send = dialog.findViewById(R.id.txt_send);
                TextView txt_reject = dialog.findViewById(R.id.txt_reject);
                final RadioGroup radioGroup01 = dialog.findViewById(R.id.radioGroup);

                radioGroup01.setOnCheckedChangeListener((group, checkedId) -> checkid = Integer.parseInt(dialog.findViewById(checkedId).getTag().toString()));

                txt_send.setOnClickListener(v12 -> {
                    if (radioGroup01.getCheckedRadioButtonId() != -1) {
                        if (NetworkUtils.getConnectivity(UserProfileActivity.this) != false) {
                            Dialog dialog_wait = HSH.onProgress_Dialog(UserProfileActivity.this, "شکیبا باشید ...");
                            SendingReportService(checkid, dialog_wait);
                            dialog.dismiss();
                        } else
                            HSH.showtoast(UserProfileActivity.this, "خطا در اتصال به اینترنت");
                    } else
                        HSH.showtoast(UserProfileActivity.this, "یک گزینه را انتخاب کنید.");
                });

                txt_reject.setOnClickListener(v1 -> dialog.dismiss());
                HSH.dialog(dialog);
                dialog.show();
                break;
            }
        }
    }
}
