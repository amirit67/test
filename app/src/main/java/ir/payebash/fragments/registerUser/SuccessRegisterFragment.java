package ir.payebash.fragments.registerUser;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import ir.payebash.R;
import ir.payebash.activities.MainActivity;
import ir.payebash.utils.RippleBackground;

public class SuccessRegisterFragment extends Fragment {

    View rootView = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    //Pressed return button - returns to the results menu
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                return true;
            }
            return false;
        });
    }

    private RippleBackground rippleBackground;
    private TextView tvGoToMain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_register_success, container, false);

        rippleBackground = rootView.findViewById(R.id.ripple);
        rippleBackground.startRippleAnimation();

        tvGoToMain = rootView.findViewById(R.id.tv_go);
        tvGoToMain.setOnClickListener(v -> {
            getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        });

        return rootView;
    }

}
