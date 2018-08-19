package com.wunder.wunderapp.database.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.google.common.collect.Lists;
import com.wunder.wunderapp.database.Car;
import com.wunder.wunderapp.database.source.CarsDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class CarsRemoteDataSource implements CarsDataSource {

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;
    private final static Map<String, Car> CARS_SERVICE_DATA = new LinkedHashMap<>();
    private static CarsRemoteDataSource INSTANCE;
    private final String URL = "https://s3-us-west-2.amazonaws.com/wunderbucket/locations.json";
    Context mContext;


    // Prevent direct instantiation.
    private CarsRemoteDataSource(Context context) {

        this.mContext = context;
    }

    public static CarsRemoteDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CarsRemoteDataSource(context);
        }
        return INSTANCE;
    }

    void addCar(Car newCar) {
        CARS_SERVICE_DATA.put(newCar.getId(), newCar);
    }

    /**
     * Note: {@link LoadCarsCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    @Override
    public void getCars(final @NonNull LoadCarsCallback callback) {

        //Connect to network, get all cars


        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, URL,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        // TODO handle the response
                        try {
                            if (response != null) {


                                String str = new String(response, "UTF-8");


                                parseJson(str, callback);


                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO handle the error
                error.printStackTrace();
            }
        }, null);


        RequestQueue mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(), new HurlStack());
        mRequestQueue.add(request);


    }

    /**
     * Note: {@link GetCarCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    @Override
    public void getCar(@NonNull String carId, final @NonNull GetCarCallback callback) {
        final Car car = CARS_SERVICE_DATA.get(carId);

    }

    @Override
    public void saveCar(@NonNull Car car) {


    }


    @Override
    public void refreshCars() {
        // Not required because the {@link CarsRepository} handles the logic of refreshing the
        // cars from all the available data sources.
    }

    @Override
    public void deleteAllCars() {


    }

    @Override
    public void deleteCar(@NonNull String carId) {


    }


    void parseJson(String jsonString, LoadCarsCallback callback) {


        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            JSONArray jsonArray = jsonObject.getJSONArray("placemarks");


            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                Car car = new Car(
                        UUID.randomUUID().toString(),
                        jsonObject1.getString("name"),
                        jsonObject1.getString("address"),
                        jsonObject1.getJSONArray("coordinates").getDouble(1),
                        jsonObject1.getJSONArray("coordinates").getDouble(0),
                        jsonObject1.getString("engineType"),
                        jsonObject1.getString("exterior"),
                        jsonObject1.getString("interior"),
                        jsonObject1.getString("fuel"),
                        jsonObject1.getString("vin")
                );


                CARS_SERVICE_DATA.put(car.getId(), car);


            }

            callback.onCarsLoaded(Lists.newArrayList(CARS_SERVICE_DATA.values()));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}