package com.example.sharethecarv05.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sharethecarv05.permission.PermissionManager;
import com.example.sharethecarv05.R;
import com.example.sharethecarv05.user.User;
import com.example.sharethecarv05.car.Car;
import com.example.sharethecarv05.adapter.CarAdapter;
import com.example.sharethecarv05.car.CarManager;

public class EditCarActivity extends AppCompatActivity implements View.OnClickListener {
    Dialog dialog;
    Button btnSCar;
    Button btnCancel;
    User user;
    ListView listView;
    CarAdapter carAdapter;
    Car temp;
    EditText carModel;
    Intent intent;
    TextView carNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_car);
        //מקבל את המידה מהאינתנת
        Intent intent =getIntent();
        user = (User) intent.getSerializableExtra("bili");

        //שומר מידה מפעיל מקשיבי לחיצה ופותאך רת דיאלוג
        btnCancel=(Button) findViewById(R.id.btnCancelC);
        btnCancel.setOnClickListener(this);
        createpickerDialog();
        carNum = (TextView) findViewById(R.id.carNum);
        carModel = (EditText) findViewById(R.id.carModel);
        btnSCar = (Button) findViewById(R.id.btnSetC);
        btnSCar.setOnClickListener(this);

    }
    //הפעולה שפותחת את הדיאלוג
    public void createpickerDialog()
    {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.car_selecter_dialog);
        //d.setTitle("pick a car");
        listView = dialog.findViewById(R.id.carList);
        carAdapter = new CarAdapter(this, R.layout.car, R.id.carListViewLayout, CarManager.getCarsArray(PermissionManager.GetUserCars(user)));
        listView.setAdapter(carAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                temp=CarManager.getCarsArray(PermissionManager.GetUserCars(user)).get(position);
                dialog.dismiss();
                carNum.setText(temp.getId());
                carModel.setText(temp.getModel());
            }
        });
        dialog.setCancelable(true);

        dialog.show();
    }

    @Override
    public void onClick(View view) {
        // שומר את השינועים ומחזיר אותחה למסך של המכוניות
        if(view==btnSCar){
            CarManager.UpdateCar(temp.getId(),carModel.getText().toString());
            intent = new Intent(EditCarActivity.this , CarsActivity.class);
            intent.putExtra("bili", user);
            startActivity(intent);
        }//לא שומר את השינועים ומחזיר אותחה למסך של המחוניות
        else if(view==btnCancel) {
            intent = new Intent(EditCarActivity.this , CarsActivity.class);
            intent.putExtra("bili", user);
            startActivity(intent);


        }
    }
}