package com.salman.firebasefindfriends.findfriends.ui;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.salman.firebasefindfriends.R;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {

    private View mView;


    public MainFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_main,container,false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ViewPager viewPager = mView.findViewById(R.id.view_pager);
        TabLayout  tabLayout = mView.findViewById(R.id.tabLayout);



        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }
    private void setUpViewPager(ViewPager viewPager) {

        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        adapter.addFragments(new UserMessageListFragment(),"Messages");
        adapter.addFragments(new ActiveFriendsFragment(),"Active");
        adapter.addFragments(new GroupFragment(),"Groups");

        viewPager.setAdapter(adapter);

    }

    public class PagerAdapter extends FragmentPagerAdapter
    {
        List<Fragment> mFragments = new ArrayList<>();
        List<String> mTitle = new ArrayList<>();


        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mTitle.get(position);
        }

        public void addFragments(Fragment fragment, String title)
        {
            mFragments.add(fragment);
            mTitle.add(title);
        }
    }
}
