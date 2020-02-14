package ir.payebash.Fragments.user;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import ir.payebash.Fragments.MyPayeFragment;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.Interfaces.IWebservice.TitleMain;
import ir.payebash.models.user.UserInfoModel;
import ir.payebash.R;

public class ActivitiesFragment extends Fragment implements IWebservice.IUserInfo{


    @Inject
    ImageLoader imageLoader;
    private TextView tvUsername, tvName, tvAboutMe;
    public ImageView nav, imgProfile, imgAuth;
    public ViewPager pager;
    View rootView = null;
    private TabLayout tabHost;
    private RelativeLayout pb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_activities, container, false);
            DeclareElements();

            tabHost = rootView.findViewById(R.id.materialTabHost);
            pager = rootView.findViewById(R.id.pager);
            tabHost.setupWithViewPager(pager);
            setupViewPager(pager);
            pager.setCurrentItem(2);
            tabHost.getTabAt(0).setIcon(R.drawable.ic_login_username);
            tabHost.getTabAt(1).setIcon(R.drawable.ic_history);
            tabHost.getTabAt(2).setIcon(R.drawable.ic_date_event);
            nav.setOnClickListener(v -> ((IWebservice.IBottomSheetNavigation) getActivity()).showBottomSheetNavigation());
        }
        ((TitleMain) getContext()).FragName("رویدادها");
        return rootView;
    }



    private void DeclareElements() {
        Application.getComponent().Inject(this);
        pb = rootView.findViewById(R.id.progressBar);
        tvUsername = rootView.findViewById(R.id.tv_username);
        tvName = rootView.findViewById(R.id.tv_fullname);
        tvAboutMe = rootView.findViewById(R.id.tv_about_me);
        nav = rootView.findViewById(R.id.img_navigation);
        imgProfile = rootView.findViewById(R.id.img_profile);
        imgAuth = rootView.findViewById(R.id.img_auth);
    }

    private void setupViewPager(ViewPager viewPager/*, UserInfoModel userInfoModel*/) {

        UserInfoFragment userInfoFragment = new UserInfoFragment();
        UserInfoFragment userInfoFragment2 = new UserInfoFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(userInfoFragment, /*"رویدادهای درخواستی من"*/"");
        adapter.addFragment(new /*UncomingEventsFragment*/MyPayeFragment(), ""/*"رویدادهایی که شرکت کردم"*/);
        adapter.addFragment(userInfoFragment2, ""/*"رویدادهای من"*/);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void getResult(UserInfoModel userInfoModel) throws Exception {
        imageLoader.displayImage(userInfoModel.getEventOwner().getProfileImage(), imgProfile);
        tvUsername.setText(userInfoModel.getEventOwner().getUserName());
        tvName.setText(userInfoModel.getEventOwner().getName());
        tvAboutMe.setText(userInfoModel.getEventOwner().getAboutMe());
        imgAuth.setVisibility(userInfoModel.getEventOwner().getVerifiedAccount() ? View.VISIBLE : View.GONE);
        pb.setVisibility(View.GONE);
    }

    @Override
    public void getError(String error) throws Exception {

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
