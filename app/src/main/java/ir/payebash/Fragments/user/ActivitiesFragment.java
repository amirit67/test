package ir.payebash.Fragments.user;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ir.payebash.Activities.MainActivity;
import ir.payebash.Application;
import ir.payebash.Fragments.MyPayeFragment;
import ir.payebash.Interfaces.IWebservice;
import ir.payebash.Interfaces.IWebservice.TitleMain;
import ir.payebash.R;

public class ActivitiesFragment extends Fragment {


    @Inject
    ImageLoader imageLoader;
    public ImageView nav, imgProfile;
    public ViewPager pager;
    View rootView = null;
    private TabLayout tabHost;
    //private NavigationView navigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_activities, container, false);
            DeclareElements();

            tabHost = rootView.findViewById(R.id.materialTabHost);
            pager = rootView.findViewById(R.id.pager);
            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            setupViewPager(pager);
            tabHost.setupWithViewPager(pager);
            pager.setCurrentItem(2);
            tabHost.getTabAt(0).setIcon(R.drawable.ic_login_username);
            tabHost.getTabAt(1).setIcon(R.drawable.ic_history);
            tabHost.getTabAt(2).setIcon(R.drawable.ic_date_event);

            nav.setOnClickListener(v ->((IWebservice.IBottomSheetNavigation) getContext()).showBottomSheet());
        }
        ((TitleMain) getContext()).FragName("رویدادها");
        return rootView;
    }

    private void DeclareElements() {
        //navigationView = rootView.findViewById(R.id.nav_view);
        Application.getComponent().Inject(this);
        nav = rootView.findViewById(R.id.img_navigation);
        imgProfile = rootView.findViewById(R.id.img_profile);
        imageLoader.displayImage(Application.preferences.getString(getString(R.string.ProfileImage) , ""), imgProfile);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new /*EventsWantedFragment*/UserInfoFragment(), /*"رویدادهای درخواستی من"*/"");
        adapter.addFragment(new /*UncomingEventsFragment*/MyPayeFragment(), ""/*"رویدادهایی که شرکت کردم"*/);
        adapter.addFragment(new /*MyPayeFragment()*/UserInfoFragment(), ""/*"رویدادهای من"*/);
        viewPager.setAdapter(adapter);
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
