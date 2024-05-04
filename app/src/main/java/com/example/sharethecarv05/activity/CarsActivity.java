package com.example.sharethecarv05.activity;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sharethecarv05.user.Parent;
import com.example.sharethecarv05.permission.PermissionManager;
import com.example.sharethecarv05.R;
import com.example.sharethecarv05.user.User;
import com.example.sharethecarv05.adapter.CarAdapter;
import com.example.sharethecarv05.car.CarManager;

public class CarsActivity extends AppCompatActivity {

    User user;
    CarAdapter carAdapter;
    ListView listView;
    TextView textviewWelcomeMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_times);
        //מקבל מידה מהאינתנת
        Intent intent =getIntent();
        user = (User) intent.getSerializableExtra("bili");
        //מציג את המידה ברשימה על המסך
        carAdapter = new CarAdapter(this, R.layout.car, R.id.carListViewLayout, CarManager.getCarsArray(PermissionManager.GetUserCars(user)));
        listView =findViewById(R.id.lvTimes);
        listView.setAdapter(carAdapter);
        //מקשיב ללחיצות על אחד האברים ברשימה והולך למסך של הרחב
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CarsActivity.this, CarActivity.class);
                intent.putExtra("bili", user);
                intent.putExtra("broooooom",CarManager.getCarsArray(PermissionManager.GetUserCars(user)).get(position));
                startActivity(intent);
            }
        });

        textviewWelcomeMessage = (TextView) findViewById(R.id.textviewWelcomeMessage);
        //במיקרה שהרשימה רקה זמ מציג הורעות למישתמש
        if(CarManager.getCarsArray(PermissionManager.GetUserCars(user)).isEmpty()){
            if(user.getClass()== Parent.class)
                textviewWelcomeMessage.setText("Use the menu to add new cars");//empty list parent
            else
                textviewWelcomeMessage.setText("Ask your parent to register their cars and give you permission to them");//empty list kid
        }
        else {
            if(user.getClass()==Parent.class)
                textviewWelcomeMessage.setText("");//not empty list parent
            else
                textviewWelcomeMessage.setText("");//not empty list kid
        }
    }
    //מציג את הרשימה המתעימה אים זה לילד או הורה
    @Override
        public boolean onCreateOptionsMenu(Menu menu){
        if(user.getClass()==Parent.class)
            getMenuInflater().inflate(R.menu.prent_main_menu,menu);
        else
            getMenuInflater().inflate(R.menu.kid_menu,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        //מחזיר את המישתמש למסך הקניסה
        if(R.id.logout == id){
            Intent intent = new Intent(CarsActivity.this,MainActivity.class);
            startActivity(intent);
        }
        //מעביר למסך עריחת הרחבים
        if(R.id.edit_car == id){
            Intent intent = new Intent(CarsActivity.this, EditCarActivity.class);
            intent.putExtra("bili", user);
            startActivity(intent);
        }//מעביר למסך של יצירת רחב חדש
        if(R.id.new_car == id){
            Intent intent = new Intent(CarsActivity.this, NewCarActivity.class);
            intent.putExtra("bili", user);
            startActivity(intent);
        }
        return true;
    }
}
