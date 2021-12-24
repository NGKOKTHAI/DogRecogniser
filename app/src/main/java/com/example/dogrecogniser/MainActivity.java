package com.example.dogrecogniser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottom_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide statusbar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //bottom navigation
        bottom_nav = findViewById(R.id.bottom_nav);
        getSupportFragmentManager().beginTransaction().replace(R.id.body_container,new HomeFragment()).commit();
        bottom_nav.setSelectedItemId(R.id.nav_camera);

        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch(item.getItemId())
                {
                    case R.id.nav_camera:
                        fragment = new HomeFragment();
                        break;

                    case R.id.nav_search:
                        fragment = new SearchFragment();
                        break;

                    case R.id.nav_history:
                        fragment = new HistoryFragment();
                        break;

                    case R.id.nav_location:
                        fragment = new LocationFragment();
                        break;

                    case R.id.nav_more:
                        fragment = new MoreFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.body_container,fragment).commit();

                return true;
            }
        });

    }
}