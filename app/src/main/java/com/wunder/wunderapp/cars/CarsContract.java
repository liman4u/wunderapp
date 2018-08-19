package com.wunder.wunderapp.cars;

import com.wunder.wunderapp.BasePresenter;
import com.wunder.wunderapp.BaseView;
import com.wunder.wunderapp.database.Car;

import java.util.List;

public interface CarsContract {


    interface View extends BaseView<Presenter> {


        void setLoadingIndicator(boolean active);

        void showLoadingCarsError();

        void showCarList(List<Car> carList);

        void showNoCarsPlaceholder();


        boolean isActive();

    }


    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadCars(boolean forceUpdate);


    }
}
