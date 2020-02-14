package ir.payebash.Fragments.request;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import ir.payebash.Interfaces.IWebservice.TitleMain;
import ir.payebash.R;

public class ParentFragment extends Fragment {

    public ViewPager pager;
    View rootView = null;
    private TabLayout tabHost;
    private RequestFragment requestFragment = null;
    private RoomsFragment roomsFragment = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_rooms_request, container, false);
        DeclareElements();

        tabHost = rootView.findViewById(R.id.materialTabHost);
        pager = rootView.findViewById(R.id.pager);
        setupViewPager(pager);
        tabHost.setupWithViewPager(pager);
        pager.setCurrentItem(1);
        ((TitleMain) getContext()).FragName("رویدادها");
        return rootView;
    }

    private void DeclareElements() {
        //navigationView = rootView.findViewById(R.id.nav_view);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        if (null == requestFragment)
            requestFragment = new RequestFragment();
        if (null == roomsFragment)
            roomsFragment = new RoomsFragment();

        adapter.addFragment(requestFragment, "درخواست ها");
        adapter.addFragment(roomsFragment, "هم پایه ها");
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
