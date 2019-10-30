package ir.payebash.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import ir.payebash.Interfaces.IWebservice.TitleMain;
import ir.payebash.R;

public class ActivitiesFragment extends Fragment {


    public static Toolbar toolbar;
    public static ViewPager pager;
    View rootView = null;
    private TabLayout tabHost;

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
            //HSH.setTypeFace2(tabHost, Application.font);
            //HSH.setTypeFace2(toolbar, Application.font);
        }
        ((TitleMain) getContext()).FragName("رویدادها");
        return rootView;
    }

    private void DeclareElements() {


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new EventsWantedFragment(), "رویدادهای درخواستی من");
        adapter.addFragment(new UncomingEventsFragment(), "رویدادهایی که شرکت کردم");
        adapter.addFragment(new MyPayeFragment(), "رویدادهای من");
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
