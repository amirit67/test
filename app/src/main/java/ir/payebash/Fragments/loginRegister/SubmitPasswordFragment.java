package ir.payebash.Fragments.loginRegister;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import ir.payebash.Classes.HSH;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.models.BaseResponse;
import ir.payebash.models.ForgotPasswordModel;
import ir.payebash.R;
import ir.payebash.asynktask.forgotPassword.AsynctaskStep3;

public class SubmitPasswordFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";

    private ForgotPasswordModel mParam1;

    private TextView btSubmit;
    private EditText etPass1, etPass2;

    private View rootView = null;

    public void initViews() {
        btSubmit = rootView.findViewById(R.id.bt_submit);
        btSubmit.setOnClickListener(this::onClick);
        etPass1 = rootView.findViewById(R.id.et_pass1);
        etPass2 = rootView.findViewById(R.id.et_pass2);
    }

    public static SubmitPasswordFragment newInstance(ForgotPasswordModel param1) {
        SubmitPasswordFragment fragment = new SubmitPasswordFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (ForgotPasswordModel) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_forgot_password_new_password, container, false);
        initViews();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_submit) {
            String pass1 = etPass1.getText().toString().trim();
            String pass2 = etPass2.getText().toString().trim();

            if (pass1.length() < 8 && pass2.length() < 8)
                HSH.showtoast(getActivity(), "حداقل 8 کاراکتر نیاز است");
            else if (!pass1.equals(pass2))
                HSH.showtoast(getActivity(), "رمز عبور با تکرار آن مطابقت ندارد");
            else
                sendMobile();
        }
    }

    private void sendMobile() {

        mParam1.setPassword(HSH.ecr(etPass1.getText().toString().trim()));

        IWebservice.IForgotPassword del = new IWebservice.IForgotPassword() {
            @Override
            public void getResult(BaseResponse response) throws Exception {
                if (response.getStatusCode() == 200) {
                    getFragmentManager().popBackStack();
                    getFragmentManager().popBackStack();
                    getFragmentManager().popBackStack();
                } else
                    HSH.showtoast(getActivity(), response.getMessage());
            }

            @Override
            public void getError(String error) throws Exception {
                HSH.showtoast(getActivity(), error);
            }
        };

        new AsynctaskStep3(mParam1, del).getData();
    }

}
