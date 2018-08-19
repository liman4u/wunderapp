package com.wunder.wunderapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.wunder.wunderapp.cars.CarsFragment;
import com.wunder.wunderapp.cars.CarsPresenter;
import com.wunder.wunderapp.showcarsonmap.MapViewFragment;
import com.wunder.wunderapp.showcarsonmap.MapViewPresenter;
import com.wunder.wunderapp.utils.ActivityUtils;
import com.wunder.wunderapp.utils.NetworkUtils;

public class BaseActivity extends AppCompatActivity {


    CarsFragment fragment;
    MapViewFragment fragment2;
    MapViewPresenter mapViewPresenter;
    private CarsPresenter mCarsPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment = CarsFragment.newInstance();
        mCarsPresenter = new CarsPresenter(
                Injection.provideCarsRepository(getApplicationContext()), fragment);


        Log.i("BASE Activity", "ON CREATE");


        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.cars_list:
                        if (fragment == null) {
                            fragment = CarsFragment.newInstance();
                        }
                        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.contentFrame);

                        Log.i("BASE ACTIVITY", "CAR LIST");


                        break;


                    case R.id.map:

                        if (fragment2 == null) {
                            fragment2 = MapViewFragment.newInstance();
                        }

                        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment2, R.id.contentFrame);

                        // Create the presenter
                        if (mapViewPresenter == null)
                            mapViewPresenter = new MapViewPresenter(
                                    Injection.provideCarsRepository(getApplicationContext()), fragment2);

                        Log.i("BASE ACTIVITY", "MAP VIEW");


                        break;
                }

                return true;
            }
        });


        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.contentFrame);


        if (!NetworkUtils.isNetworkConnected(this))
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_LONG).show();


    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }
}
