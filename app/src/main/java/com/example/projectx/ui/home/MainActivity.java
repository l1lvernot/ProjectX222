package com.example.projectx.ui.home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.projectx.R;
import com.example.projectx.ui.AddFragment;
import com.example.projectx.ui.HomeFragment;
import com.example.projectx.ui.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Set up ViewPager adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new AddFragment());
        adapter.addFragment(new ProfileFragment());
        viewPager.setAdapter(adapter);

        // Connect ViewPager with BottomNavigationView
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (item.getItemId() == R.id.add) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (item.getItemId() == R.id.profile) {
                viewPager.setCurrentItem(2);
                return true;
            } else {
                return false;
            }

        });


        // Listen for page changes in ViewPager
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                bottomNavigation.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
