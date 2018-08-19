package com.wunder.wunderapp.cars;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wunder.wunderapp.R;
import com.wunder.wunderapp.database.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class CarsFragment extends Fragment implements CarsContract.View {


    CarsContract.Presenter presenter;
    CarsRecyclerAdapter mAdapter;
    ScrollChildSwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog progress;
    private View noDataView;


    public CarsFragment() {
    }


    public static CarsFragment newInstance() {
        return new CarsFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new CarsRecyclerAdapter(getContext(), new ArrayList<Car>(0));
        mAdapter.setHasStableIds(true);


        progress = new ProgressDialog(getActivity());
        progress.setIndeterminate(true);
        progress.setMessage("Please wait...");
        progress.show();

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cars_list, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.cars_recycler);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        noDataView = root.findViewById(R.id.no_data);

        swipeRefreshLayout = root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );


        // Set the scrolling view in the custom SwipeRefreshLayout.


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadCars(false);
            }
        });

        swipeRefreshLayout.setScrollUpChild(recyclerView);


        return root;

    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();


    }


    @Override
    public void setPresenter(@NonNull CarsContract.Presenter presenter) {
        this.presenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.result(requestCode, resultCode);
    }


    @Override
    public void setLoadingIndicator(final boolean active) {
        Log.i("CARS FRAGMENT", "Setting loading indicator action = " + active);

        if (swipeRefreshLayout == null) {
            return;
        }
        if (swipeRefreshLayout.isRefreshing()) {
            return;
        }

        // Make sure setRefreshing() is called after the layout is done with everything else.
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(active);
            }
        });

    }

    @Override
    public void showLoadingCarsError() {

        Toast.makeText(getContext(), "There was an error accessing the data. Please try again", Toast.LENGTH_LONG).show();

    }

    @Override
    public void showCarList(List<Car> carList) {

        mAdapter.setCarArrayList(carList);
        noDataView.setVisibility(View.GONE);

        if (progress.isShowing())
            progress.dismiss();

        Log.i("CARS FRAGMENT", "Size of cars list " + carList.size());


    }

    @Override
    public void showNoCarsPlaceholder() {

        if (progress.isShowing())
            progress.dismiss();

        noDataView.setVisibility(View.VISIBLE);


    }

    @Override
    public boolean isActive() {
        return isAdded();
    }


}
