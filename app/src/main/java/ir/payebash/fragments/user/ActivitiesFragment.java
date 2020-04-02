package ir.payebash.fragments.user;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ir.payebash.Application;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.Interfaces.IWebservice.TitleMain;
import ir.payebash.R;
import ir.payebash.adapters.userInfo.userInfoAdapter;
import ir.payebash.databinding.FragmentActivitiesBinding;
import ir.payebash.models.user.UserInfoModel;
import ir.payebash.remote.user.AsynctaskGetUserInfo;

public class ActivitiesFragment extends Fragment implements IWebservice.IUserInfo {


    @Inject
    ImageLoader imageLoader;
    public ImageView nav, imgProfile, imgAuth;
    public ViewPager pager;
    View rootView = null;
    private TabLayout tabHost;
    private RelativeLayout pb;
    MyPayeFragment myPayeFragment = null;
    UserInfoFragment userInfoFragment = null;
    UserInfoFragment userInfoFragment2 = null;
    FragmentActivitiesBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_activities, container, false);
        rootView = binding.getRoot();
        DeclareElements();
        getUserInfo();
        tabHost = rootView.findViewById(R.id.materialTabHost);
        pager = rootView.findViewById(R.id.pager);
        tabHost.setupWithViewPager(pager);
        setupViewPager(pager);
        pager.setCurrentItem(pager.getAdapter().getCount() - 1);
        //tabHost.getTabAt(0).setIcon(R.drawable.ic_login_username);
        //tabHost.getTabAt(1).setIcon(R.drawable.ic_history);
        tabHost.getTabAt(0).setIcon(R.drawable.ic_date_event);
        nav.setOnClickListener(v -> ((IWebservice.IBottomSheetNavigation) getActivity()).showBottomSheetNavigation());

        ((TitleMain) getContext()).FragName("رویدادها");
        return rootView;
    }

    private void DeclareElements() {
        Application.getComponent().Inject(this);
        pb = rootView.findViewById(R.id.progressBar);
        nav = rootView.findViewById(R.id.img_navigation);
        imgProfile = rootView.findViewById(R.id.img_profile);
        imgAuth = rootView.findViewById(R.id.img_auth);
    }

    private void setupViewPager(ViewPager viewPager/*, UserInfoModel userInfoModel*/) {

        if (myPayeFragment == null)
            myPayeFragment = new MyPayeFragment();
        if (userInfoFragment == null)
            userInfoFragment = new UserInfoFragment();
        if (userInfoFragment2 == null)
            userInfoFragment2 = new UserInfoFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(userInfoFragment, /*"رویدادهای درخواستی من"*/"");
        adapter.addFragment( /*UncomingEventsFragment*/myPayeFragment, ""/*"رویدادهایی که شرکت کردم"*/);
        adapter.addFragment(userInfoFragment2, ""/*"رویدادهای من"*/);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void getResult(UserInfoModel userInfoModel) throws Exception {
        binding.setUserInfoItem(userInfoModel);
        imageLoader.displayImage(userInfoModel.getEventOwner().getProfileImage(), imgProfile);
        pb.setVisibility(View.GONE);
    }

    @Override
    public void getError(String error) throws Exception {
    }

    private void getUserInfo() {

        IWebservice.IUserInfo del = new IWebservice.IUserInfo() {
            @Override
            public void getResult(UserInfoModel userInfoModel) throws Exception {
                binding.setUserInfoItem(userInfoModel);
                imageLoader.displayImage(userInfoModel.getEventOwner().getProfileImage(), imgProfile);
                pb.setVisibility(View.GONE);
            }

            @Override
            public void getError(String error) throws Exception {

            }
        };
        new AsynctaskGetUserInfo(del).getData();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private Fragment mCurrentFragment;

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        //...
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
