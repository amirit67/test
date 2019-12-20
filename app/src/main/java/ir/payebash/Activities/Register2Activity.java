package ir.payebash.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ir.payebash.Application;
import ir.payebash.BuildConfig;
import ir.payebash.Classes.HSH;
import ir.payebash.Classes.NetworkUtils;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Models.PlusItem;
import ir.payebash.Models.UserItem;
import ir.payebash.R;
import ir.payebash.asynktask.AsynctaskCheckPhoneNumber;
import ir.payebash.chat.AsyncLoginTask;
import ir.payebash.helpers.PrefsManager;
import ir.payebash.helpers.User;
import microsoft.aspnet.signalr.client.http.CookieCredentials;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


public class Register2Activity extends BaseActivity implements View.OnClickListener, TextWatcher
        , View.OnFocusChangeListener {


    private String registerUrl = BuildConfig.BaseUrl + "/account/register";
    private EditText etFullname, etUsername, etEmail, etPassword;
    private TextInputLayout etName;
    public TextView btRegister;
    private ProgressBar progressBar;
    private UserItem params;
    private DefaultHttpClient httpclient;

    private void initViews() {
        etName = findViewById(R.id.name_parent);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        progressBar = findViewById(R.id.progressBar);
        etFullname = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        btRegister = findViewById(R.id.bt_register);

        etUsername.addTextChangedListener(this);
        etEmail.addTextChangedListener(this);
        etPassword.addTextChangedListener(this);
        etFullname.addTextChangedListener(this);

        etFullname.setOnFocusChangeListener(this);
        /*etEmail.setOnFocusChangeListener(this);
        etPassword.setOnFocusChangeListener(this);
        etFullname.setOnFocusChangeListener(this);*/

        btRegister.setOnClickListener(this::onClick);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        Application.activity = Register2Activity.this;
        initViews();

    }

    //Firebase_Auth
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_register) {
            UserInfo();
        }
    }

    private void UserInfo() {
        try {
            params = new UserItem();
            progressBar.setVisibility(View.VISIBLE);
            btRegister.setVisibility(View.GONE);
            //params.put(getString(R.string.Token), FirebaseInstanceId.getInstance().getToken());
            params.setUserName(etUsername.getText().toString().trim());
            params.setEmail(etEmail.getText().toString().trim());
            params.setFullName(etFullname.getText().toString().trim());
            params.setPassword(etPassword.getText().toString().trim());
            if (NetworkUtils.getConnectivity(Register2Activity.this) != false)
                new AsyncRegisterTask().execute();
            else
                HSH.showtoast(Register2Activity.this, "خطا در اتصال به اینترنت");
        } catch (Exception e) {
        }
    }

    private void RegisterUser(String token) {

        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).inesrtUser(token, params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    if (response.code() == 200) {
                        try {
                            HSH.editor(getString(R.string.UserId), response.body().string());
                            HSH.editor(getString(R.string.IsAuthenticate), "true");
                            HSH.editor(getString(R.string.FullName), params.getFullName());

                            new AsyncLoginTask(Register2Activity.this, progressBar, btRegister)
                                    .execute(registerUrl, params.getUserName(), params.getPassword());
                        } catch (Exception e) {
                        }
                    } else {

                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                HSH.showtoast(Register2Activity.this, "خطا در ارتباط با سرور");
                progressBar.setVisibility(View.GONE);
                btRegister.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        if (etFullname.getText().length() > 5 &&
                etUsername.getText().length() > 4 &&
                (etEmail.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) &&
                etPassword.getText().length() > 7) {
            btRegister.setEnabled(true);
            btRegister.setBackground(getResources().getDrawable(R.drawable.rounded_corners_solid_black));
        } else {
            btRegister.setEnabled(false);
            btRegister.setBackground(getResources().getDrawable(R.drawable.rounded_corners_solid_gray));

        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.et_name && hasFocus) {
            etName.setHint("نام کامل (حداقل 8 حرف)");
            etFullname.setHint("");
        } else if (v.getId() == R.id.et_name){
            etName.setHint("");
            etFullname.setHint("نام کامل");
        }


    }


    public class AsyncRegisterTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            httpclient = new DefaultHttpClient();

            try {
                HttpGet httpPost = new HttpGet(registerUrl);
                HttpResponse response = httpclient.execute(httpPost);
                HttpEntity resEntity = response.getEntity();
                String responseBody = EntityUtils.toString(resEntity);

                // Get __RequestVerificationToken
                String token = GetToken(responseBody);

                return token;
            } catch (Exception e) {
            } finally {
                httpclient.getConnectionManager().shutdown();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String cookieCredentials) {

            params.set__RequestVerificationToken(cookieCredentials);
            RegisterUser(cookieCredentials);
        }
    }


    private String GetToken(String content) {
        int startIndex = content.indexOf("__RequestVerificationToken");
        int endIndex = content.indexOf("/>", startIndex);

        if (startIndex == -1 || endIndex == -1) {
            return null;
        }

        content = content.substring(startIndex, endIndex);

        // Find Token
        Pattern p = Pattern.compile("value=\"(\\S+)\"");
        Matcher m = p.matcher(content);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
}
