package ir.payebash.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.payebash.classes.HSH;
import ir.payebash.fragments.HomeFragment;
import ir.payebash.fragments.user.MyPayeFragment;
import ir.payebash.fragments.NewAddressActivity;
import ir.payebash.fragments.createEvent.CreateEventDialog;
import ir.payebash.fragments.request.ParentFragment;
import ir.payebash.fragments.user.ActivitiesFragment;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.Interfaces.IWebservice.TitleMain;
import ir.payebash.models.UserItem;
import ir.payebash.models.user.UserInfoModel;
import ir.payebash.R;
import ir.payebash.utils.FragmentStack;
import ir.payebash.utils.reviewratings.BarLabels;
import ir.payebash.utils.reviewratings.RatingReviews;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends BaseActivity implements View.OnClickListener, TitleMain, IWebservice.IBottomSheetNavigation {

    private int CREATEEVENT = 321;
    public static LinearLayout ll_bottomNavigation;
    private BottomAppBar bottomAppBar;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment home_fragment = null;
    private ParentFragment roomsFragment = null;
    private NewAddressActivity newAddressActivity = null;
    private ActivitiesFragment activitiesFragment = null;
    private FloatingActionButton fab;
    private FragmentStack fragmentStack;
    private CreateEventDialog createEventDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (preferences.getString(getString(R.string.UserId), "").equals("")
                || preferences.getString(getString(R.string.UserName), "").equals("")) {
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            finish();
        } else {

            fragmentStack = new FragmentStack(MainActivity.this, getSupportFragmentManager(), R.id.frame);

            initViews();
            UpdateChecker();
            IsAuthenticate();
            Toolbar toolbar = findViewById(R.id.toolbar_top);
            setSupportActionBar(toolbar);
            setTitle("");
            fab.setOnClickListener(view -> showBottomsheetCreateEvent());

            bottomNavigationView = findViewById(R.id.bottom_navigation);
            ll_bottomNavigation = findViewById(R.id.linearLayout);
            // home_fragment = new HomeFragment();
            home_fragment = new HomeFragment();
            fragmentStack.replace(home_fragment);
            //Application.fra = home_fragment;

            bottomNavigationView.getMenu().findItem(R.id.action_list).setChecked(true);
            bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
                menuItem.setChecked(true);
                if (menuItem.getItemId() == R.id.action_home) {
                    if (activitiesFragment == null)
                        activitiesFragment = new /*NewAddressActivity*/ActivitiesFragment();
                    fragmentStack.replace(activitiesFragment);
                } else if (menuItem.getItemId() == R.id.action_list) {
                   /* if (profile_fragment == null)
                        profile_fragment = new ProfileFragment();
                    openFragment(MainActivity.this, profile_fragment);*/
                    if (home_fragment == null)
                        home_fragment = new HomeFragment();
                    fragmentStack.replace(home_fragment);

                } else if (menuItem.getItemId() == R.id.action_creat_event) {
                    Intent intent = new Intent(MainActivity.this, PostRegisterActivity.class);
                    startActivityForResult(intent, CREATEEVENT);
                } else if (menuItem.getItemId() == R.id.action_notify) {
                    if (roomsFragment == null)
                        roomsFragment = new ParentFragment();
                    fragmentStack.replace(roomsFragment);
                } else if (menuItem.getItemId() == R.id.action_event) {
                    if (newAddressActivity == null)
                        newAddressActivity = new NewAddressActivity();
                    fragmentStack.replace(newAddressActivity);
                }
                return false;
            });
        }
    }

    private void initViews() {
        bottomAppBar = findViewById(R.id.bottomAppBar);
        fab = findViewById(R.id.btn_new_event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }

        if (requestCode == CREATEEVENT && null != data) {
            MyPayeFragment fra = new MyPayeFragment();
            fragmentStack.replace(fra);
        } else if (requestCode == 132 &&
                preferences.getString(getString(R.string.mobile), "").length() > 10 &&
                (
                        preferences.getString(getString(R.string.Telegram), "").length() > 4
                                ||
                                preferences.getString(getString(R.string.Soroosh), "").length() > 4
                                ||
                                preferences.getString(getString(R.string.Instagram), "").length() > 4
                )
        ) {
            Intent intent = new Intent(MainActivity.this, PostRegisterActivity.class);
            startActivityForResult(intent, CREATEEVENT);
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

                if (tag.equals(HomeFragment.class.getSimpleName()))
                    bottomNavigationView.getMenu().getItem(0).setChecked(true);
                else if (tag.equals(ParentFragment.class.getSimpleName()))
                    bottomNavigationView.getMenu().getItem(3).setChecked(true);
                else if (tag.equals(ActivitiesFragment.class.getSimpleName()))
                    bottomNavigationView.getMenu().getItem(4).setChecked(true);

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
                    params.put(getString(R.string.UserId), preferences.getString(getString(R.string.UserId), "0"));
                    params.put(getString(R.string.FbToken), task.getResult().getToken());
                    Call<UserItem> call =
                            ApiClient.getClient().create(ApiInterface.class).getAuthenticate(params);
                    call.enqueue(new Callback<UserItem>() {
                        @Override
                        public void onResponse(Call<UserItem> call, retrofit2.Response<UserItem> response) {
                            //if (response.code() == 200)
                               /* try {
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
                                }*/
                        }

                        @Override
                        public void onFailure(Call<UserItem> call, Throwable t) {
                            // if (!preferences.getString(getString(R.string.UserId), "00000").equals("00000"))
                            //IsAuthenticate();
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
                        editor.putInt(getString(R.string.Feepayable), result.getInt(getString(R.string.Feepayable)));
                        editor.commit();
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

    private void showBottomsheetCreateEvent() {

        if (createEventDialog == null)
            createEventDialog = new CreateEventDialog();
        createEventDialog.show(getSupportFragmentManager(),
                "CreateEventDialog");
        //FragmentStack fragmentStack = new FragmentStack(MainActivity.this, getSupportFragmentManager(), R.id.parent_frame);
        //fragmentStack.replace(createEventDialog);
    }

    @Override
    public void showBottomSheetNavigation() {
        View view = getLayoutInflater().inflate(R.layout.dialog_navigation, null);
        BottomSheetDialog dialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetDialog);
        dialog.setContentView(view);

        dialog.findViewById(R.id.tv_recent_visit).setOnClickListener(v -> {
            MyPayeFragment fra = new MyPayeFragment();
            Bundle bnd = new Bundle();
            bnd.putString("FavoriteOrRecent", "History");
            fra.setArguments(bnd);
            fragmentStack.replace(fra);
        });

        BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

                switch (i) {

                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        dialog.show();
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                //setScrim(v);
                dialog.show();
            }
        });
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        dialog.show();
        //ConstraintLayout c1 = view.findViewById(R.id.constraintLayout1);
    }

    @Override
    public void showBottomSheetRating(UserInfoModel result) {
        View view = getLayoutInflater().inflate(R.layout.dialog_rating, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        dialog.setContentView(view);

        int sum = 0, rate1 = 0, rate2 = 0, rate3 = 0, rate4 = 0, rate5 = 0;
        for (int i = 0; i < result.getRating().size(); i++) {
            sum += result.getRating().get(i).getCount();
            if (result.getRating().get(i).getRate() == 1)
                rate1 = result.getRating().get(i).getCount();
            if (result.getRating().get(i).getRate() == 2)
                rate2 = result.getRating().get(i).getCount();
            if (result.getRating().get(i).getRate() == 3)
                rate3 = result.getRating().get(i).getCount();
            if (result.getRating().get(i).getRate() == 4)
                rate4 = result.getRating().get(i).getCount();
            if (result.getRating().get(i).getRate() == 5)
                rate5 = result.getRating().get(i).getCount();
        }

        RatingReviews ratingReviews = view.findViewById(R.id.rating_reviews);
        int colors[] = new int[]{
                Color.parseColor("#0e9d58"),
                Color.parseColor("#bfd047"),
                Color.parseColor("#ffc105"),
                Color.parseColor("#ef7e14"),
                Color.parseColor("#d36259")};

        int raters[] = new int[]{
                (rate5 * 100 / sum),
                (rate4 * 100 / sum),
                (rate3 * 100 / sum),
                (rate2 * 100 / sum),
                (rate1 * 100 / sum)
        };
        ratingReviews.createRatingBars(100, BarLabels.STYPE1, colors, raters);

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        ratingBar.setRating(Float.parseFloat(result.getEventOwner().getRate()));

        TextView tvRate = view.findViewById(R.id.tv_rate);
        tvRate.setText(result.getEventOwner().getRate());

        TextView tvCount = view.findViewById(R.id.tv_count);
        tvCount.setText(sum + "");

        BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

                switch (i) {

                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        dialog.show();
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                //setScrim(v);
                dialog.show();
            }
        });
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        dialog.show();
    }
}
