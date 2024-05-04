package com.example.sharethecarv05.permission;

import android.util.Log;

import com.example.sharethecarv05.user.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class PermissionManager {
    public static ArrayList<CarUser> carUsers;
    public static DatabaseReference myPermissionRef;

    public PermissionManager() {
        carUsers = new ArrayList<>();
        myPermissionRef = FirebaseDatabase.getInstance().getReference("Permissions");
        carUsers = GetHashMApOfPermissionsFromDB();
    }
    //מקבל רשימה של ההרשעות
    public static ArrayList<CarUser> GetHashMApOfPermissionsFromDB()
    {
        myPermissionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<CarUser>> t = new GenericTypeIndicator<ArrayList<CarUser>>() {
                };
                carUsers = (ArrayList<CarUser>) dataSnapshot.getValue(t);
                //return null;
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("MyReadWriteDb", "Failed to read value.", error.toException());
            }
        });
        return carUsers;
    }
    //הפעטלה מקבלת מישתמש ומחזירה את כול הרכבים שיש לו גישה אלהם
    public static ArrayList<String> GetUserCars(@NotNull User user){
        ArrayList<String> carIds = new ArrayList<>();
        if(carUsers==null)//בודק שיש ביחלל הרשעות
            return null;
        //עובר על כול ההרשאות ובודק אים השם על ההרשעה והשם שך המישתמש שווים ומחניס אותם לרשימה ניפרדת
        for (int i = 0; i < carUsers.size(); i++) {
            if(carUsers.get(i).getUserName().equals(user.getUsername()))
                carIds.add(carUsers.get(i).getCarId());
        }
        return carIds;
    }//מוסיף הרשעה חדשה
    public static void SetNewPermission(String userName , String id){
        carUsers.add(new CarUser(userName,id));
        myPermissionRef.setValue(carUsers);
    }
    //מבי את כול הענשים שיש להם גישה לרחב חוץ מהמישתמש שנתנו לו
    public static ArrayList<String> GetCarUsersWifawtUser(String carId,String userName){
        ArrayList<String> userNames = new ArrayList<>();
        for (int i = 0; i < carUsers.size(); i++) {
            if(carUsers.get(i).getCarId().equals(carId))
                if (!carUsers.get(i).getUserName().equals(userName))
                    userNames.add(carUsers.get(i).getUserName());
        }
        return userNames;
    }
    //נותן את כול האנשים אים הרשאות לרחב
    public static ArrayList<String> GetCarUsers(String carId){
        ArrayList<String> userNames = new ArrayList<>();
        for (int i = 0; i < carUsers.size(); i++) {
            if(carUsers.get(i).getCarId().equals(carId))
                userNames.add(carUsers.get(i).getUserName());
        }
        return userNames;
    }
    //בודק אים למישתמש יש הרשעה לשימוש ברכב
    public static Boolean UserHasCarPermission(String userName,String carId){
        for (int i = 0; i < carUsers.size(); i++) {
            if(carUsers.get(i).getUserName().equals(userName)&&carUsers.get(i).getCarId().equals(carId))
                return true;
        }
        return false;
    }
    //מסיר הרשעה של בנשים לרכב מסוים
    public static void RemovePermission(String userName,String carId){
        for (int i = 0; i < carUsers.size(); i++) {
            if(carUsers.get(i).getUserName().equals(userName)&&carUsers.get(i).getCarId().equals(carId)){
                carUsers.remove(i);
                myPermissionRef.setValue(carUsers);
            }
        }
    }
    //מסיר את כול ההרשעות שיש לכולם לרכב
    //מישתמשים בזה כשמוחרים את הרכב
    public static void RemovePermission(String carId){
        for (int i = 0; i < carUsers.size(); i++) {
            if(carUsers.get(i).getCarId().equals(carId)){
                carUsers.remove(i);
                myPermissionRef.setValue(carUsers);
            }
        }
    }

}
