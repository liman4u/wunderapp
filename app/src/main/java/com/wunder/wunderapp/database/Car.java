package com.wunder.wunderapp.database;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.common.base.Objects;


/**
 * Immutable model class for a Car.
 */

@Entity(tableName = "cars")
public class Car {

    @ColumnInfo(name = "car_name")
    private final String name;
    @ColumnInfo(name = "address")
    private final String address;
    @ColumnInfo(name = "latitude")
    private final Double latitude;
    @ColumnInfo(name = "longitude")
    private final Double longitude;
    @ColumnInfo(name = "engine_type")
    private final String engineType;
    @ColumnInfo(name = "exterior")
    private final String exterior;
    @ColumnInfo(name = "interior")
    private final String interior;
    @ColumnInfo(name = "fuel")
    private final String fuel;
    @ColumnInfo(name = "vin")
    private final String vin;
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "entry_id")
    private String id;



/*



    public Car(@Nullable String name, @Nullable String address, @Nullable Double latitude, @Nullable Double longitude, @Nullable  String engineType, @Nullable String exterior, @Nullable String interior, @Nullable String fuel, @Nullable String vin){
        this(UUID.randomUUID().toString(), name, address, latitude, longitude, engineType, exterior, interior, fuel, vin);

    }*/


    /**
     * Use this constructor to add a Car if Car already has an id
     *
     * @param id         id of car
     * @param name       name of car
     * @param address    address of car
     * @param latitude   location coordinates latitude
     * @param longitude  location coordinates longitude
     * @param engineType engine type of car
     * @param exterior   exterior status of car
     * @param interior   interior status of car
     * @param fuel       fuel consumption of car
     * @param vin        Vehicle Identification No. of car
     */

    public Car(String id, String name, String address, Double latitude, Double longitude, String engineType, String exterior, String interior, String fuel, String vin) {

        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.engineType = engineType;
        this.exterior = exterior;
        this.interior = interior;
        this.fuel = fuel;
        this.vin = vin;

    }

    public String getEngineType() {
        return engineType;
    }

    public String getExterior() {
        return exterior;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getFuel() {
        return fuel;
    }

    public String getId() {
        return id;
    }

    public void setId(String mId) {
        this.id = mId;
    }

    public String getInterior() {
        return interior;
    }

    public String getName() {
        return name;
    }

    public String getVin() {
        return vin;
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(id, vin);
    }

}
