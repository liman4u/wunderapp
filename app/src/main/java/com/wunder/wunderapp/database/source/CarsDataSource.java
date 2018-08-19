package com.wunder.wunderapp.database.source;

import android.support.annotation.NonNull;

import com.wunder.wunderapp.database.Car;

import java.util.List;

public interface CarsDataSource {


    void getCars(@NonNull LoadCarsCallback callback);

    void getCar(@NonNull String carId, @NonNull GetCarCallback callback);

    void saveCar(@NonNull Car car);

    void refreshCars();

    void deleteAllCars();

    void deleteCar(@NonNull String carId);

    interface LoadCarsCallback {

        void onCarsLoaded(List<Car> cars);

        void onDataNotAvailable();
    }

    interface GetCarCallback {

        void onCarLoaded(Car car);

        void onDataNotAvailable();
    }
}
