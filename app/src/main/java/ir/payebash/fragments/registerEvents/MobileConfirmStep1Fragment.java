package ir.payebash.fragments.registerEvents;


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
import ir.payebash.fragments.loginRegister.ValidationCodeFragment;
import ir.payebash.models.BaseResponse;
import ir.payebash.models.event.detail.EventDetailsModel;

public class MobileConfirmStep1Fragment extends Fragment implements View.OnClickListener {

    private TextView btSend;
    private EditText etMobile;
    private View rootView = null;

    public void initViews() {
        btSend = rootView.findViewById(R.id.bt_send);
        btSend.setOnClickListener(this::onClick);
        etMobile = rootView.findViewById(R.id.et_mobile);
    }

    public static MobileConfirmStep1Fragment newInstance(EventDetailsModel eventDetailsModel) {
        MobileConfirmStep1Fragment fragment = new MobileConfirmStep1Fragment();
      /*  Bundle args = new Bundle();
        args.putString(ARG_PARAM1, categoryId);
        args.putString(ARG_PARAM2, categoryTitle);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_authenticate_mobile, container, false);

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
