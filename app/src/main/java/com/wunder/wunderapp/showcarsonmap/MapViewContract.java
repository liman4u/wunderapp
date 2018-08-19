package com.wunder.wunderapp.showcarsonmap;

import android.location.Location;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.wunder.wunderapp.BasePresenter;
import com.wunder.wunderapp.BaseView;

import java.util.List;

public interface MapViewContract {


    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showCarMarkers(List<Marker> markers, LatLngBounds bounds);

        void showUsersCurrentLocation(Location location);

        void showSelectedMarkerInfo(Marker marker);

        boolean isActive();


    }


    interface Presenter extends BasePresenter {


        void loadMarkers(final GoogleMap googleMap);

        void showSelectedMarkerInfo(Marker marker);

        void getUsersCurrentLocation(Location location);

        void loadAllMarkers(List<Marker> markers, @Nullable LatLngBounds bounds);


    }


    interface MarkerItemClickListener {

        void onMarkerClick(Marker marker, boolean showAllMarkers);


    }


}
