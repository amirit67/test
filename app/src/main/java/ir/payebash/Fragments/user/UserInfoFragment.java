package ir.payebash.Fragments.user;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ir.payebash.Adapters.RoomsAdapter;
import ir.payebash.Adapters.userInfo.userInfoAdapter;
import ir.payebash.Classes.ItemDecorationAlbumColumns;
import ir.payebash.R;
import ir.payebash.chat.ChatActivity;
import ir.payebash.modelviewsChat.RoomViewModel;

public class UserInfoFragment extends Fragment {


    userInfoAdapter adapter;
    private RecyclerView rv;
    private LinearLayoutManager layoutManager;
    View rootView = null;

    public void DeclareElements() {
        adapter = new userInfoAdapter(getActivity());
        rv = rootView.findViewById(R.id.rv_user_info);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_user_info, container, false);
            DeclareElements();
        }
        return rootView;
    }
}
