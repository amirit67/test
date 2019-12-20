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
import ir.payebash.Classes.HSH;
import ir.payebash.Classes.NetworkUtils;
import ir.payebash.Interfaces.ApiClient;
import ir.payebash.Interfaces.ApiInterface;
import ir.payebash.Models.PlusItem;
import ir.payebash.Models.UserItem;
import ir.payebash.R;
import ir.payebash.asynktask.AsynctaskCheckPhoneNumber;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


public class Register2Activity extends BaseActivity implements View.OnClickListener, TextWatcher {


    private EditText etFullname, etUsername, etEmail, etPassword;
    public TextView btRegister;
    private ProgressBar progressBar;
    private UserItem params;

    private void initViews() {
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
            progressBar.setVisibility(View.VISIBLE);
            btRegister.setVisibility(View.GONE);
            //params.put(getString(R.string.Token), FirebaseInstanceId.getInstance().getToken());
            params.setUserName(etUsername.getText().toString().trim());
            params.setEmail(etEmail.getText().toString().trim());
            params.setFullName(etFullname.getText().toString().trim());
            params.setUserName(etUsername.getText().toString().trim());
            if (NetworkUtils.getConnectivity(Register2Activity.this) != false)
                RegisterUser();
            else
                HSH.showtoast(Register2Activity.this, "خطا در اتصال به اینترنت");
        } catch (Exception e) {
        }
    }

    private void RegisterUser() {
        Call<ResponseBody> call =
                ApiClient.getClient().create(ApiInterface.class).inesrtUser(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    progressBar.setVisibility(View.GONE);
                    btRegister.setVisibility(View.VISIBLE);
                    if (response.code() != 200 && response.code() != 500) {
                        try {
                            HSH.showtoast(Register2Activity.this, response.body().string());
                        } catch (Exception e) {
                            try {
                                HSH.showtoast(Register2Activity.this, response.errorBody().string());
                            } catch (Exception e1) {
                            }
                        }
                    } else if (response.code() == 500) {
                        HSH.showtoast(Register2Activity.this, "لطفا بعد از چند لحظه مجددا تلاش نمایید");
                    } else if (response.code() == 200) {
                        try {
                            /**/
                            HSH.editor(getString(R.string.UserId), response.body().string());
                            HSH.editor(getString(R.string.IsAuthenticate), "true");
                            HSH.editor(getString(R.string.FullName), params.getFullName());
                            Intent intent = new Intent(Register2Activity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                        }

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

        if (etUsername.getText().length() > 5 &&
                etUsername.getText().length() > 4 &&
                (!etEmail.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) &&
                etPassword.getText().length() > 7) {
            btRegister.setEnabled(true);
            btRegister.setBackground(getResources().getDrawable(R.drawable.rounded_corners_solid_blue));
        } else {
            btRegister.setEnabled(false);
            btRegister.setBackground(getResources().getDrawable(R.drawable.rounded_corners_solid_gray));

        }
    }
}
