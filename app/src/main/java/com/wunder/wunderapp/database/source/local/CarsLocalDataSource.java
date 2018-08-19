package com.wunder.wunderapp.database.source.local;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.wunder.wunderapp.database.Car;
import com.wunder.wunderapp.database.source.CarsDataSource;
import com.wunder.wunderapp.utils.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class CarsLocalDataSource implements CarsDataSource {


    private static volatile CarsLocalDataSource INSTANCE;

    private CarsDao mCarsDao;

    private AppExecutors mAppExecutors;

    // Prevent direct instantiation.
    private CarsLocalDataSource(@NonNull AppExecutors appExecutors,
                                @NonNull CarsDao carsDao) {
        mAppExecutors = appExecutors;
        mCarsDao = carsDao;
    }

    public static CarsLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                  @NonNull CarsDao carsDao) {
        if (INSTANCE == null) {
            synchronized (CarsLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CarsLocalDataSource(appExecutors, carsDao);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    static void clearInstance() {
        INSTANCE = null;
    }

    /**
     * Note: {@link LoadCarsCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */


    @Override
    public void getCars(@NonNull final LoadCarsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Car> cars = mCarsDao.getCars();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (cars.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable();
                        } else {
                            callback.onCarsLoaded(cars);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    /**
     * Note: {@link GetCarCallback#onDataNotAvailable()} is fired if the {@link Car} isn't
     * found.
     */
    @Override
    public void getCar(@NonNull final String carId, @NonNull final GetCarCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Car car = mCarsDao.getCarById(carId);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (car != null) {
                            callback.onCarLoaded(car);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveCar(@NonNull final Car car) {
        checkNotNull(car);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mCarsDao.insertCar(car);
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void refreshCars() {
        // Not required because the {@link CarsRepository} handles the logic of refreshing the
        // cars from all the available data sources.
    }

    @Override
    public void deleteAllCars() {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mCarsDao.deleteCars();
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public void deleteCar(@NonNull final String carId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mCarsDao.deleteCarById(carId);
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }
}
