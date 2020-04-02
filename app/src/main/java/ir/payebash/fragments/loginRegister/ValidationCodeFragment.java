package ir.payebash.fragments.loginRegister;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import ir.payebash.Interfaces.IWebservice;
import ir.payebash.R;
import ir.payebash.remote.forgotPassword.AsynctaskStep2;
import ir.payebash.classes.HSH;
import ir.payebash.models.BaseResponse;
import ir.payebash.models.ForgotPasswordModel;
import ir.payebash.utils.CodePicker;

public class ValidationCodeFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";

    private String mobile;

    private CodePicker codePicker;
    private TextView btSend;

    private View rootView = null;

    public void initViews() {
        btSend = rootView.findViewById(R.id.bt_send);
        btSend.setOnClickListener(this::onClick);
        codePicker = rootView.findViewById(R.id.code_picker);

    }

    public static ValidationCodeFragment newInstance(String param1) {
        ValidationCodeFragment fragment = new ValidationCodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mobile = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_validation_code, container, false);
        initViews();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_send) {
            if (codePicker.getCode().length() == 4)
                sendMobile();
            else
                HSH.showtoast(getActivity(), "کد تایید را وارد نمایید");
        }
    }

    private void sendMobile() {

        ForgotPasswordModel model = new ForgotPasswordModel();
        model.setMobile(mobile);
        model.setSmsCode(codePicker.getCode());

        IWebservice.IForgotPassword del = new IWebservice.IForgotPassword() {
            @Override
            public void getResult(BaseResponse response) throws Exception {
                if (response.getStatusCode() == 200)
                    HSH.openFragment(getActivity(), SubmitPasswordFragment.newInstance(model));
                else
                    HSH.showtoast(getActivity(), response.getMessage());
            }

            @Override
            public void getError(String error) throws Exception {
                HSH.showtoast(getActivity(), error);
            }
        };

        new AsynctaskStep2(model, del).getData();
    }
}
