package ir.payebash.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import ir.payebash.Adapters.NotificationAdapter;
import ir.payebash.Classes.HSH;
import ir.payebash.Interfaces.IWebservice.TitleMain;
import ir.payebash.R;

public class NotificationFragment extends Fragment {

    public static Button btn_location;
    public static NotificationAdapter adapter;
    RecyclerView rv;
    private View rootView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_notification, container, false);
            btn_location = rootView.findViewById(R.id.btn_location);
            btn_location.setOnClickListener(view -> HSH.selectLocation(getActivity(), 1, btn_location));

            adapter = new NotificationAdapter(getActivity());
            rv = rootView.findViewById(R.id.listView);
            rv.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            rv.setLayoutManager(layoutManager);
            rv.setAdapter(adapter);
        }
        ((TitleMain) getContext()).FragName("اعلانیه ها");
        return rootView;
    }
}
