package ir.payebash.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.payebash.Application;
import ir.payebash.BuildConfig;
import ir.payebash.classes.HSH;
import ir.payebash.classes.NetworkUtils;
import ir.payebash.fragments.registerUser.RegisterActivity;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.models.googlePlus.PlusItem;
import ir.payebash.models.UserItem;
import ir.payebash.R;
import ir.payebash.remote.AsynctaskCheckPhoneNumber;
import retrofit2.Call;
import retrofit2.Callback;


public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 9001;
    public static EditText editTextMobile, editTextCode;
    public TextView txt_timer, txt_register;
    private FloatingActionButton fab;
    private TextInputLayout input_layout_code;
    private ProgressBar progressBar;
    private Map<String, String> params = new HashMap<>();
    private Button bt_go;
    //private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SweetAlertDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Application.activity = LoginActivity.this;
        fab = findViewById(R.id.fab);
        input_layout_code = findViewById(R.id.input_layout_code);
        editTextMobile = findViewById(R.id.et_mobile);
        editTextCode = findViewById(R.id.et_code);
        progressBar = findViewById(R.id.progressBar);
        txt_timer = findViewById(R.id.txt_timer);
        txt_register = findViewById(R.id.txt_register);
        bt_go = findViewById(R.id.bt_go);

        if (preferences.getString("MobileTemp", "").length() == 11
                && null == getIntent().getExtras()) {
            progressBar.setVisibility(View.GONE);
            bt_go.setEnabled(true);
            editTextMobile.setEnabled(false);
            editTextMobile.setText(preferences.getString("MobileTemp", ""));
            txt_timer.setVisibility(View.VISIBLE);
            input_layout_code.setVisibility(View.VISIBLE);
            editTextCode.setEnabled(true);
            editTextCode.requestFocus();
            bt_go.setText("تایید");
            txt_timer.setText("ارسال مجدد");
            txt_timer.setOnClickListener(v -> {
                HSH.editor("MobileTemp", "");
                finish();
                startActivity(getIntent());
            });
            bt_go.setOnClickListener(v -> CommitCode());
        }

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.CI))
                .requestEmail()
                .requestProfile()
                .requestScopes(new Scope(Scopes.PLUS_ME), new Scope(Scopes.PLUS_LOGIN))
                .requestServerAuthCode(getString(R.string.CI))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();
        //mAuth = FirebaseAuth.getInstance();

        txt_register.setPaintFlags(txt_register.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txt_register.setOnClickListener(v -> clickRegisterLayout());
        fab.setOnClickListener(v -> clickRegisterLayout());

        editTextMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    bt_go.setEnabled(false);
                    bt_go.setBackgroundResource(R.drawable.bt_shape);
                } else if (s.length() == 11) {
                    bt_go.setEnabled(true);
                    bt_go.setBackgroundResource(R.drawable.press_button_background_green);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        loading = HSH.onProgress_Dialog(LoginActivity.this, "لطفا منتظر بمانید");

        if (null != getIntent().getExtras()) {

            ((TextView) findViewById(R.id.title)).setText("تایید شماره موبایل");
            txt_register.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            bt_go.setEnabled(false);
            editTextMobile.setEnabled(false);
            editTextMobile.setText(preferences.getString("MobileTemp", ""));

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        String token = task.getResult().getToken();

                        HSH.editor("MobileTemp", editTextMobile.getText().toString().trim());
                        params.put(getString(R.string.UserId), preferences.getString(getString(R.string.UserId), ""));
                        params.put(getString(R.string.mobile), editTextMobile.getText().toString().trim());
                        params.put(getString(R.string.Token), token);
                        params.put("Type", "Login");
                        SendPhoneNumber(bt_go);
                    });
        }

    }

    private void clickRegisterLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, fab, fab.getTransitionName());
            startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class), 123, options.toBundle());
        } else {
            startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class), 123);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED, null);
        finish();
    }

    public void clickLogin(final View view) {
        String mobile = editTextMobile.getText().toString();
        if (mobile.equals("") || !mobile.startsWith("09") || mobile.length() != 11)
            HSH.showtoast(LoginActivity.this, "لطفا شماره موبایل معتبر وارد نمایید");
        else {
            final String PN = ((EditText) findViewById(R.id.et_mobile)).getText().toString().trim();

            final SweetAlertDialog dialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.NORMAL_TYPE);
            dialog.setTitleText(PN);
            dialog.setContentText("شماره موبایل صحیح است؟");
            dialog.setConfirmText("بله");
            dialog.setCancelText("خیر");
            dialog.setConfirmClickListener(sDialog -> {
                if (NetworkUtils.getConnectivity(LoginActivity.this) == true) {
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    //Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();

                                editTextMobile.setEnabled(false);
                                progressBar.setVisibility(View.VISIBLE);
                                view.setEnabled(false);
                                HSH.editor("MobileTemp", editTextMobile.getText().toString().trim());
                                params.put(getString(R.string.UserId), preferences.getString(getString(R.string.UserId), ""));
                                params.put(getString(R.string.mobile), editTextMobile.getText().toString().trim());
                                params.put(getString(R.string.Token), token);
                                params.put("Type", "Login");
                                SendPhoneNumber(view);
                            });
                    dialog.dismiss();
                } else
                    HSH.showtoast(LoginActivity.this, "خطا در اتصال به اینترنت");
            });
            dialog.setCancelClickListener(sweetAlertDialog -> dialog.dismissWithAnimation());
            dialog.setCancelable(true);
            HSH.dialog(dialog);
        }
    }

    private void SendPhoneNumber(final View v) {
       /* Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).inesrtUser(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.EventModel<ResponseBody> response) {
                progressBar.setVisibility(View.GONE);
                v.setEnabled(true);
                if (response.code() != 200 && response.code() != 500) {
                    try {
                        HSH.showtoast(LoginActivity.this, response.body().string());
                    } catch (Exception e) {
                        try {
                            String s = response.errorBody().string();
                            HSH.showtoast(LoginActivity.this, s);
                            if (s.contains("دقیقه")) {
                                progressBar.setVisibility(View.GONE);
                                bt_go.setEnabled(true);
                                editTextMobile.setEnabled(false);
                                txt_timer.setVisibility(View.GONE);
                                input_layout_code.setVisibility(View.VISIBLE);
                                editTextCode.setEnabled(true);
                                editTextCode.requestFocus();
                                bt_go.setText("تایید");
                                bt_go.setOnClickListener(v12 -> CommitCode());
                            }
                        } catch (Exception e1) {
                        }
                    }
                } else if (response.code() == 500)
                    HSH.showtoast(LoginActivity.this, "خطا...مجددا تلاش نمایید");
                else if (response.code() == 200) {
                    HSH.showtoast(LoginActivity.this, "منتظر دریافت کد تایید بمانید");
                    editTextMobile.setEnabled(false);
                    txt_timer.setVisibility(View.VISIBLE);
                    txt_timer.setEnabled(false);
                    final String FORMAT = "%02d:%02d";
                    new CountDownTimer(120000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            txt_timer.setText("زمان باقی مانده (" + String.format(FORMAT,
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + ")");
                        }

                        public void onFinish() {
                            txt_timer.setText("ارسال مجدد");
                            txt_timer.setEnabled(true);
                            txt_timer.setVisibility(View.VISIBLE);
                            txt_timer.setOnClickListener(v1 -> SendPhoneNumber(v));
                        }
                    }.start();


                    try {
                        HSH.display(LoginActivity.this, input_layout_code);
                        editTextCode.setEnabled(true);
                        ((Button) v).setText("تایید");
                        v.setOnClickListener(v13 -> CommitCode());

                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                try {
                    if (t.toString().toLowerCase().contains("time") || t.toString().toLowerCase().contains("physical connection"))
                        SendPhoneNumber(v);
                    else {
                        HSH.showtoast(LoginActivity.this, "خطا.مجددا تلاش نمایید");
                        progressBar.setVisibility(View.GONE);
                        v.setEnabled(true);
                    }
                } catch (Exception e) {
                    progressBar.setVisibility(View.GONE);
                    v.setEnabled(true);
                }
            }
        });*/
    }

    private void CommitCode() {
        String s = editTextCode.getText().toString().trim();
        if (s.equals(""))
            HSH.showtoast(LoginActivity.this, "لطفا کد تایید دریافتی را وارد نمایید");
        else if (NetworkUtils.getConnectivity(LoginActivity.this) != false) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            progressBar.setVisibility(View.VISIBLE);
            bt_go.setEnabled(false);
            Map<String, String> params = new HashMap<>();
            params.put(getString(R.string.mobile), editTextMobile.getText().toString().trim());
            params.put(getString(R.string.SmsCode), editTextCode.getText().toString().trim());
            AsynctaskCheckPhoneNumber a = new AsynctaskCheckPhoneNumber(LoginActivity.this,
                    params,
                    progressBar,
                    bt_go);
            a.getData();
        } else
            HSH.showtoast(LoginActivity.this, "خطا در اتصال به اینترنت");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 888) {
            if (data != null && resultCode == Activity.RESULT_OK) {
                new GetaccessToken(LoginActivity.this).execute();
            }
           /* if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }*/
        }
        if (requestCode == RC_SIGN_IN) {
            //https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=
            new GetaccessToken(LoginActivity.this).execute();
        }
       /* if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //firebaseAuthWithGoogle(account);
                getUserInfo(account.getIdToken());
                //new GetaccessToken(LoginActivity.this).execute();
            } catch (Exception e) {
                HSH.showtoast(LoginActivity.this, "خطا!!!");
            }
        }*/
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
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

                params.put(getString(R.string.UserId), preferences.getString(getString(R.string.UserId), ""));
                //params.put(getString(R.string.Token), FirebaseInstanceId.getInstance().getToken());
                params.put(getString(R.string.Type), "Login");
                params.put(getString(R.string.GivenName), user.getDisplayName().getGivenName());
                params.put(getString(R.string.familyName), user.getDisplayName().getFamilyName());
                params.put(getString(R.string.Email), user.getEmails().get(0).getValue());
                params.put(getString(R.string.Images), user.getImage().getUrl());
                if (user.isPlusUser) {
                    String s = "داستان زندگی : " + "\n" +
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
                if (user.getPlacesLived().size() > 0)
                    params.put(getString(R.string.city), user.getPlacesLived().get(0).getValue() + " - " + user.getPlacesLived().get(user.getPlacesLived().size() - 1).getValue());
                else
                    params.put(getString(R.string.city), "");
                //getAge(token);
                if (NetworkUtils.getConnectivity(LoginActivity.this) != false)
                    GetUserVerificationViaGoogle(user.getEmails().get(0).getValue());
                else
                    HSH.showtoast(LoginActivity.this, "خطا در اتصال به اینترنت");
            }

            @Override
            public void onFailure(Call<PlusItem> call, Throwable t) {
                loading.dismiss();
            }
        });
    }

    private void GetUserVerificationViaGoogle(final String gmail) {
        Call<UserItem> call =
                ApiClient.getClient().create(ApiInterface.class).GetUserVerificationViaGoogle(params);
        call.enqueue(new Callback<UserItem>() {
            @Override
            public void onResponse(Call<UserItem> call, retrofit2.Response<UserItem> response) {
                loading.dismiss();
                if (response.code() == 200) {
                    try {
                        UserItem user = response.body();
                        String UserId = user.getUserId();
                        if (UserId.equals("0")) {
                            String Message = user.getMessage();
                            HSH.showtoast(LoginActivity.this, Message);
                        } else {
                            try {
                                HSH.editor(getString(R.string.Email), gmail);
                                HSH.editor(getString(R.string.UserId), user.getUserId());
                                HSH.editor(getString(R.string.mobile), params.get(getString(R.string.mobile)));
                                HSH.editor(getString(R.string.FullName), user.getFullName());
                                if (!user.getProfileImage().contains("https://"))
                                    HSH.editor(getString(R.string.ProfileImage), BuildConfig.BaseUrl + "/Images/Users/" + user.getProfileImage() + ".jpg");
                                else
                                    HSH.editor(getString(R.string.ProfileImage), user.getProfileImage());
                                HSH.editor(getString(R.string.ServicesIds), user.getServicesIds().trim());
                                try {
                                    HSH.editor(getString(R.string.IsAuthenticate), "true");
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } catch (Exception e) {
                                }
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<UserItem> call, Throwable t) {
                loading.dismiss();
                if (t.toString().contains("java.net.SocketTimeoutException")) {
                    loading.show();
                    GetUserVerificationViaGoogle(gmail);
                }
            }
        });
    }

    /*private void oauth2(String token) {
        loading = HSH.onProgress_Dialog(LoginActivity.this, "لطفا منتظر بمانید");
        loading.show();
        Call<GoogleOuathItem> call =
                ApiClient.getClient4().create(ApiInterface.class).OAuth2(token);
        call.enqueue(new Callback<GoogleOuathItem>() {
            @Override
            public void onResponse(Call<GoogleOuathItem> call, retrofit2.EventModel<GoogleOuathItem> response) {
                if (response.code() == 200) {
                    GoogleOuathItem user = response.body();
                    if (user.getEmail_verified()) {
                        params.put(getString(R.string.UserId), Application.preferences.getString(getString(R.string.UserId), ""));
                        params.put(getString(R.string.Token), FirebaseInstanceId.getInstance().getToken());
                        params.put(getString(R.string.Type), "Login");
                        params.put(getString(R.string.GivenName), user.getGiven_name());
                        params.put(getString(R.string.familyName), user.getFamily_name());
                        params.put(getString(R.string.Images), user.getPicture());
                        params.put(getString(R.string.Email), user.getEmail());
                        GetUserVerificationViaGoogle(user.getEmail());
                    }
                }
            }

            @Override
            public void onFailure(Call<GoogleOuathItem> call, Throwable t) {
                loading.dismiss();
            }
        });
    }*/

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
                HSH.showtoast(LoginActivity.this, "مجددا تلاش نمایید");
        }
    }
}
