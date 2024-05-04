package com.example.sharethecarv05.car;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CarManager {
    public static DatabaseReference myCarRef;
    public static HashMap<String, Car> hscars;
    public CarManager(){
        //מקבל רפרנס וכורה למחוניות
        myCarRef = FirebaseDatabase.getInstance().getReference("Cars");
        hscars = GetHashMApOfCarsFromDB();
    }
    //פעולה שמחזירה את כול המחוניות שמופיאות בפאיר ביס
    public static HashMap<String, Car> GetHashMApOfCarsFromDB(){
        myCarRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, Car>> t = new GenericTypeIndicator<HashMap<String, Car>>() {
                };
                hscars = (HashMap<String, Car>) dataSnapshot.getValue(t);

                //return null;
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("MyReadWriteDb", "Failed to read value.", error.toException());

            }
        });
        return hscars;
    }
    //מוסיף מחונית
    public static Boolean setNewCar(Car car){
        //בודק שאין רחב אים אותו מספר ומוסיך אים לא
        if(hscars.get(car.getId())==null){
            hscars.put(car.getId(),car);
            myCarRef.child(car.getId()).setValue(car);
            return true;
        }
        else
            return false;
    }//מעדקין את הנתונים של מחונית
    public static void UpdateCar(String temp,String model){
        myCarRef.child(temp).child("model").setValue(model);
        hscars = GetHashMApOfCarsFromDB();
    }

    //מקבל רשימה של מספר רחב ומחזיר רשימה של הרחבים אים אותם מספרי רכב
    public static ArrayList<Car> getCarsArray(ArrayList<String> carIds){
        ArrayList<Car> cars = new ArrayList<>();
        for (int i = 0; i < carIds.size(); i++) {
            if(hscars.get(carIds.get(i))!=null){
                if(hscars.get(carIds.get(i))!=null)
                    cars.add(hscars.get(carIds.get(i)));
            }
        }
        return cars;
    }
    //מוחק את הרחב
    public static void DeleteCar(Car car){
        hscars.remove(car.getId());
        myCarRef.setValue(hscars);
    }

    public static void SetBluetooth(String number,String bluetooth){
        hscars.get(number).setBluetooth(bluetooth);
        myCarRef.setValue(hscars);
    }
}
