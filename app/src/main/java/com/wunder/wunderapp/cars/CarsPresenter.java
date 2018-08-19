package com.wunder.wunderapp.cars;

import android.support.annotation.NonNull;
import android.util.Log;

import com.wunder.wunderapp.database.Car;
import com.wunder.wunderapp.database.source.CarsDataSource;
import com.wunder.wunderapp.database.source.CarsRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class CarsPresenter implements CarsContract.Presenter {

    private final CarsRepository mCarsRepository;
    private final CarsContract.View mCarsView;

    private boolean isFirstLoad = true;


    public CarsPresenter(@NonNull CarsRepository repository, @NonNull CarsContract.View view) {
        mCarsRepository = checkNotNull(repository, "The repository cannot be null");
        mCarsView = checkNotNull(view, "The view cannot be null!");

        mCarsView.setPresenter(this);
    }


    @Override
    public void result(int requestCode, int resultCode) {

    }

    @Override
    public void loadCars(boolean forceUpdate) {

        Log.i("CARS PRESENTER", "Load cars Method");
        Log.i("CARS PRESENTER", "FORCE UPDATE = " + forceUpdate);
        Log.i("CARS PRESENTER", "FIRST LOAD = " + isFirstLoad);


        loadCarsData(forceUpdate || isFirstLoad, forceUpdate);
        isFirstLoad = false;

    }

    @Override
    public void start() {

        loadCars(false);

    }

    @Override
    public void pause() {

    }


    private void loadCarsData(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mCarsView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            mCarsRepository.refreshCars();
        }


        mCarsRepository.getCars(new CarsDataSource.LoadCarsCallback() {
            @Override
            public void onCarsLoaded(List<Car> cars) {

                // The view may not be able to handle UI updates anymore
                if (!mCarsView.isActive()) {
                    Log.i("CARS PRESENTER", "CARS VIEW IS INACTIVE");
                    return;
                }

                if (showLoadingUI) {
                    mCarsView.setLoadingIndicator(false);
                }

                if (cars.isEmpty()) {
                    mCarsView.showNoCarsPlaceholder();
                } else {
                    mCarsView.showCarList(cars);
                }
            }

            @Override
            public void onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!mCarsView.isActive()) {
                    return;
                }
                mCarsView.showLoadingCarsError();
            }
        });
    }


}
