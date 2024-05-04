package com.example.sharethecarv05.user;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UserManager {
    public static DatabaseReference myPersonRef;

    public  static HashMap<String, User> hsusers = new HashMap<>() ;

    public static String activeUser;
    public UserManager(){
        myPersonRef = FirebaseDatabase.getInstance().getReference("Users");
        hsusers = GetHashMApOfPersonsFromDB();
    }
    //שומר את המישתמש בישביל ההתרעות
    public static void setActiveUser(String au){
        activeUser=au;
    }
    //מוסיף מישתמש חדש
    public static Boolean setNewUser(User user){
        //בודר ששם המישתמש אוד לא קים
        if(hsusers.get(user.getUsername()) == null){
            //מוסיף את המישתמש החדש למפה הפנימית ולפיאר ביס
            hsusers.put(user.getUsername(), user);
            myPersonRef.child(user.getUsername()).setValue(user);
            return true;
        }
        else
            return false;
    }
    public static HashMap<String, User> GetHashMApOfPersons()
    {
        return hsusers;
    }
    //מחזיר מפה של כול המישתמשים
    public static HashMap<String, User> GetHashMApOfPersonsFromDB() {
        myPersonRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, User>> t = new GenericTypeIndicator<HashMap<String, User>>() {
                };
                Log.w("MyReadWriteDb", "YOUUUUU");
                hsusers = (HashMap<String, User>) dataSnapshot.getValue(t);
                //return null;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("MyReadWriteDb", "Failed to read value.", error.toException());

            }
        });
        return hsusers;
    }
    //מחזיר מישתמש אחרי שאפחו אותו להורה או ילד
    public static User getUser(String user){
        if(hsusers.get(user)==null)
            return null;
        if(hsusers.get(user).getType().equals("Kid")){
            return new Kid(hsusers.get(user));}
        else{
            return new Parent(hsusers.get(user));}
    }
    //מעדקן את המישתמש
    public static void Update(@NonNull User user){
        //hspersons.remove(p.getUsername());
        hsusers.put(user.getUsername(),user);
        myPersonRef.child(user.getUsername()).setValue(user);
    }
    //מחזיר רשימה של כול המישתמשים שחניסו את השמות שלהם
    public static ArrayList<User> GetUsers(ArrayList<String> userNames){
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < userNames.size(); i++) {
            if(hsusers.get(userNames.get(i))!=null){
                if(hsusers.get(userNames.get(i))!=null)
                    users.add(hsusers.get(userNames.get(i)));
            }
        }
        return users;
    }
}
