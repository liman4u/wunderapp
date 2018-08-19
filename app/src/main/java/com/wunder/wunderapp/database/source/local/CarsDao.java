package com.wunder.wunderapp.database.source.local;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.wunder.wunderapp.database.Car;

import java.util.List;

/**
 * Data Access Object for the cars table in the local persistent repo
 */

@Dao
public interface CarsDao {


    /**
     * Select all cars from the table
     *
     * @return all cars.
     */

    @Query("SELECT * FROM Cars")
    List<Car> getCars();


    /**
     * Select car from cars by id
     *
     * @param carId the car id
     * @return the car with id carId
     */

    @Query("SELECT * FROM Cars WHERE entry_id = :carId")
    Car getCarById(String carId);


    /**
     * Insert car into database. If car exists, replace it
     *
     * @param car the car to be inserted
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCar(Car car);


    /**
     * Update a car
     *
     * @param car car to be updated
     * @return value which should be 1
     */

    @Update
    int updateCar(Car car);


    /**
     * Delete car by id
     *
     * @param carId id of the car
     * @return no of car deleted = 1
     */
    @Query("DELETE FROM Cars WHERE entry_id = :carId")
    int deleteCarById(String carId);


    /**
     * Delete all cars
     */
    @Query("DELETE FROM Cars")
    void deleteCars();
}
