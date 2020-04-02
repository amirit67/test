package ir.payebash.fragments.registerUser;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.payebash.Application;
import ir.payebash.BuildConfig;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.R;
import ir.payebash.activities.BaseActivity;
import ir.payebash.remote.AsynctaskCheckPhoneNumber;
import ir.payebash.classes.HSH;
import ir.payebash.classes.NetworkUtils;
import ir.payebash.models.googlePlus.PlusItem;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 9001;
    public static EditText et_code;
    public static Button bt_register, bt_go, bt_signin;
    public TextView txt_timer;
    FloatingActionButton fab;
    CardView cvAdd;
    JSONObject result = null;
    private EditText et_mobile, et_name, et_family, et_city, et_age;
    private ProgressBar progressBar;
    private FrameLayout frameLayout;
    private ProgressWheel pb;
    private Map<String, String> params = new HashMap<>();
    //private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SweetAlertDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Application.activity = RegisterActivity.this;
        frameLayout = findViewById(R.id.frameLayout);
        fab = findViewById(R.id.fab);
        cvAdd = findViewById(R.id.cv_add);
        et_mobile = findViewById(R.id.et_mobile);
        et_name = findViewById(R.id.et_name);
        et_family = findViewById(R.id.et_family);
        et_age = findViewById(R.id.et_age);
        et_city = findViewById(R.id.et_city);
        et_code = findViewById(R.id.et_code);
        bt_register = findViewById(R.id.bt_register);
        bt_go = findViewById(R.id.bt_go);
        bt_signin = findViewById(R.id.sign_in_button);
        progressBar = findViewById(R.id.progressBar);
        pb = findViewById(R.id.pb);
        txt_timer = findViewById(R.id.txt_timer);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ShowEnterAnimation();
            }
            fab.setOnClickListener(v -> animateRevealClose());
            //et_city.setOnClickListener(view -> HSH.selectLocation(RegisterActivity.this, 0, et_city));
        } catch (Exception e) {
            HSH.showtoast(RegisterActivity.this, e.getMessage() + "a");
        }

        bt_signin.setOnClickListener(this);
        GoogleSignInOptions gso;

        try {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.CI))
                    .requestEmail()
                    .requestProfile()
                    .requestScopes(new Scope(Scopes.PLUS_ME))
                    .requestServerAuthCode(getString(R.string.CI))
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            mGoogleSignInClient.signOut();
            // mAuth = FirebaseAuth.getInstance();
        } catch (Exception e) {
            HSH.showtoast(RegisterActivity.this, e.getMessage() + "b");
        }

        try {
            if (getIntent().getExtras().getString(getString(R.string.Type)).equals("Update")) {
                params.clear();
                params.put(getString(R.string.Type), "Update");
                bt_register.setText("ویرایش");
                bt_signin.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);
                getProfileDetails();
            }
        } catch (Exception e) {
        }

        loading = HSH.onProgress_Dialog(RegisterActivity.this, "لطفا منتظر بمانید");
    }

    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

    private void ShowEnterAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
            getWindow().setSharedElementEnterTransition(transition);
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    cvAdd.setVisibility(View.GONE);
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        transition.removeListener(this);
                    animateRevealShow();
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                }

                @Override
                public void onTransitionPause(Transition transition) {
                }

                @Override
                public void onTransitionResume(Transition transition) {
                }
            });
        }
    }

    public void animateRevealShow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, fab.getWidth() / 2, cvAdd.getHeight());
                mAnimator.setDuration(500);
                mAnimator.setInterpolator(new AccelerateInterpolator());
                mAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        cvAdd.setVisibility(View.VISIBLE);
                        super.onAnimationStart(animation);
                    }
                });
                mAnimator.start();
            } catch (Exception e) {
                cvAdd.setVisibility(View.VISIBLE);
            }
        } else {
            cvAdd.setVisibility(View.VISIBLE);
        }
    }

    public void animateRevealClose() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, cvAdd.getHeight(), fab.getWidth() / 2);
            mAnimator.setDuration(500);
            mAnimator.setInterpolator(new AccelerateInterpolator());
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    cvAdd.setVisibility(View.INVISIBLE);
                    super.onAnimationEnd(animation);
                    fab.setImageResource(R.mipmap.ic_signup);
                    RegisterActivity.super.onBackPressed();
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                }
            });
            mAnimator.start();
        } else {
            cvAdd.setVisibility(View.INVISIBLE);
            fab.setImageResource(R.mipmap.ic_signup);
            RegisterActivity.super.onBackPressed();
        }
    }

    public void clickRegister(View view) {
        String s = et_mobile.getText().toString().trim();
        if (s.equals("") || !s.startsWith("09") || s.length() != 11)
            HSH.showtoast(RegisterActivity.this, "لطفا شماره موبایل معتبر وارد نمایید");
        else if (et_family.getText().toString().trim().equals(""))
            HSH.showtoast(RegisterActivity.this, "لطفا نام خانوادگی خود را وارد نمایید");
        else if (et_name.getText().toString().trim().equals(""))
            HSH.showtoast(RegisterActivity.this, "لطفا نام خود را وارد نمایید");
        else if (et_age.getText().toString().trim().equals(""))
            HSH.showtoast(RegisterActivity.this, "لطفا سن خود را وارد نمایید");
        else if (et_city.getTag().toString().equals("City")) {
            HSH.showtoast(RegisterActivity.this, "لطفا شهر خود را انتخاب نمایید");
            //HSH.selectLocation(RegisterActivity.this, 0, et_city);
        } else {
            if (params.size() == 0 || params.get(getString(R.string.Type)).equals("Register")) {
                final String PN = et_mobile.getText().toString().trim();

                final SweetAlertDialog dialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.NORMAL_TYPE);
                dialog.setTitleText(PN);
                dialog.setContentText("شماره موبایل صحیح است؟");
                dialog.setConfirmText("بله");
                dialog.setCancelText("خیر");
                dialog.setConfirmClickListener(sDialog -> {
                    if (NetworkUtils.getConnectivity(RegisterActivity.this) == true) {
                        dialog.dismiss();
                        params.put(getString(R.string.Type), "Register");
                        UserInfo();
                    } else
                        HSH.showtoast(RegisterActivity.this, "خطا در اتصال به اینترنت");
                });
                dialog.setCancelClickListener(sweetAlertDialog -> dialog.dismissWithAnimation());
                dialog.setCancelable(true);
                HSH.dialog(dialog);
            } else {
                //params.put(getString(R.string.Type), "Update");
                UserInfo();
            }
        }
    }

    private void UserInfo() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            bt_register.setEnabled(false);
            //params.put(getString(R.string.Token), FirebaseInstanceId.getInstance().getToken());
            params.put(getString(R.string.Name2), et_name.getText().toString().trim());
            params.put(getString(R.string.Family), et_family.getText().toString().trim());
            params.put(getString(R.string.mobile), et_mobile.getText().toString().trim());
            params.put(getString(R.string.Age2), et_age.getText().toString().trim());
            params.put(getString(R.string.city), et_city.getTag().toString());
            params.put(getString(R.string.Email), preferences.getString(getString(R.string.Email), ""));
            HSH.editor("MobileTemp", et_mobile.getText().toString().trim());
            if (NetworkUtils.getConnectivity(RegisterActivity.this) != false)
                SendUserInfo("Normal", "");
            else
                HSH.showtoast(RegisterActivity.this, "خطا در اتصال به اینترنت");
        } catch (Exception e) {
        }
    }

    public void clickSubmitRegister(View view) {

        String s = et_code.getText().toString().trim();
        if (s.equals(""))
            HSH.showtoast(RegisterActivity.this, "لطفا کد تایید دریافتی را وارد نمایید");
        else {
            progressBar.setVisibility(View.VISIBLE);
            bt_go.setEnabled(false);
            params.clear();
            params.put(getString(R.string.mobile), et_mobile.getText().toString().trim());
            params.put(getString(R.string.SmsCode), et_code.getText().toString().trim());
            if (NetworkUtils.getConnectivity(RegisterActivity.this) != false) {
                AsynctaskCheckPhoneNumber a = new AsynctaskCheckPhoneNumber(RegisterActivity.this,
                        params,
                        progressBar,
                        bt_go);
                a.getData();
            } else
                HSH.showtoast(RegisterActivity.this, "خطا در اتصال به اینترنت");
        }
    }

    private void SendUserInfo(final String SigninType, final String email) {
        /*params.put(getString(R.string.UserId), preferences.getString(getString(R.string.UserId), ""));
        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).inesrtUser(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.EventModel<ResponseBody> response) {
                loading.dismiss();
                if (SigninType.equals("google") && response.code() == 200) {
                    try {
                        HSH.editor(getString(R.string.Email), email);
                        HSH.editor(getString(R.string.UserId), response.body().string().toString());
                        HSH.editor(getString(R.string.IsAuthenticate), "true");
                    } catch (Exception e) {
                    }
                    HSH.onOpenPage(RegisterActivity.this, MainActivity.class);
                    finish();
                } else
                    try {
                        progressBar.setVisibility(View.GONE);
                        bt_register.setEnabled(true);
                        if (response.code() != 200 && response.code() != 500) {
                            try {
                                HSH.showtoast(RegisterActivity.this, response.body().string());
                            } catch (Exception e) {
                                try {
                                    HSH.showtoast(RegisterActivity.this, response.errorBody().string());
                                } catch (Exception e1) {
                                }
                            }
                        } else if (response.code() == 500) {
                            HSH.showtoast(RegisterActivity.this, "لطفا بعد از چند لحظه مجددا تلاش نمایید");
                        } else if (response.code() == 200) {
                            if (params.size() > 0 && !params.get(getString(R.string.Type)).matches("Update") && preferences.getString(getString(R.string.mobile), "").length() < 10)
                                try {
                                    *//**//*
                                    HSH.editor(getString(R.string.UserId), response.body().string());
                                    HSH.editor(getString(R.string.IsAuthenticate), "true");
                                    HSH.editor(getString(R.string.FullName), params.get("Name") + params.get("Family"));
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } catch (Exception e) {
                                }
                            else {
                                HSH.editor(getString(R.string.FullName), params.get("Name") + " " + params.get("Family"));
                                HSH.showtoast(RegisterActivity.this, "با موفقیت ویرایش گردید");
                                Intent resultData = new Intent();
                                resultData.putExtra("data", "data");
                                setResult(Activity.RESULT_OK, resultData);
                                finish();
                            }
                        }
                    } catch (Exception e) {
                    }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                HSH.showtoast(RegisterActivity.this, "خطا در ارتباط با سرور");
                progressBar.setVisibility(View.GONE);
                bt_register.setEnabled(true);
                loading.dismiss();
            }
        });*/
    }

    private void getProfileDetails() {
        final Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.VoterUserId), preferences.getString(getString(R.string.UserId), "0"));
        Call<ResponseBody> call = ApiClient.getClient().create(ApiInterface.class).getProfileDetails(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse
                    (Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200)
                    try {
                        result = new JSONObject(response.body().string().trim());
                        setMainDrawableColor((RelativeLayout) findViewById(R.id.ll_register));
                        frameLayout.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.txt_title)).setText("ویرایش پروفایل");
                        if (preferences.getString(getString(R.string.mobile), "").length() < 11)
                            et_mobile.setEnabled(true);
                        else
                            et_mobile.setEnabled(false);
                    } catch (Exception e) {
                    }
                else
                    getProfileDetails();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                getProfileDetails();
            }
        });
    }

    private void setMainDrawableColor(RelativeLayout layout) {
        int a = layout.getChildCount();
        for (int i = 0; i < a; i++) {
            if (layout.getChildAt(i) instanceof LinearLayout) {
                View v = ((LinearLayout) (layout.getChildAt(i))).getChildAt(0);
                if (v instanceof TextInputLayout)
                    v = ((TextInputLayout) v).getChildAt(0);
                if (v instanceof FrameLayout) {
                    try {
                        if (!(((FrameLayout) v).getChildAt(0)).getTag().toString().equals("City"))
                            ((EditText) (((FrameLayout) v).getChildAt(0))).setText(result.getString(((FrameLayout) v).getChildAt(0).getTag().toString()).trim());
                        else {
                            try {
                                Cursor cr = Application.database.rawQuery("SELECT * from Citys where id = '" + Integer.parseInt(result.getString(((FrameLayout) v).getChildAt(0).getTag().toString()).trim()) + "'", null);
                                cr.moveToFirst();
                                ((EditText) (((FrameLayout) v).getChildAt(0))).setText(cr.getString(cr.getColumnIndex("StateCity")));
                                (((FrameLayout) v).getChildAt(0)).setTag(cr.getString(cr.getColumnIndex("id")));
                            } catch (Exception e) {
                                ((EditText) (((FrameLayout) v).getChildAt(0))).setText(result.getString(((FrameLayout) v).getChildAt(0).getTag().toString()).trim());
                                ((((FrameLayout) v).getChildAt(0))).setTag(result.getString(((FrameLayout) v).getChildAt(0).getTag().toString()).trim());
                            }
                        }
                    } catch (Exception e) {
                    }
                } else if (v instanceof Spinner) {
                    try {
                        ((Spinner) v).setSelection(Integer.parseInt(result.getString(v.getTag().toString()).trim()));
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    //Firebase_Auth
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 888) {
            if (data != null && resultCode == Activity.RESULT_OK) {
                new GetaccessToken(RegisterActivity.this).execute();
            }
           /* if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }*/
        }
        if (requestCode == RC_SIGN_IN) {
            //https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=
            Bundle extra = data.getExtras();
            new GetaccessToken(RegisterActivity.this).execute();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void getUserInfo(final String token) {
        Call<PlusItem> call =
                ApiClient.getClient4().create(ApiInterface.class).Plus(token);
        call.enqueue(new Callback<PlusItem>() {
            @Override
            public void onResponse(Call<PlusItem> call, retrofit2.Response<PlusItem> response) {
                PlusItem user = response.body();

                HSH.editor(getString(R.string.mobile), params.get(getString(R.string.mobile)));
                HSH.editor(getString(R.string.FullName), user.getdisplayName());
                if (!user.getImage().getUrl().contains("https://"))
                    HSH.editor(getString(R.string.ProfileImage), BuildConfig.BaseUrl + "/Images/Users/" + user.getImage().getUrl() + ".jpg");
                else
                    HSH.editor(getString(R.string.ProfileImage), user.getImage().getUrl());
                HSH.editor(getString(R.string.ServicesIds), "");

                params.put(getString(R.string.Type), "Register");
                //params.put(getString(R.string.Token), FirebaseInstanceId.getInstance().getToken());
                params.put(getString(R.string.Name2), user.getDisplayName().getGivenName());
                params.put(getString(R.string.Family), user.getDisplayName().getFamilyName());
                params.put(getString(R.string.Email), user.getEmails().get(0).getValue());
                params.put(getString(R.string.Images), user.getImage().getUrl());
                if (user.isPlusUser) {
                    String s = "داستان : " + "\n" +
                            "توضیحات : " + user.getTagline() + "\n" +
                            "معرفی : " + user.getAboutMe() + "\n" +
                            "وضعیت تاهل : " + user.getRelationshipStatus() + "\n" +
                            "جنسیت : " + user.getGender() + "\n";
                    if (user.getOrganizations().size() > 0) {
                        s += "تحصیلات : " + "\n" +
                                "نام محل تحصیلی : " + user.getOrganizations().get(0).getName() + "\n" +
                                "رشته تحصیلی : " + user.getOrganizations().get(0).getTitle() + "\n" +
                                "شروع : " + user.getOrganizations().get(0).getStartDate() +
                                "پایان : " + user.getOrganizations().get(0).getEndDate();
                    }
                    if (user.getOrganizations().size() > 1) {
                        s += "سابقه کاری : " + "\n" +
                                "نام : " + user.getOrganizations().get(1).getName() + "\n" +
                                "عنوان : " + user.getOrganizations().get(1).getTitle() + "\n" +
                                "شروع : " + user.getOrganizations().get(1).getStartDate() +
                                "پایان : " + user.getOrganizations().get(1).getEndDate() + "\n";
                    }
                    s += "شغل : " + user.getOccupation() + "\n";
                    if (user.getPlacesLived().size() > 0) {
                        try {
                            s += "محل سکونت : " + user.getPlacesLived().get(0).getValue() + " - " + user.getPlacesLived().get(user.getPlacesLived().size() - 1).getValue();
                        } catch (Exception e) {
                        }
                    }
                    params.put(getString(R.string.Aboutme), s);
                }
                params.put(getString(R.string.mobile), et_mobile.getText().length() == 11 ? et_mobile.getText().toString().trim() : "");
                if (user.getPlacesLived().size() > 0)
                    params.put(getString(R.string.city), user.getPlacesLived().get(0).getValue() + " - " + user.getPlacesLived().get(user.getPlacesLived().size() - 1).getValue());
                else
                    params.put(getString(R.string.city), "");
                //getAge(token);
                HSH.editor("MobileTemp", et_mobile.getText().toString().trim());
                if (NetworkUtils.getConnectivity(RegisterActivity.this) != false)
                    SendUserInfo("google", user.getEmails().get(0).getValue());
                else
                    HSH.showtoast(RegisterActivity.this, "خطا در اتصال به اینترنت");
            }

            @Override
            public void onFailure(Call<PlusItem> call, Throwable t) {
            }
        });
    }

    private void getAge(String token) {
        Call<PlusItem> call =
                ApiClient.getClient5().create(ApiInterface.class).birthDay(token);
        call.enqueue(new Callback<PlusItem>() {
            @Override
            public void onResponse(Call<PlusItem> call, retrofit2.Response<PlusItem> response) {
                PlusItem user = response.body();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String birthDate =
                        user.getBirthdays().get(0).getDate().getDay() + "/" +
                                user.getBirthdays().get(0).getDate().getMonth() + "/" +
                                user.getBirthdays().get(0).getDate().getYear();
                try {
                    Date date1 = sdf.parse(birthDate);
                    Date date2 = sdf.parse(sdf.format(new Date()));
                    int age = (int) (((date2.getTime() - date1.getTime()) / (3600 * 24 * 365)) / 1000);
                    params.put(getString(R.string.Age2), String.valueOf(age));
                } catch (Exception e) {
                }
                HSH.editor("MobileTemp", et_mobile.getText().toString().trim());
                if (NetworkUtils.getConnectivity(RegisterActivity.this) != false)
                    SendUserInfo("google", user.getEmails().get(0).getValue());
                else
                    HSH.showtoast(RegisterActivity.this, "خطا در اتصال به اینترنت");
            }

            @Override
            public void onFailure(Call<PlusItem> call, Throwable t) {
            }
        });
    }

    public class GetaccessToken extends AsyncTask<Void, Void, String> {

        private final Context context;

        public GetaccessToken(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                try {
                    //https://www.googleapis.com/plus/v1/people/me?access_token=accessToken
                    String scope = "oauth2:" + "https://www.googleapis.com/auth/plus.login " +
                            "https://www.googleapis.com/auth/plus.me ";
                            /*"https://www.googleapis.com/auth/userinfo.email " +
                            "https://www.googleapis.com/auth/userinfo.profile " +
                            "https://www.googleapis.com/auth/user.birthday.read " +
                            "https://www.googleapis.com/auth/user.phonenumbers.read"*/
                    ;
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
                    String accessToken = GoogleAuthUtil.getToken(context, account.getAccount(), scope, new Bundle());
                    return accessToken;
                } catch (UserRecoverableAuthException e) {
                    startActivityForResult(e.getIntent(), 888);
                    e.printStackTrace();
                } catch (GoogleAuthException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (null != result) {
                loading.show();
                getUserInfo(result);
            } else
                HSH.showtoast(RegisterActivity.this, "مجددا تلاش نمایید");
        }
    }
}
