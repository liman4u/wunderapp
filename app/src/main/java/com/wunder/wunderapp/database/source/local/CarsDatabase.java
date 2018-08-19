package com.wunder.wunderapp.database.source.local;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.wunder.wunderapp.database.Car;

@Database(entities = {Car.class}, version = 1)
public abstract class CarsDatabase extends RoomDatabase {


    private static final Object sLock = new Object();
    private static CarsDatabase INSTANCE;

    public static CarsDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {

                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        CarsDatabase.class, "Cars.db")
                        .build();
            }

            return INSTANCE;

        }


    }

    public abstract CarsDao carsDao();


}
