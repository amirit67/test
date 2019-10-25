package ir.payebash.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import ir.payebash.Adapters.NotificationAdapter;
import ir.payebash.Classes.HSH;
import ir.payebash.Interfaces.IWebservice.TitleMain;
import ir.payebash.R;

public class NotificationFragment extends Fragment {

    public static Button btn_location;
    public static NotificationAdapter adapter;
    ListView lv;
    private View rootView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_notification, container, false);
            btn_location = rootView.findViewById(R.id.btn_location);
            btn_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HSH.selectLocation(getActivity(), 1, btn_location);
                }
            });

            adapter = new NotificationAdapter(getActivity());
            lv = rootView.findViewById(R.id.listView);
            lv.setAdapter(adapter);
        }
        ((TitleMain) getContext()).FragName("اعلانیه ها");
        return rootView;
    }
}
