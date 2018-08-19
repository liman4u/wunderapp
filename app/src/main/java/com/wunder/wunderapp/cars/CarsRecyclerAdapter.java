package com.wunder.wunderapp.cars;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wunder.wunderapp.R;
import com.wunder.wunderapp.database.Car;
import com.wunder.wunderapp.utils.Constants;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class CarsRecyclerAdapter extends RecyclerView.Adapter<CarsRecyclerAdapter.CarViewHolder> {


    Context mContext;
    private List<Car> carArrayList;

    public CarsRecyclerAdapter(Context context, List<Car> dataList) {
        this.carArrayList = dataList;
        this.mContext = context;

    }


    @Override
    public CarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.car_item_view, parent, false);
        return new CarViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CarViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        Car car = carArrayList.get(position);
        checkNotNull(car);

        holder.name.setText(car.getName());
        holder.address.setText(car.getAddress());
        holder.fuel.setText(car.getFuel());


        holder.vin.setText("VIN: " + car.getVin());
        holder.engineType.setText("Engine Type: " + car.getEngineType());


        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(10f);


        @ColorInt int goodColor = ContextCompat.getColor(mContext, R.color.carQualityGood);
        @ColorInt int badColor = ContextCompat.getColor(mContext, R.color.carQualityBad);


        holder.interior.setText(car.getInterior());

        switch (car.getInterior()) {


            case Constants.CAR_QUALITY_GOOD:
                holder.interior.setTextColor(goodColor);
                break;
            case Constants.CAR_QUALITY_BAD:
                holder.interior.setTextColor(badColor);

                break;
        }


        holder.exterior.setText(car.getExterior());

        switch (car.getExterior()) {

            case Constants.CAR_QUALITY_GOOD:
                holder.exterior.setTextColor(goodColor);

                break;
            case Constants.CAR_QUALITY_BAD:
                holder.exterior.setTextColor(badColor);

                break;

        }


        holder.setIsRecyclable(false);


    }

    @Override
    public int getItemCount() {
        return carArrayList.size();
    }

    public List<Car> getCarArrayList() {
        return carArrayList;
    }

    public void setCarArrayList(List<Car> newCarList) {
        carArrayList = newCarList;
        notifyDataSetChanged();

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    class CarViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView address;
        TextView fuel;
        TextView interior;
        TextView exterior;
        TextView vin;
        TextView engineType;


        CarViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            fuel = itemView.findViewById(R.id.fuel);
            interior = itemView.findViewById(R.id.interior);
            engineType = itemView.findViewById(R.id.engine_type);
            exterior = itemView.findViewById(R.id.exterior);
            vin = itemView.findViewById(R.id.vin);


        }
    }
}
