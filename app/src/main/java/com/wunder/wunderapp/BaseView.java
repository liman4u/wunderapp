package com.wunder.wunderapp;

public interface BaseView<T extends BasePresenter> {


    void setPresenter(T presenter);


}
