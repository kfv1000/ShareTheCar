package com.example.sharethecarv05.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharethecarv05.R;
import com.example.sharethecarv05.car.Car;

import java.util.List;

public class CarAdapter extends ArrayAdapter {

    private Context context;

    private View view;

    private LayoutInflater layoutInflater;

    private TextView carModelView;
    private TextView carNumView;

    private Car carObj;

    private List<Car> carsLst;
    public CarAdapter(Context adapterContext, int xmlFileResource, int layoutResource,
                      List<Car> carsLst) {
        super(adapterContext, xmlFileResource, layoutResource, carsLst);

        this.context = adapterContext;
        this.carsLst = carsLst;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        this.layoutInflater = ((Activity)this.context).getLayoutInflater();

        this.view = layoutInflater.inflate(R.layout.car, parent, false);
        //בקבל את המקומות שהנתונים היו מוצגים
        this.carModelView = (TextView)view.findViewById(R.id.carModel);
        this.carNumView = (TextView)view.findViewById(R.id.carNum);
        //מקבל את האיבר במיקים הנוחחי
        this.carObj = carsLst.get(position);

        //מציג את הנתונים
        this.carModelView.setText(carObj.getModel());
        this.carNumView.setText(carObj.getId());

        return this.view;
    }
}
