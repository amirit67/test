package ir.payebash.fragments.registerUser;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import ir.payebash.Interfaces.IWebservice;
import ir.payebash.R;
import ir.payebash.activities.MainActivity;
import ir.payebash.remote.AsynctaskRegister;
import ir.payebash.classes.HSH;
import ir.payebash.classes.NetworkUtils;
import ir.payebash.models.UserItem;
import ir.payebash.models.login.LoginModel;


public class Register2Activity extends Fragment implements View.OnClickListener, TextWatcher {


    private EditText etFullname, etUsername, etEmail, etPassword;
    public TextView btRegister, txtError;
    ImageView imgBack, imgName, imgUsername, imgEmail, imgPassword;
    private ProgressBar progressBar;
    private UserItem params;
    private View rootView = null;

    private void initViews() {
        etFullname = rootView.findViewById(R.id.et_name);
        etEmail = rootView.findViewById(R.id.et_email);
        etUsername = rootView.findViewById(R.id.et_username);
        etPassword = rootView.findViewById(R.id.et_password);
        progressBar = rootView.findViewById(R.id.progressBar);
        btRegister = rootView.findViewById(R.id.bt_register);
        txtError = rootView.findViewById(R.id.txt_error);
        imgBack = rootView.findViewById(R.id.img_back);
        imgName = rootView.findViewById(R.id.img_name);
        imgUsername = rootView.findViewById(R.id.img_username);
        imgEmail = rootView.findViewById(R.id.img_email);
        imgPassword = rootView.findViewById(R.id.img_password);

        etUsername.addTextChangedListener(this);
        etEmail.addTextChangedListener(this);
        etPassword.addTextChangedListener(this);
        etFullname.addTextChangedListener(this);

        /*etEmail.setOnFocusChangeListener(this);
        etPassword.setOnFocusChangeListener(this);
        etFullname.setOnFocusChangeListener(this);*/

        btRegister.setOnClickListener(this::onClick);
        rootView.findViewById(R.id.sign_in_button).setOnClickListener(this);
        imgBack.setOnClickListener(this::onClick);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_register2, container, false);
            initViews();
        }

        return rootView;
    }

    //Firebase_Auth
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_register) {
            if (etFullname.getText().length() < 7) {
                txtError.setText("نام کامل حداقل 7 حرف می باشد");
                imgName.setBackgroundResource(R.drawable.ic_error);
            } else if (etUsername.getText().length() < 5) {
                txtError.setText("نام کاربری حداقل 5 حرف به زبان انگلیسی می باشد");
                imgUsername.setBackgroundResource(R.drawable.ic_error);
            } else if ((!etEmail.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                txtError.setText("فرمت ایمیل صحیح نمی باشد");
                imgEmail.setBackgroundResource(R.drawable.ic_error);
            } else if (etPassword.getText().length() < 8) {
                txtError.setText("رمز عبور حداقل 8 حرف می باشد");
                imgPassword.setVisibility(View.GONE);
                imgPassword.setBackgroundResource(R.drawable.ic_error);
            } else
                UserInfo();
        } else if (i == R.id.img_back)
            getFragmentManager().popBackStack();
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
            params.setPassword(HSH.ecr(etPassword.getText().toString().trim()));
            if (NetworkUtils.getConnectivity(getActivity()) != false)
                RegisterUser();
            else
                HSH.showtoast(getActivity(), "خطا در اتصال به اینترنت");
        } catch (Exception e) {
        }
    }

    private void RegisterUser() {

        IWebservice.ILogin del = new IWebservice.ILogin() {
            @Override
            public void getResult(LoginModel user) throws Exception {
                progressBar.setVisibility(View.GONE);
                btRegister.setVisibility(View.VISIBLE);
                if (user.getStatusCode() == 200) {
                    HSH.editor(getString(R.string.FullName), user.getFullName());
                    HSH.editor(getString(R.string.UserName), user.getUsername());
                    HSH.editor(getString(R.string.UserId), user.getUserId());
                    HSH.editor(getString(R.string.ProfileImage), user.getProfileImage());
                    HSH.editor(getString(R.string.Password), HSH.ecr(etPassword.getText().toString()));
                    //HSH.editor("IsAuthenticate", "true");
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                } else
                    txtError.setText(user.getMessage());
            }

            @Override
            public void getError(String error) throws Exception {
                txtError.setText(error);
                progressBar.setVisibility(View.GONE);
                btRegister.setVisibility(View.VISIBLE);
            }
        };
        new AsynctaskRegister(getActivity(), params, del).getData();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        if (etFullname.getText().length() > 6)
            imgName.setBackgroundResource(R.drawable.ic_check_circle);
        else
            imgName.setBackgroundResource(0);

        if (etUsername.getText().length() > 4)
            imgUsername.setBackgroundResource(R.drawable.ic_check_circle);
        else
            imgUsername.setBackgroundResource(0);

        if (etEmail.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))
            imgEmail.setBackgroundResource(R.drawable.ic_check_circle);
        else
            imgEmail.setBackgroundResource(0);

        if (etPassword.getText().length() > 7) {
            imgPassword.setVisibility(View.VISIBLE);
            imgPassword.setBackgroundResource(R.drawable.ic_check_circle);
        } else {
            imgPassword.setVisibility(View.GONE);
            imgPassword.setBackgroundResource(0);
        }

        txtError.setText("");
        if (etFullname.getText().length() > 0 &&
                etUsername.getText().length() > 0 &&
                etEmail.getText().length() > 0 &&
                etPassword.getText().length() > 0) {
            btRegister.setEnabled(true);
            btRegister.setBackground(getResources().getDrawable(R.drawable.rounded_corners_solid_black));
        } else {
            btRegister.setEnabled(false);
            btRegister.setBackground(getResources().getDrawable(R.drawable.rounded_corners_solid_gray));

        }
    }
}
