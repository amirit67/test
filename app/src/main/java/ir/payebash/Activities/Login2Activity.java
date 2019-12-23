package ir.payebash.Activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.payebash.Application;
import ir.payebash.BuildConfig;
import ir.payebash.Classes.HSH;
import ir.payebash.Classes.NetworkUtils;
import ir.payebash.Fragments.ForgotPasswordFragment;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Models.PlusItem;
import ir.payebash.Models.UserItem;
import ir.payebash.R;
import ir.payebash.asynktask.AsynctaskCheckPhoneNumber;
import ir.payebash.chat.AsyncLoginTask;
import ir.payebash.helpers.PrefsManager;
import ir.payebash.helpers.Utils;
import microsoft.aspnet.signalr.client.http.CookieCredentials;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


public class Login2Activity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private static final int RC_SIGN_IN = 9001;
    private String loginUrl = BuildConfig.BaseUrl + "/Account/Login";
    public static EditText etUsername, etPassword;
    public TextView txtRegister, btLogin, txtForgotPassword;
    private ProgressBar progressBar;
    private Map<String, String> params = new HashMap<>();
    //private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        progressBar = findViewById(R.id.progressBar);
        txtRegister = findViewById(R.id.txt_register);
        txtForgotPassword = findViewById(R.id.txt_forgot_password);
        btLogin = findViewById(R.id.bt_login);

        etUsername.addTextChangedListener(this);
        etPassword.addTextChangedListener(this);
        btLogin.setOnClickListener(this::onClick);
        txtRegister.setOnClickListener(this::onClick);
        txtForgotPassword.setOnClickListener(this::onClick);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login3);
        // Load Auth cookies/credentials

        CookieCredentials loginCredentials = PrefsManager.loadAuthCookie(this);
        if (loginCredentials != null) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            Application.activity = Login2Activity.this;
            initViews();
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
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 888) {
            if (data != null && resultCode == Activity.RESULT_OK) {
                new GetaccessToken(Login2Activity.this).execute();
            }
           /* if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }*/
        }
        if (requestCode == RC_SIGN_IN) {
            //https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=
            new GetaccessToken(Login2Activity.this).execute();
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
        } else if (i == R.id.bt_login) {
            if (Utils.isOnline(this)) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                new AsyncLoginTask(this, progressBar, btLogin).execute(loginUrl, username, password);

            } else {
                Toast.makeText(this, "خطا در اتصال به اینترنت!", Toast.LENGTH_SHORT).show();
            }
        } else if (i == R.id.txt_forgot_password) {
            HSH.openFragment(this, new ForgotPasswordFragment());
        } else if (i == R.id.txt_register) {
            HSH.openFragment(this, new Register2Activity());
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
                    HSH.editor(getString(R.string.ProfileImage), getString(R.string.url) + "Images/Users/" + user.getImage().getUrl() + ".jpg");
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
                if (NetworkUtils.getConnectivity(Login2Activity.this) != false)
                    GetUserVerificationViaGoogle(user.getEmails().get(0).getValue());
                else
                    HSH.showtoast(Login2Activity.this, "خطا در اتصال به اینترنت");
            }

            @Override
            public void onFailure(Call<PlusItem> call, Throwable t) {
            }
        });
    }

    private void GetUserVerificationViaGoogle(final String gmail) {
        Call<UserItem> call =
                ApiClient.getClient().create(ApiInterface.class).GetUserVerificationViaGoogle(params);
        call.enqueue(new Callback<UserItem>() {
            @Override
            public void onResponse(Call<UserItem> call, retrofit2.Response<UserItem> response) {
                if (response.code() == 200) {
                    try {
                        UserItem user = response.body();
                        String UserId = user.getUserId();
                        if (UserId.equals("0")) {
                            String Message = user.getMessage();
                            HSH.showtoast(Login2Activity.this, Message);
                        } else {
                            try {
                                HSH.editor(getString(R.string.Email), gmail);
                                HSH.editor(getString(R.string.UserId), user.getUserId());
                                HSH.editor(getString(R.string.mobile), params.get(getString(R.string.mobile)));
                                HSH.editor(getString(R.string.FullName), user.getFullName());
                                if (!user.getProfileImage().contains("https://"))
                                    HSH.editor(getString(R.string.ProfileImage), getString(R.string.url) + "Images/Users/" + user.getProfileImage() + ".jpg");
                                else
                                    HSH.editor(getString(R.string.ProfileImage), user.getProfileImage());
                                HSH.editor(getString(R.string.ServicesIds), user.getServicesIds().trim());
                                try {
                                    HSH.editor(getString(R.string.IsAuthenticate), "true");
                                    Intent intent = new Intent(Login2Activity.this, MainActivity.class);
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
                //loading.dismiss();
                if (t.toString().contains("java.net.SocketTimeoutException")) {
                    //  loading.show();
                    GetUserVerificationViaGoogle(gmail);
                }
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
                //loading.show();
                getUserInfo(result);
            } else
                HSH.showtoast(Login2Activity.this, "مجددا تلاش نمایید");
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        if (etUsername.getText().length() > 5 &&
                etPassword.getText().length() > 5) {
            btLogin.setEnabled(true);
            btLogin.setBackground(getResources().getDrawable(R.drawable.rounded_corners_solid_black));
        } else {
            btLogin.setEnabled(false);
            btLogin.setBackground(getResources().getDrawable(R.drawable.rounded_corners_solid_gray));

        }
    }
}
