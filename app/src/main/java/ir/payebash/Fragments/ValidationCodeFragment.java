package ir.payebash.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import ir.payebash.Classes.HSH;
import ir.payebash.R;

public class ValidationCodeFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView btSend;

    private View rootView = null;

    public void initViews()
    {
        btSend = rootView.findViewById(R.id.txt_forgot_password);
        btSend.setOnClickListener(this::onClick);

    }

    public static ValidationCodeFragment newInstance(String param1, String param2) {
        ValidationCodeFragment fragment = new ValidationCodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_validation_code, container, false);
        //initViews();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.txt_forgot_password)
        {
            HSH.openFragment(getActivity(), new ValidationCodeFragment());
        }
    }
}
