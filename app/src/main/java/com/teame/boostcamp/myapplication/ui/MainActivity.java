package com.teame.boostcamp.myapplication.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.teame.boostcamp.myapplication.R;
import com.teame.boostcamp.myapplication.adapter.MainViewPagerAdapter;
import com.teame.boostcamp.myapplication.databinding.ActivityMainBinding;
import com.teame.boostcamp.myapplication.ui.search.SearchFragment;
import com.teame.boostcamp.myapplication.ui.base.BaseActivity;
import com.teame.boostcamp.myapplication.ui.search.SearchPlaceFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements FragmentCallback {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.bnvMainNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        setupViewPager();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_main:
                binding.vpFragment.setCurrentItem(0);
                return true;
            case R.id.navigation_my_list:
                binding.vpFragment.setCurrentItem(1);
                return true;
            case R.id.navigation_sns:
                binding.vpFragment.setCurrentItem(2);
                return true;
        }
        return false;
    };

    @SuppressLint("ClickableViewAccessibility")
    private void setupViewPager() {
        //ViewPager remove swipe
        binding.vpFragment.setOnTouchListener((__, ___) -> true);
        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        Fragment fragmentHome = SearchFragment.newInstance();
        Fragment fragmentWallet = MyListFragment.newInstance();
        Fragment fragmentNavigationDrawer = SNSFragment.newInstance();
        mainViewPagerAdapter.addFragment(fragmentHome);
        mainViewPagerAdapter.addFragment(fragmentWallet);
        mainViewPagerAdapter.addFragment(fragmentNavigationDrawer);
        binding.vpFragment.setAdapter(mainViewPagerAdapter);
        binding.vpFragment.setOffscreenPageLimit(mainViewPagerAdapter.getCount()-1);
    }

    @Override
    public void startNewFragment() {
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.change, SearchPlaceFragment.newInstance());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
