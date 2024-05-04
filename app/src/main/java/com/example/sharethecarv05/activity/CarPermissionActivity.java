package com.example.sharethecarv05.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharethecarv05.permission.PermissionManager;
import com.example.sharethecarv05.R;
import com.example.sharethecarv05.user.User;
import com.example.sharethecarv05.adapter.UserAdapter;
import com.example.sharethecarv05.user.UserManager;
import com.example.sharethecarv05.car.Car;

public class CarPermissionActivity extends AppCompatActivity implements View.OnClickListener {
    ListView listView;
    Button btnAddUser;
    Button btnBack;
    EditText editTextUser;
    UserAdapter userAdapter;
    User user;
    Car car;
    String u;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_permission);
//מקבל את המידה מהאינתנת
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("bili");
        car = (Car) intent.getSerializableExtra("broooooom");
//מוצה את האברים ושומר אותם לשימוש
        listView = findViewById(R.id.listViewUsers);
        btnAddUser = findViewById(R.id.btnAddUser);
        editTextUser = findViewById(R.id.editTextUser);
        btnBack = findViewById(R.id.btnBack);
        title = findViewById(R.id.title);
        title.setText("Users that are allowed to use the "+car.getModel()+" car");

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        PermissionManager.RemovePermission(u,car.getId());
                        refresListView();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        //מציג את הרשימה של הענשים אים גישה לרחב
        Context context = this;
        userAdapter = new UserAdapter(this, R.layout.user, R.id.carListViewLayout, UserManager.GetUsers(PermissionManager.GetCarUsersWifawtUser(car.getId(), user.getUsername())));
        listView.setAdapter(userAdapter);
        //מקשיב ללחיצות על האצמים
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                u = PermissionManager.GetCarUsersWifawtUser(car.getId(), user.getUsername()).get(position);
                builder.setMessage("Are you sure you want to delete "+u+"'s permission to use this car?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        //מפעיל הקשבה על לחיצת כפתורים
        btnAddUser.setOnClickListener(this);
        btnBack.setOnClickListener(this);


    }
    //מעפס את הרשימה
    public void refresListView(){
        userAdapter = new UserAdapter(this, R.layout.user, R.id.carListViewLayout, UserManager.GetUsers(PermissionManager.GetCarUsersWifawtUser(car.getId(), user.getUsername())));
        listView.setAdapter(userAdapter);
    }
    @Override
    public void onClick(View view) {
        if(view == btnAddUser){
            String userName = editTextUser.getText().toString();
            User PermissionUser = UserManager.getUser(userName);
            //בודקר שהמישתמש באמת קים
            if(PermissionUser == null){
                Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show();
            }//בודק אים למישתמש גבר יש גישה לרחב
            else if (PermissionManager.UserHasCarPermission(userName,car.getId())) {
                Toast.makeText(this, "User already has permission", Toast.LENGTH_SHORT).show();
            }//מוסיף למישתמש את הגישה לרחב
            else{
                PermissionManager.SetNewPermission(userName,car.getId());
                userAdapter = new UserAdapter(this, R.layout.user, R.id.carListViewLayout, UserManager.GetUsers(PermissionManager.GetCarUsersWifawtUser(car.getId(), user.getUsername())));
                listView.setAdapter(userAdapter);
            }
        }//מחזיר אותחה אחורה למסך של הרחב
        if(view == btnBack){
            Intent intent = new Intent(CarPermissionActivity.this, CarActivity.class);
            intent.putExtra("bili", user);
            intent.putExtra("broooooom",car);
            startActivity(intent);
        }
    }
}