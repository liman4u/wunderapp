package com.wunder.wunderapp.showcarsonmap;


import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wunder.wunderapp.R;
import com.wunder.wunderapp.database.Car;
import com.wunder.wunderapp.utils.Constants;
import com.wunder.wunderapp.utils.ScreenUtils;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static com.google.common.base.Preconditions.checkNotNull;

public class MapViewFragment extends Fragment implements MapViewContract.View, LocationListener {
    LocationManager locationManager;
    String provider;
    MapViewContract.Presenter presenter;
    ProgressDialog progress;

    GoogleApiClient mGoogleApiClient;
    List<Marker> markers;
    List<Car> cars;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    Boolean IS_GPS_ENABLED = null;
    boolean showAllMarkers = true;
    MapViewContract.MarkerItemClickListener markerItemClickListener = new MapViewContract.MarkerItemClickListener() {
        @Override
        public void onMarkerClick(Marker marker, boolean showAllMarkers) {

            if (showAllMarkers) {

                presenter.loadMarkers(mMap);

            } else {
                presenter.showSelectedMarkerInfo(marker);
            }


        }
    };
    OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {

            mMap = googleMap;
            presenter.start();
            requestUsersLocation();


            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    markerItemClickListener.onMarkerClick(marker, showAllMarkers);
                    return true;
                }
            });
        }

    };
    private View noDataPlaceHolder;
    private GroundOverlay groundOverlay;
    private Location mLastLocation;


    public MapViewFragment() {
    }

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progress = new ProgressDialog(getActivity());
        progress.setIndeterminate(true);
        progress.setMessage("Please wait...");


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_map, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mapFragment.getMapAsync(onMapReadyCallback);
            }
        }, 500);


        return root;

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();

        if (IS_GPS_ENABLED != null)
            if (IS_GPS_ENABLED)
                requestUsersLocation();

    }

    @Override
    public void onPause() {
        super.onPause();

        if (locationManager != null)
            locationManager.removeUpdates(this);

    }

    @Override
    public void setPresenter(@NonNull MapViewContract.Presenter presenter) {
        this.presenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void setLoadingIndicator(boolean active) {

        if (active) {
            progress.show();
        } else {
            progress.dismiss();
        }

    }

    @Override
    public void showCarMarkers(List<Marker> markers, LatLngBounds bounds) {

        Log.i("MAP VIEW FRAG", "Show All Car Markers - set to false");

        CameraUpdate mCameraUpdate = (CameraUpdateFactory.newLatLngBounds(bounds, ScreenUtils.getScreenWidth(getActivity()) / 8));

        mMap.animateCamera(mCameraUpdate);

        showAllMarkers = false;

    }

    @Override
    public void showSelectedMarkerInfo(Marker marker) {

        Log.i("MAP VIEW FRAG", "Show All Car Markers - set to true");

        Log.i("MAP VIEW FRAG", "On marker click " + marker.getTitle());

        mMap.clear();

        marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude))
                .anchor(0.5f, 0.5f)
                .title(marker.getTitle())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_mech)));

        moveCameraToPosition(mMap, marker.getPosition(), 17f);
        marker.showInfoWindow();

        showAllMarkers = true;


    }

    @Override
    public void showUsersCurrentLocation(Location location) {

        mMap.clear();

        mLastLocation = location;

        if (groundOverlay == null)
            groundOverlay = addOverlay(mMap, new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        else {
            groundOverlay.remove();
            groundOverlay = addOverlay(mMap, new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }


        presenter.loadMarkers(mMap);


    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    void requestUsersLocation() {

        Log.i("MAP VIEW FRAG", "Requesting location");

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PERMISSION_FINE_LOCATION);


        } else {

            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            IS_GPS_ENABLED = enabled;

            if (!enabled) {


                showAlertDialog(false, "GPS is disabled", "Would you like to enable it?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        progress.dismiss();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);

                        IS_GPS_ENABLED = true;

                    }
                }, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }, "CANCEL", 0);


            }


            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);

            provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null)
                onLocationChanged(location);


        }


    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(Location location) {

        presenter.getUsersCurrentLocation(location);
        mMap.setMyLocationEnabled(true);


        if (locationManager != null)
            locationManager.removeUpdates(this);


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    GroundOverlay addOverlay(GoogleMap mMap, LatLng place) {

        GroundOverlay groundOverlay = mMap.addGroundOverlay(new
                GroundOverlayOptions()
                .position(place, 100)
                .transparency(0.5f)
                .zIndex(3)
                .image(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(ContextCompat.getDrawable(getActivity(), R.drawable.map_overlay)))));

        startOverlayAnimation(groundOverlay);

        return groundOverlay;
    }

    Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    void startOverlayAnimation(final GroundOverlay groundOverlay) {

        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator vAnimator = ValueAnimator.ofInt(0, 100);
        vAnimator.setRepeatCount(ValueAnimator.INFINITE);
        vAnimator.setRepeatMode(ValueAnimator.RESTART);
        vAnimator.setInterpolator(new LinearInterpolator());
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final Integer val = (Integer) valueAnimator.getAnimatedValue();
                groundOverlay.setDimensions(val);


            }
        });

        ValueAnimator tAnimator = ValueAnimator.ofFloat(0, 1);
        tAnimator.setRepeatCount(ValueAnimator.INFINITE);
        tAnimator.setRepeatMode(ValueAnimator.RESTART);
        tAnimator.setInterpolator(new LinearInterpolator());
        tAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float val = (Float) valueAnimator.getAnimatedValue();
                groundOverlay.setTransparency(val);

            }
        });

        animatorSet.setDuration(3000);
        animatorSet.playTogether(vAnimator, tAnimator);
        animatorSet.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        requestUsersLocation();
    }

    public void showAlertDialog(Boolean cancelable, @Nullable String title, @Nullable String message,
                                @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                @NonNull String positiveText,
                                @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                @NonNull String negativeText, @Nullable int icon_drawable) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppDialog);
        builder.setTitle(title);
        builder.setCancelable(cancelable);


        if (icon_drawable != 0) builder.setIcon(icon_drawable);
        builder.setMessage(message);

        if (onPositiveButtonClickListener != null)
            builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        if (onNegativeButtonClickListener != null)
            builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        builder.show();
    }


    CameraPosition moveCameraToPosition(GoogleMap mMap, LatLng latLng, Float ZOOM) {

        CameraPosition mCameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(ZOOM)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));

      /*
        if (mLastLocation != null) {

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastLocation.getLatitude(),
                            mLastLocation.getLongitude()), DEFAULT_ZOOM));
        }*/


        return mCameraPosition;
    }

}
