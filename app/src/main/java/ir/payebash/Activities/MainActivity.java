package ir.payebash.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.payebash.Application;
import ir.payebash.Classes.HSH;
import ir.payebash.Fragments.ActivitiesFragment;
import ir.payebash.Fragments.HomeFragment;
import ir.payebash.Fragments.MyPayeFragment;
import ir.payebash.Fragments.NotificationFragment;
import ir.payebash.Fragments.ProfileFragment;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice.TitleMain;
import ir.payebash.Models.UserItem;
import ir.payebash.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static ir.payebash.Classes.HSH.openFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TitleMain/*, HideActionbar*/ {

    public static LinearLayout ll_bottomNavigation;
    BottomNavigationView bottomNavigationView;
    private HomeFragment home_fragment = null;
    private ActivitiesFragment activities_fragment = null;
    private NotificationFragment notification_fragment = null;
    private ProfileFragment profile_fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!Application.preferences.getString(getString(R.string.IsAuthenticate), "").equals("true")) {
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            finish();
        } else {
           /* PackageManager pm = getPackageManager();
            ComponentName componentName = new ComponentName(MainActivity.this, IncomingSms.class);
            pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);*/
            UpdateChecker();
            IsAuthenticate();
            Toolbar toolbar = findViewById(R.id.toolbar_top);
            setSupportActionBar(toolbar);
            setTitle("");
            findViewById(R.id.btn_new_event).setOnClickListener(view -> {
                final SweetAlertDialog dialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                String s = Application.preferences.getString("MobileTemp", "");
                if (s.length() < 11) {
                    dialog.setCustomImage(R.mipmap.mobile);
                    dialog.setTitleText("احراز هویت");
                    dialog.setContentText("برای ثبت رویداد لازم است ابتدا اطلاعات خود را ثبت نمایید");
                    dialog.setConfirmText("باشه");
                    dialog.setConfirmClickListener(sDialog -> {
                        Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                        i.putExtra("Type", "Update");
                        startActivityForResult(i, 132);
                        sDialog.dismissWithAnimation();
                    });
                    HSH.dialog(dialog);
                    dialog.show();
                } else if (Application.preferences.getString(getString(R.string.Telegram), "").length() < 5 &&
                        Application.preferences.getString(getString(R.string.Soroosh), "").length() < 5 && !dialog.isShowing()) {
                    dialog.setCustomImage(R.mipmap.completed_info);
                    dialog.setTitleText("تکمیل اطلاعات");
                    dialog.setContentText("قبل از ثبت رویداد جدید پروفایل خود را تکمیل نمایید");
                    dialog.setConfirmText("باشه");
                    dialog.setConfirmClickListener(sDialog -> {
                        Intent i = new Intent(MainActivity.this, EditProfileActivity.class);
                        startActivityForResult(i, 132);
                        sDialog.dismissWithAnimation();
                    });
                    HSH.dialog(dialog);
                    dialog.show();
                } else {
                    Intent intent = new Intent(MainActivity.this, PostRegisterActivity.class);
                    startActivityForResult(intent, 321);
                }
            });

            bottomNavigationView = findViewById(R.id.bottom_navigation);
            ll_bottomNavigation = findViewById(R.id.linearLayout);
            home_fragment = new HomeFragment();
            openFragment(MainActivity.this, home_fragment);
            //Application.fra = home_fragment;

            bottomNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);
            bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
                menuItem.setChecked(true);
                if (menuItem.getItemId() == R.id.action_home) {
                    if (home_fragment == null)
                        home_fragment = new HomeFragment();
                    openFragment(MainActivity.this, home_fragment);
                }
                if (menuItem.getItemId() == R.id.action_profile) {
                    if (profile_fragment == null)
                        profile_fragment = new ProfileFragment();
                    openFragment(MainActivity.this, profile_fragment);
                } else if (menuItem.getItemId() == R.id.action_notify) {
                    if (notification_fragment == null)
                        notification_fragment = new NotificationFragment();
                    openFragment(MainActivity.this, notification_fragment);
                } else if (menuItem.getItemId() == R.id.action_event) {
                    if (activities_fragment == null)
                        activities_fragment = new ActivitiesFragment();
                    openFragment(MainActivity.this, activities_fragment);
                }
                return false;
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321 && null != data) {
            Application.myAds = 42907631;
            MyPayeFragment fra = new MyPayeFragment();
            HSH.openFragment(MainActivity.this, fra);
        } else if (requestCode == 132 &&
                Application.preferences.getString(getString(R.string.mobile), "").length() > 10 &&
                (
                        Application.preferences.getString(getString(R.string.Telegram), "").length() > 4
                                ||
                                Application.preferences.getString(getString(R.string.Soroosh), "").length() > 4
                                ||
                                Application.preferences.getString(getString(R.string.Instagram), "").length() > 4
                )
        ) {
            Intent intent = new Intent(MainActivity.this, PostRegisterActivity.class);
            startActivityForResult(intent, 321);
        } else
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            try {
                String tag = getSupportFragmentManager()
                        .getBackStackEntryAt(getSupportFragmentManager()
                                .getBackStackEntryCount() - 2).getName();

               /* if (tag.equals("HomeFragment"))
                    HSH.setMainDrawableColor(ll_bottomNavigation, txt_home);
                else if (tag.equals("NotificationFragment"))
                    HSH.setMainDrawableColor(ll_bottomNavigation, txt_notification);

                else if (tag.equals("ActivitiesFragment"))
                    HSH.setMainDrawableColor(ll_bottomNavigation, txt_events);

                else if (tag.equals("ProfileFragment"))
                    HSH.setMainDrawableColor(ll_bottomNavigation, txt_profile);*/

                getSupportFragmentManager().popBackStack();
                return true;
            } catch (Exception e) {
                /*if (home_fragment.adapter.Tempfeed.size() > 0) {
                    home_fragment.adapter.Notify(home_fragment.adapter.Tempfeed);
                    home_fragment.adapter.ClearTempFeed();
                }
                else*/
                exit();
            }

        }
        return false;
    }

    private void IsAuthenticate() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        //Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    Map<String, String> params = new HashMap<>();
                    params.put(getString(R.string.UserId), Application.preferences.getString(getString(R.string.UserId), "0"));
                    params.put(getString(R.string.FbToken), task.getResult().getToken());
                    Call<UserItem> call =
                            ApiClient.getClient().create(ApiInterface.class).getAuthenticate(params);
                    call.enqueue(new Callback<UserItem>() {
                        @Override
                        public void onResponse(Call<UserItem> call, retrofit2.Response<UserItem> response) {
                            if (response.code() == 200)
                                try {
                                    UserItem item = response.body();
                                    if (item.getIsAuthenticate().toLowerCase().equals("false")) {
                                        HSH.showtoast(MainActivity.this, "حساب کاربری شما مسدود گردید");
                                        new Handler().postDelayed(new Runnable() {
                                            public void run() {
                                                Intent a = new Intent(Intent.ACTION_MAIN);
                                                a.addCategory(Intent.CATEGORY_HOME);
                                                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(a);
                                                System.exit(0);
                                                finish();
                                            }
                                        }, 2500);
                                    }
                                    HSH.editor(getString(R.string.FullName), item.getFullName().trim().toLowerCase());
                                    if (!item.getProfileImage().contains("https://"))
                                        HSH.editor(getString(R.string.ProfileImage), getString(R.string.url) + "Images/Users/" + item.getProfileImage() + ".jpg");
                                    else
                                        HSH.editor(getString(R.string.ProfileImage), item.getProfileImage());
                                    HSH.editor(getString(R.string.Instagram), item.getInstagram().trim().toLowerCase());
                                    HSH.editor(getString(R.string.Telegram), item.getTelegram().trim().toLowerCase());
                                    HSH.editor(getString(R.string.Soroosh), item.getSoroosh().trim().toLowerCase());
                                    HSH.editor(getString(R.string.Gmail), item.getGmail().trim().toLowerCase());
                        /*Cursor cr = Application.database.rawQuery("SELECT Id from RecentVisit WHERE data = '" + item.getMobile() + "'", null);
                        if (cr.getCount() > 0 && item.getMobile().length() > 10) */
                                    HSH.editor(getString(R.string.mobile), item.getMobile());
                                    HSH.editor("MobileTemp", item.getMobileTemp());

                                    String s = item.getServicesIds().trim().toLowerCase();
                                    if (s.length() > 3 && !s.equals("null")) {
                                        String[] temp = s.trim().split(",");
                                        for (int i = 0; i < temp.length; i++) {
                                            temp[i] = temp[i].replace("\"", "");
                                            if (temp[i].contains("-"))
                                                SubscribeTopic(temp[i]);
                                        }

                                        if (!s.equals("") && !s.equals("null")) {
                                            HSH.editor(getString(R.string.ServicesIds), s.trim());
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            else if (!Application.preferences.getString(getString(R.string.UserId), "00000").equals("00000"))
                                IsAuthenticate();
                        }

                        @Override
                        public void onFailure(Call<UserItem> call, Throwable t) {
                            if (!Application.preferences.getString(getString(R.string.UserId), "00000").equals("00000"))
                                IsAuthenticate();
                        }
                    });
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

    public void exit() {
        final SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        dialog.setTitleText("خروج از برنامه");
        dialog.setContentText("خوشحال میشیم نظر بدین");
        dialog.setConfirmText("نظر میدم");
        dialog.setCancelText("فعلا نه!خروج");
        dialog.setConfirmClickListener(sDialog -> {
            try {
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setData(Uri.parse("bazaar://details?id=" + getPackageName()));
                intent.setPackage("com.farsitel.bazaar");
                startActivity(intent);
                dialog.dismiss();
            } catch (Exception e) {
            }
        });
        dialog.setCancelClickListener(sweetAlertDialog -> {
            sweetAlertDialog.dismissWithAnimation();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            System.exit(0);
            finish();
        });
        HSH.dialog(dialog);
        dialog.show();
    }

    private void UpdateChecker() {
        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).GetUpdate();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200)
                    try {
                        JSONObject result = new JSONObject(response.body().string().trim());
                        Application.editor.putInt(getString(R.string.Feepayable), result.getInt(getString(R.string.Feepayable)));
                        Application.editor.commit();
                        String _version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                        String newVersion = result.getString(getString(R.string.VersionName));
                        if (!newVersion.equals(_version) && !newVersion.equals("")) {
                            String REMOTE_APK_URL = result.getString(getString(R.string.DownloadUrl)).trim();
                            UpdateChecker uc = new UpdateChecker(MainActivity.this, newVersion, REMOTE_APK_URL);
                            uc.curVersion = _version;
                            uc.localApkName = "PayeBash.apk";
                            uc.alertTitle = "بروز رسانی";
                            uc.alertMessage = result.getString(getString(R.string.Description)).trim();
                            uc.alertTitleError = "خطا در بارگذاری";
                            uc.alertMessageError = "امکان دانلود فایل وجود ندارد.";
                            uc.progressMessage = "در حال بارگذاری...";
                            uc.startUpdateChecker();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        HSH.setMainDrawableColor(ll_bottomNavigation, v);
        try {
            HSH._snackbar.dismiss();
        } catch (Exception e) {
        }
    }

    @Override
    public void FragName(String name) {
        ((TextView) findViewById(R.id.txt_title)).setText(name);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(false);
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }*/
}
