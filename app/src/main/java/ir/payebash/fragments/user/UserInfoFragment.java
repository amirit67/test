package ir.payebash.fragments.user;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ir.payebash.Interfaces.IWebservice;
import ir.payebash.R;
import ir.payebash.adapters.userInfo.userInfoAdapter;
import ir.payebash.asynktask.user.AsynctaskGetUserInfo;
import ir.payebash.models.user.UserInfoModel;

public class UserInfoFragment extends Fragment {


    userInfoAdapter adapter;
    private RecyclerView rv;
    private LinearLayoutManager layoutManager;
    View rootView = null;

    public void DeclareElements() {
        rv = rootView.findViewById(R.id.rv_user_info);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            if (rootView != null) {
                ViewGroup parent = (ViewGroup) rootView.getParent();
                if (parent != null) {
                    parent.removeView(rootView);
                }
            }
        } catch (Exception e) {
        }
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_user_info, container, false);
            DeclareElements();
            getUserInfo();
        }
        return rootView;
    }


    private void getUserInfo() {

        IWebservice.IUserInfo del = new IWebservice.IUserInfo() {
            @Override
            public void getResult(UserInfoModel userInfoModel) throws Exception {

                ((IWebservice.IUserInfo) getParentFragment()).getResult(userInfoModel);

                adapter = new userInfoAdapter(getActivity(), userInfoModel);
                rv.setAdapter(adapter);
            }

            @Override
            public void getError(String error) throws Exception {

            }
        };
        new AsynctaskGetUserInfo(del).getData();
    }
}
