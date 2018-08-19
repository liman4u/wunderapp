package com.wunder.wunderapp.showcarsonmap;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wunder.wunderapp.R;
import com.wunder.wunderapp.database.Car;
import com.wunder.wunderapp.database.source.CarsDataSource;
import com.wunder.wunderapp.database.source.CarsRepository;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class MapViewPresenter implements MapViewContract.Presenter {

    private final CarsRepository mCarsRepository;
    private final MapViewContract.View mMapView;


    private List<Marker> MARKERS_CACHE = new ArrayList<>();


    public MapViewPresenter(@NonNull CarsRepository repository, @NonNull MapViewContract.View view) {
        mCarsRepository = checkNotNull(repository, "The repository cannot be null");
        mMapView = checkNotNull(view, "The view cannot be null!");

        mMapView.setPresenter(this);
    }


    @Override
    public void start() {
        //load the map, get users location and populate the markers.
        mMapView.setLoadingIndicator(true);

    }

    @Override
    public void pause() {

    }


    @Override
    public void loadAllMarkers(List<Marker> markers, @Nullable LatLngBounds bounds) {
        mMapView.showCarMarkers(markers, bounds);
    }


    @Override
    public void loadMarkers(final GoogleMap googleMap) {

        //get List of cars from DB populate on map with view

        mCarsRepository.getCars(new CarsDataSource.LoadCarsCallback() {
            @Override
            public void onCarsLoaded(List<Car> cars) {

                if (!mMapView.isActive())
                    return;

                populateMarkers(googleMap, cars);

            }

            @Override
            public void onDataNotAvailable() {

                //Todo Data not available

            }
        });


    }

    private void populateMarkers(GoogleMap mMap, List<Car> cars) {

        checkNotNull(MARKERS_CACHE);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        for (Car car : cars) {

            addMarker(mMap, car.getLatitude(), car.getLongitude(), car.getName());
            builder.include(new LatLng(car.getLatitude(), car.getLongitude()));
        }

        LatLngBounds bounds = builder.build();


        mMapView.showCarMarkers(MARKERS_CACHE, bounds);

    }


    Marker addMarker(GoogleMap mMap, Double lat, Double lon, String name) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .anchor(0.5f, 0.5f)
                .title(name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_mech)));

    }


    @Override
    public void showSelectedMarkerInfo(Marker marker) {

        mMapView.showSelectedMarkerInfo(marker);

    }

    @Override
    public void getUsersCurrentLocation(Location location) {


        mMapView.setLoadingIndicator(false);
        mMapView.showUsersCurrentLocation(location);

    }


}
