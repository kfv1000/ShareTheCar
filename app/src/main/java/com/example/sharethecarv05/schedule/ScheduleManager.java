package com.example.sharethecarv05.schedule;

import android.util.Log;

import com.example.sharethecarv05.car.Car;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ScheduleManager {
    static ArrayList<Entry> entries;
    static DatabaseReference myScheduleRef;
    public ScheduleManager(){
        myScheduleRef = FirebaseDatabase.getInstance().getReference("Schedule");
        entries = GetHashMApOfEntrysFromDB();
    }
    //הפעולה נותנת את כול הזמני שימוש הקבועים
    public static ArrayList<Entry> GetHashMApOfEntrysFromDB()
    {
        myScheduleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Entry>> t = new GenericTypeIndicator<ArrayList<Entry>>() {
                };
                Log.w("MyReadWriteDb", "Entry");
                entries = (ArrayList<Entry>) dataSnapshot.getValue(t);
                //return null;
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("MyReadWriteDb", "Failed to read value.", error.toException());

            }
        });
        return entries;
    }
    //הפעולה הזות בודקת אים למישתמש יש גבר זמן קבוע באותו זמן
    public static Boolean PersonTimeOverLapChecker(String userName, DateRange dateRange){
        for (int i = 0; i < entries.size(); i++) {
            if(entries.get(i).getDateRange().overlap(dateRange) && entries.get(i).getUserName().equals(userName))
                return true;
        }
        return false;
    }
    //בודק אים לרחב הזה יש גבר זמן קבוע אים הזמן שניחנס
    public static Boolean CarTimeOverLapChecker(String carId, DateRange dateRange){
        for (int i = 0; i < entries.size(); i++) {
            if(entries.get(i).getDateRange().overlap(dateRange) && entries.get(i).getCarId().equals(carId))
                return true;
        }
        return false;
    }
    //מוסיף זמן חדש אחרי בדיקה שלא רץ עז זמן אחר
    public static Integer AddEntry(String userName,String carId,DateRange dateRange){
        if (PersonTimeOverLapChecker(userName,dateRange))
            return 1;//1 == you olredy slekted a time on a car
        else if(CarTimeOverLapChecker(carId, dateRange))
            return 2;//2 == time in use bay ater user
        else{
            entries.add(new Entry(userName,carId,dateRange));
            myScheduleRef.setValue(entries);
            return 0;//0 == added suksesfuly
        }
    }
    //פעולה מוחקת את הזמן שגבר קים ומחניסה את הזמן הארוח במקום
    public static Integer EditEntry(String userName,String carId,DateRange dateRange,Entry entry) {
        DeleteEntry(entry);
        if (PersonTimeOverLapChecker(userName, dateRange)) {
            entries.add(entry);
            return 1;//1 == you olredy slekted a time on a car
        } else if (CarTimeOverLapChecker(carId, dateRange)){
            entries.add(entry);
            return 2;//2 == time in use bay ater user
        }
        else{
            entries.add(new Entry(userName,carId,dateRange));
            myScheduleRef.setValue(entries);
            return 0;//0 == added suksesfuly
        }
    }
    //מוחק זמן שימוש ברכב
    public static void DeleteEntry(Entry entry){
        for (int i = 0; i < entries.size(); i++) {
            if(entries.get(i)==entry){
                entries.remove(i);
                break;
            }
        }
    }
    public static ArrayList<Entry> GetDateRangesForCar(String carId){
        ArrayList<Entry> entries1 = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            if(entries.get(i).getCarId().equals(carId))
                entries1.add(entries.get(i));
        }
        return SortEntryArray(entries1);
    }
    public static ArrayList<Entry> GetDateRangesForUser(String userName){
        ArrayList<Entry> entries1 = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            if(entries.get(i).getUserName().equals(userName))
                entries1.add(entries.get(i));
        }
        return SortEntryArray(entries1);
    }
    public static ArrayList<Entry> SortEntryArray(ArrayList<Entry> entries){
        int n = entries.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (entries.get(j).getDateRange().getTimeForSort() > entries.get(j + 1).getDateRange().getTimeForSort()) {
                    // Swapping elements if they are in the wrong order
                    Entry temp = entries.get(j);
                    entries.set(j, entries.get(j + 1));
                    entries.set(j + 1, temp);
                }
            }
        }
        return entries;
    }
    //מוחק את כול הזמנים של מישתמש לרכב מסוים
    public static void DeleteUsersEntries(String userName,String carId){
        for (int i = 0; i < entries.size(); i++) {
            if(entries.get(i).getUserName().equals(userName)&&entries.get(i).getCarId().equals(carId))
                entries.remove(i);
        }
        myScheduleRef.setValue(entries);
    }
    //מוחק את כול הזמנים של רכב מסוים לכול המישתמשים
    public static void DeleteCarEntries(Car car){
        for (int i = 0; i < entries.size(); i++) {
            if(entries.get(i).getCarId().equals(car.getId()))
                entries.remove(i);
        }
        myScheduleRef.setValue(entries);
    }
}
