package ir.payebash.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

import ir.payebash.R;


public class FilterFragment extends Fragment {

    private boolean isLoading;
    private Map<String, String> params = new HashMap<>();
    private Activity ac;
    private EditText etSearch;
    private Button btnCategories, btnLocation;
    private View rootView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null)
            rootView = inflater.inflate(R.layout.dialog_filter_posts2, container, false);

        DeclareElements();
        ac = getActivity();

        return rootView;
    }

    public void DeclareElements() {

    }

    private void TransToSearchFrag() {

    }
}
