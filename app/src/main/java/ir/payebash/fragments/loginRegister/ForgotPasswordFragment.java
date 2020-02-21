package ir.payebash.fragments.loginRegister;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import ir.payebash.Interfaces.IWebservice;
import ir.payebash.R;
import ir.payebash.asynktask.forgotPassword.AsynctaskStep1;
import ir.payebash.classes.HSH;
import ir.payebash.models.BaseResponse;

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    private TextView btSend;
    private EditText etMobile;
    private View rootView = null;

    public void initViews() {
        btSend = rootView.findViewById(R.id.bt_send);
        btSend.setOnClickListener(this::onClick);
        etMobile = rootView.findViewById(R.id.et_mobile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_forgot_password_mobile, container, false);
        initViews();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_send) {
            if (etMobile.getText().toString().length() == 11
                    && etMobile.getText().toString().startsWith("09"))
                sendMobile();
            else
                HSH.showtoast(getActivity(), "شماره موبایل معتبر وارد نمایید");
        }
    }

    private void sendMobile() {
        IWebservice.IForgotPassword del = new IWebservice.IForgotPassword() {
            @Override
            public void getResult(BaseResponse response) throws Exception {
                if (response.getStatusCode() == 200)
                    HSH.openFragment(getActivity(), ValidationCodeFragment.newInstance(etMobile.getText().toString().trim()));
                else
                    HSH.showtoast(getActivity(), response.getMessage());
            }

            @Override
            public void getError(String error) throws Exception {
                HSH.showtoast(getActivity(), error);
            }
        };

        new AsynctaskStep1(etMobile.getText().toString().trim(), del).getData();
    }
}
