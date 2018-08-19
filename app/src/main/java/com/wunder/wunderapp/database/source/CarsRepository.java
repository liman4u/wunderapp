package com.wunder.wunderapp.database.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wunder.wunderapp.database.Car;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class CarsRepository implements CarsDataSource {

    private static CarsRepository INSTANCE = null;

    private final CarsDataSource mTasksRemoteDataSource;

    private final CarsDataSource mTasksLocalDataSource;


    Map<String, Car> mCachedCars;


    boolean mCacheIsDirty = false;

    // Instantiation.
    private CarsRepository(@NonNull CarsDataSource carsRemoteDataSource, @NonNull CarsDataSource carsLocalDataSource) {
        mTasksRemoteDataSource = checkNotNull(carsRemoteDataSource);
        mTasksLocalDataSource = checkNotNull(carsLocalDataSource);
    }

    /**
     * Returns the single instance of this class.
     *
     * @param carsRemoteDataSource the server data source
     * @param carsLocalDataSource  the device storage data source
     * @return the {@link CarsRepository} instance
     */
    public static CarsRepository getInstance(CarsDataSource carsRemoteDataSource,
                                             CarsDataSource carsLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new CarsRepository(carsRemoteDataSource, carsLocalDataSource);
        }
        return INSTANCE;
    }


    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets tasks from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link LoadCarsCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getCars(@NonNull final LoadCarsCallback callback) {
        checkNotNull(callback);

        Log.i("CARS REPO", "Is Cache dirty? " + mCacheIsDirty);

        // Respond immediately with cached cars data if available and not dirty
        if (mCachedCars != null && !mCacheIsDirty) {
            callback.onCarsLoaded(new ArrayList<>(mCachedCars.values()));
            return;
        }

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getCarsFromRemoteDataSource(callback);
            Log.i("CARS REPO", "Get Cars from remote");

        } else {
            // Query the local storage if available. If not, query the network.

            Log.i("CARS REPO", "Get Cars from local");

            mTasksLocalDataSource.getCars(new LoadCarsCallback() {
                @Override
                public void onCarsLoaded(List<Car> cars) {
                    refreshCache(cars);
                    callback.onCarsLoaded(new ArrayList<>(mCachedCars.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getCarsFromRemoteDataSource(callback);
                }
            });
        }
    }

    @Override
    public void saveCar(@NonNull Car car) {
        checkNotNull(car);
        mTasksRemoteDataSource.saveCar(car);
        mTasksLocalDataSource.saveCar(car);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedCars == null) {
            mCachedCars = new LinkedHashMap<>();
        }
        mCachedCars.put(car.getId(), car);
    }


    /**
     * Gets tasks from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     * <p>
     * Note: {@link GetCarCallback#onDataNotAvailable()} is fired if both data sources fail to
     * get the data.
     */
    @Override
    public void getCar(@NonNull final String carId, @NonNull final GetCarCallback callback) {
        checkNotNull(carId);
        checkNotNull(callback);

        final Car cachedCar = getCarWithId(carId);

        // Respond immediately with cache if available
        if (cachedCar != null) {
            callback.onCarLoaded(cachedCar);
            return;
        }

        // Load from server/persisted if needed.

        // Is the task in the local data source? If not, query the network.
        mTasksLocalDataSource.getCar(carId, new GetCarCallback() {
            @Override
            public void onCarLoaded(Car car) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedCars == null) {
                    mCachedCars = new LinkedHashMap<>();
                }
                mCachedCars.put(car.getId(), car);
                callback.onCarLoaded(car);
            }

            @Override
            public void onDataNotAvailable() {
                mTasksRemoteDataSource.getCar(carId, new GetCarCallback() {
                    @Override
                    public void onCarLoaded(Car car) {
                        // Do in memory cache update to keep the app UI up to date
                        if (mCachedCars == null) {
                            mCachedCars = new LinkedHashMap<>();
                        }
                        mCachedCars.put(car.getId(), car);
                        callback.onCarLoaded(car);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void refreshCars() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllCars() {
        mTasksRemoteDataSource.deleteAllCars();
        mTasksLocalDataSource.deleteAllCars();

        if (mCachedCars == null) {
            mCachedCars = new LinkedHashMap<>();
        }
        mCachedCars.clear();
    }

    @Override
    public void deleteCar(@NonNull String carId) {
        mTasksRemoteDataSource.deleteCar(checkNotNull(carId));
        mTasksLocalDataSource.deleteCar(checkNotNull(carId));

        mCachedCars.remove(carId);
    }

    private void getCarsFromRemoteDataSource(@NonNull final LoadCarsCallback callback) {
        mTasksRemoteDataSource.getCars(new LoadCarsCallback() {

            @Override
            public void onCarsLoaded(List<Car> cars) {

                Log.i("CARS REPO", "REMOTE: ON CARS LOADED Size = " + cars.size());

                refreshCache(cars);
                refreshLocalDataSource(cars);
                callback.onCarsLoaded(new ArrayList<>(mCachedCars.values()));
            }

            @Override
            public void onDataNotAvailable() {

                Log.i("CARS REPO", "REMOTE: DATA UNAVAILABLE");

                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Car> cars) {
        if (mCachedCars == null) {
            mCachedCars = new LinkedHashMap<>();
        }
        mCachedCars.clear();
        for (Car car : cars) {
            mCachedCars.put(car.getId(), car);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Car> cars) {
        mTasksLocalDataSource.deleteAllCars();
        for (Car car : cars) {
            mTasksLocalDataSource.saveCar(car);
        }
    }

    @Nullable
    private Car getCarWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedCars == null || mCachedCars.isEmpty()) {
            return null;
        } else {
            return mCachedCars.get(id);
        }
    }
}
