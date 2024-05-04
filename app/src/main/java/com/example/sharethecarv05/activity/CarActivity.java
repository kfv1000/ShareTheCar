package com.example.sharethecarv05.activity;

import static com.example.sharethecarv05.activity.MainActivity.MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.sharethecarv05.service.Coordinates;
import com.example.sharethecarv05.schedule.DateRange;
import com.example.sharethecarv05.schedule.Entry;
import com.example.sharethecarv05.adapter.EntryAdapter;
import com.example.sharethecarv05.user.Parent;
import com.example.sharethecarv05.permission.PermissionManager;
import com.example.sharethecarv05.R;
import com.example.sharethecarv05.schedule.ScheduleManager;
import com.example.sharethecarv05.user.User;
import com.example.sharethecarv05.car.Car;
import com.example.sharethecarv05.car.CarManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class CarActivity extends AppCompatActivity implements View.OnClickListener {
    User user;
    Car car;
    TextView carModel;
    TextView carNum;
    Button btnNewTime;
    ListView listView;
    EntryAdapter entryAdapter;
    Button btnDate;
    Button startTime;
    Button endTime;
    Button btnMap;
    ImageButton btnBlue;
    Integer year;
    Integer month;
    Integer day;
    Integer startHour;
    Integer startMinute;
    Integer endHour;
    Integer endMinute;
    Integer editYear;
    Integer editMonth;
    Integer editDay;
    ArrayList<Entry> entries;
    String date;
    Dialog dialog;
    Button btnDialogSaveChanges,editStarTime,editEndTime,editDate;
    Entry edit;
    double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);
        //מקבל את המידה מהאינתנת
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("bili");
        car = (Car) intent.getSerializableExtra("broooooom");
        //מוצה את האברים ושומר אותם לשימוש
        carModel = findViewById(R.id.tvCarModel);
        carNum = findViewById(R.id.tvCarNum);
        btnBlue = findViewById(R.id.btnBlue);
        btnNewTime = findViewById(R.id.btnNewTime);
        listView = findViewById(R.id.lv);
        btnDate = findViewById(R.id.btnDate);
        startTime = findViewById(R.id.btnSTime);
        endTime = findViewById(R.id.btnETime);
        btnMap = findViewById(R.id.btnMap);
        //סם בלמלה של המסך את שם ומיספר הרחב
        carModel.setText(car.getId());
        carNum.setText(car.getModel());
        //מסיג את התעריך הנוחחי ושומר אותו
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        date = day + "/" + month + "/" + year;
        btnDate.setText(date);

        //מעלה את הרשימה של הרחבים למסך
        updateListView();

        //מתחיל את הקשבת הלחיצה על הקפתורים
        btnNewTime.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        btnBlue.setOnClickListener(this);
        btnMap.setOnClickListener(this);


        //מוצה את מצה הטלון מבחינת מצב אור בהיר או חשוך
        int nightModeFlags = CarActivity.this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        //בודק מצב האור ומשנה את הצבעים בהתאם
        if(nightModeFlags == Configuration.UI_MODE_NIGHT_YES){
            btnMap.setBackgroundColor(Color.WHITE);
            btnDate.setBackgroundColor(Color.WHITE);
            btnNewTime.setBackgroundColor(Color.WHITE);
            startTime.setBackgroundColor(Color.WHITE);
            endTime.setBackgroundColor(Color.WHITE);

            btnDate.setTextColor(Color.BLACK);
            btnNewTime.setTextColor(Color.BLACK);
            startTime.setTextColor(Color.BLACK);
            endTime.setTextColor(Color.BLACK);
            btnMap.setTextColor(Color.BLACK);
        }
        else{
            btnDate.setBackgroundColor(Color.BLACK);
            btnNewTime.setBackgroundColor(Color.BLACK);
            startTime.setBackgroundColor(Color.BLACK);
            endTime.setBackgroundColor(Color.BLACK);
            btnMap.setBackgroundColor(Color.BLACK);

            btnDate.setTextColor(Color.WHITE);
            btnNewTime.setTextColor(Color.WHITE);
            startTime.setTextColor(Color.WHITE);
            endTime.setTextColor(Color.WHITE);
            btnMap.setTextColor(Color.WHITE);
        }


    }
    //מעלה את דיאלוג האריחה למסך ומפעיל הקשבות על הקפתורים
    public void createEditEntryDialog(Entry edit)
    {
        this.edit=edit;
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.entry_edit_dialog);
        //d.setTitle("pick a car");
        dialog.setCancelable(true);
        //שומר את הקפתורים
        editStarTime = dialog.findViewById(R.id.btnStarTime);
        editEndTime = dialog.findViewById(R.id.btnEndTime);
        btnDialogSaveChanges= dialog.findViewById(R.id.btnSaveChanges);
        editDate = dialog.findViewById(R.id.btnDate);
        //מפעיל מקשיבי לחיצה על הכפתורים
        btnDialogSaveChanges.setOnClickListener(this);
        editEndTime.setOnClickListener(this);
        editStarTime.setOnClickListener(this);
        editDate.setOnClickListener(this);
        //מציב את המידה בקפתורים ותיבות תקסת
        endTime.setText("Choose end time");
        startTime.setText("Choose start time");
        editDay = edit.getDateRange().getDay();
        editMonth = edit.getDateRange().getMonth();
        editYear = edit.getDateRange().getYear();
        editDate.setText(editDay + "/" + editMonth + "/" + editYear);
        startHour=edit.getDateRange().getDateStart().getHours();
        startMinute=edit.getDateRange().getDateStart().getMinutes();
        endHour=edit.getDateRange().getDateEnd().getHours();
        endMinute=edit.getDateRange().getDateEnd().getMinutes();
        editStarTime.setText(edit.getDateRange().StartTimeToString());
        editEndTime.setText(edit.getDateRange().EndTimeToString());
        //מפעיל את הדיאלוג
        dialog.show();
    }
    //פעולה שנותנת את הזמנים השמורים לפי התעריך ששמור
    public ArrayList<Entry> getDayEntries(){
        //אוסף את התאריך למיספר
        date = day + "/" + month + "/" + year;
        // מקבל את הזמנים של הרחב
        entries = ScheduleManager.GetDateRangesForCar(car.getId());
        ArrayList<Entry> dayEntries = new ArrayList<>();
        //בודק איזה זמנים מתאימים אים התעריך
        for (int i = 0; i < entries.size(); i++){
            String dateRange = entries.get(i).getDateRange().DateToString();
            if(dateRange.equals(date))
                dayEntries.add(entries.get(i));
        }
        return dayEntries;
    }
    //מעפס את רשימת הליסט
    public void updateListView(){
        TextView t =(TextView) findViewById(R.id.mt);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        //בודק אים הרשימה של הזמנים רקה לאותו יום מציג הוראות
        if(getDayEntries().isEmpty()){

            t.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) t.getLayoutParams();
            layoutParams.weight = 360;
            layoutParams.height = height;
            t.setLayoutParams(layoutParams);
        }
        else{
            t.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            entryAdapter = new EntryAdapter(this, R.layout.entry, R.id.carListViewLayout, getDayEntries(),user.getUsername());
            listView.setAdapter(entryAdapter);

            LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams)
                    listView.getLayoutParams();
            layoutParams.weight = 360;
            layoutParams.height = height;
            listView.setLayoutParams(layoutParams);
        }
    }
    //סם את המניו הנכון תלוי אים המישתמש הורה או ילד
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(user.getClass()== Parent.class)
            getMenuInflater().inflate(R.menu.parent_car_menu,menu);
        else
            getMenuInflater().inflate(R.menu.kid_car_menu,menu);
        return true;
    }
    //מקשיב ללחיצות על התפרית
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        //מעביר למסך של ההרשעות
        if (R.id.permission == id) {
            Intent intent = new Intent(CarActivity.this, CarPermissionActivity.class);
            intent.putExtra("bili", user);
            intent.putExtra("broooooom", car);
            startActivity(intent);
        }
        if (R.id.logout == id) {
            Intent intent = new Intent(CarActivity.this, MainActivity.class);
            startActivity(intent);
        }
        //מנתק את הקשר בין המישתמש לרחב
        if (R.id.unlinkCar == id) {
            //מעלה מסך ששואל אים אתה בתואך שאתה רוצה לנתק את הרחב
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                PermissionManager.RemovePermission(user.getUsername(), car.getId());
                                if (PermissionManager.GetCarUsers(car.getId()) == null) {
                                    CarManager.DeleteCar(car);
                                }
                                ScheduleManager.DeleteUsersEntries(user.getUsername(),car.getId());
                                Intent intent = new Intent(CarActivity.this, CarsActivity.class);
                                intent.putExtra("bili", user);
                                startActivity(intent);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to unlink this car from your user?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
        }
        //מוחק את הרחב לגמרה לכול המישתמשים
        if(R.id.deleteCar == id){
            //שועל אם אתה בתואך שאתה רוצה לימחוק את הרחב
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //מוחק את הפרמישנים לרחב
                            PermissionManager.RemovePermission(car.getId());
                            //מוחחק את הרחב
                            CarManager.DeleteCar(car);
                            //מוחק את כול הזמנים של הרחב
                            ScheduleManager.DeleteCarEntries(car);
                            Intent intent = new Intent(CarActivity.this, CarsActivity.class);
                            intent.putExtra("bili", user);
                            startActivity(intent);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to DELETE this car?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
        //מחזיר אותחה למסך של כול המחוניות
        if(R.id.back == id){
            Intent intent = new Intent(CarActivity.this, CarsActivity.class);
            intent.putExtra("bili", user);
            intent.putExtra("broooooom",car);
            startActivity(intent);
        }
        return true;
    }
    @Override
    public void onClick(View view) {
        //מוסיף את הזמנים והתעריך כזמן שימוש ברחב
        if(view == btnNewTime){
            //בודק שלא חסר זמן התחלה או סוף
            if (startHour == null)
                Toast.makeText(this, "Start time is required", Toast.LENGTH_SHORT).show();
            else if (endHour == null)
                Toast.makeText(this, "End time is required", Toast.LENGTH_SHORT).show();
            else{
                //שומר את התעריך והזמנים
                DateRange dateRange = new DateRange(year,month,day,startHour,startMinute,endHour,endMinute);
                //בודק אים אפשר להסים את הזמן הזה ואים כן עז מוסף אותו
                Integer resolt = ScheduleManager.AddEntry(user.getUsername(),car.getId(),dateRange);
                switch (resolt){
                    case 1:{
                        Toast.makeText(this, "This user already has a car booking that intersects with your selected time", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 2:{
                        Toast.makeText(this, "Car is already booked for your selected time", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    //מוחר את הזמנים הקודמים כי גבר הוספו
                    case 0:{
                        updateListView();
                        endTime.setText("Choose end time");
                        startTime.setText("Choose start time");
                        startHour=null;
                        startMinute=null;
                        endHour=null;
                        endMinute=null;
                        break;
                    }
                }
            }
        }
        //מקשיב ללחיצה על קפתורי התעריך ופותאך את בחירת התעריך
        else if (view == btnDate||view == editDate){
            showDatePicker(view);
        }
        //פותאך את מסך בחירת השעה
        else if (startTime == view || endTime == view || editEndTime == view || editStarTime == view)
        {
            final Calendar calendar = Calendar.getInstance();

            int hour;
            int minute;
            // on below line we are getting our hour, minute.
            //במיקרה שהמישתמש לא משנה את הזמנים אני גבר פו שומר את הזמנים המקוריאים
            if(editStarTime == view){
                hour = startHour;
                minute = startMinute;
            }
            else if(editEndTime == view){
                hour = endHour;
                minute = endMinute;
            }
            else{
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);
            }

            // on below line we are initializing our Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(CarActivity.this,new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay,
                                      int minute) {
                    // on below line we are setting selected time
                    // in our text view.
                    if(endTime == view){
                        if((startHour != null && startHour > hourOfDay) || (startHour != null && startMinute > minute && startHour == hourOfDay))
                            Toast.makeText(CarActivity.this, "The end time you selected is before the start time", Toast.LENGTH_SHORT).show();
                        else{
                            if(minute<10)
                                endTime.setText(hourOfDay + ":0" + minute);
                            else
                                endTime.setText(hourOfDay + ":" + minute);
                            endHour=hourOfDay;
                            endMinute=minute;
                        }
                    }
                    //שומר את הזמן בכפתור הנכון
                    else if(startTime == view){
                        if((endHour != null && endHour < hourOfDay)|| (endHour != null && endMinute < minute && endHour == hourOfDay))
                            Toast.makeText(CarActivity.this, "The start time you selected is after the end time", Toast.LENGTH_SHORT).show();
                        else{
                            if(minute<10)
                                startTime.setText(hourOfDay + ":0" + minute);
                            else
                                startTime.setText(hourOfDay + ":" + minute);
                            startHour=hourOfDay;
                            startMinute=minute;
                        }
                    }
                    else if(editEndTime == view){
                        if((startHour != null && startHour > hourOfDay) || (startHour != null && startMinute > minute && startHour == hourOfDay))
                            Toast.makeText(CarActivity.this, "The end time you selected is before the start time", Toast.LENGTH_SHORT).show();
                        else{
                            if(minute<10)
                                editEndTime.setText(hourOfDay + ":0" + minute);
                            else
                                editEndTime.setText(hourOfDay + ":" + minute);
                            endHour=hourOfDay;
                            endMinute=minute;
                        }
                    }
                    else{
                        if((endHour != null && endHour < hourOfDay)|| (endHour != null && endMinute < minute && endHour == hourOfDay))
                            Toast.makeText(CarActivity.this, "The start time you selected is after the end time", Toast.LENGTH_SHORT).show();
                        else{
                            if(minute<10)
                                editStarTime.setText(hourOfDay + ":0" + minute);
                            else
                                editStarTime.setText(hourOfDay + ":" + minute);
                            startHour=hourOfDay;
                            startMinute=minute;
                        }
                    }
                }
            }, hour, minute, false);
            // at last we are calling show to
            // display our time picker dialog.
            timePickerDialog.show();
        }
        //שומר את השינועים שהיתבצעו בדיאלוג של האריחה
        else if(btnDialogSaveChanges == view){
            DateRange dateRange = new DateRange(editYear,editMonth,editDay,startHour,startMinute,endHour,endMinute);

            Integer resolt = ScheduleManager.EditEntry(user.getUsername(),car.getId(),dateRange,edit);
            switch (resolt){
                case 1:{
                    Toast.makeText(this, "This user already has a car booking that intersects with your selected time", Toast.LENGTH_SHORT).show();
                    break;
                }
                case 2:{
                    Toast.makeText(this, "Car is already booked for your selected time", Toast.LENGTH_SHORT).show();
                    break;
                }
                case 0:{
                    updateListView();
                    startHour=null;
                    startMinute=null;
                    endHour=null;
                    endMinute=null;
                    break;
                }
            }


            // TODO: 08/01/2024

            dialog.dismiss();
        }
        //מעלה את בחירת מחשירי הבלו טוף
        else if(btnBlue == view){
            showBluetoothDevicesDialog();
        }
        //בודק אים יש בלו טוף ומיקום ומציג את המיקום אים יש
        else if (btnMap == view) {
            if(car.getBluetooth()==null)
                Toast.makeText(this, "No Bluetooth device assigned to this car", Toast.LENGTH_SHORT).show();
            else{
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BluetoothDevices").child(car.getBluetooth());
                ref.child("latitude").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                            latitude = (double) snapshot.getValue();
                        else
                            Log.w("Map","latitude snapsho no no");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                ref.child("longitude").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            longitude = (double) snapshot.getValue();
                            openGoogleMaps();}
                        else
                            Log.w("Map","longitude snapsho no no");
                        Toast.makeText(CarActivity.this, "no location saved", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        }
    }
    //שומר את התאריך המיתקבל לאיחסון
    public void saveDate(Integer year,Integer month,Integer day,String selectedDate){
        btnDate.setText(selectedDate);
        this.year = year;
        this.month = month;
        this.day = day;
    }
    //שומר את התאריך של הדיאלוג של האריחה
    private void saveEditeDate(Integer year,Integer month,Integer day,String selectedDate){
        editDate.setText(selectedDate);
        this.editYear=year;
        this.editMonth=month;
        this.editDay=day;
    }
    //הפעולה שמציגה את בוחר התעריך
    private void showDatePicker(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year;
        int month;
        int day;
        //בודק איזה תעריך להציג
        if(view == btnDate){
            year = this.year;
            month = this.month;
            day = this.day;
        }
        else{
            year = editYear;
            month = editMonth;
            day = editDay;
        }
        View v = view;
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String selectedDate = dayOfMonth + "/" + (monthOfYear) + "/" + year;
                if(v == btnDate)
                    saveDate(year,monthOfYear,dayOfMonth,selectedDate);
                else if(v == editDate)
                    saveEditeDate(year,monthOfYear,dayOfMonth,selectedDate);
                updateListView();
            }
        }, year, month, day);

        datePickerDialog.show();
    }
    //מצתיג את המחשירים שאפשר ליבחור
    private void showBluetoothDevicesDialog() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted. Request the permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT);
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        List<String> deviceList = new ArrayList<>();
        for (BluetoothDevice device : pairedDevices) {
            deviceList.add(device.getName());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(CarActivity.this);
        builder.setTitle("Select a Bluetooth Device");

        String[] devicesArray = deviceList.toArray(new String[0]);
        builder.setItems(devicesArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedDeviceName = deviceList.get(which).split("\n")[0]; // Get device name
                checkAndSaveDevice(selectedDeviceName);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //בודק אים הבלו טופ שניבחר מיקודם לא בשימוש ועז שומר אותו
    private void checkAndSaveDevice(String deviceName) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BluetoothDevices").child(deviceName);
        car.setBluetooth(deviceName);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the device already exists
                if (!dataSnapshot.exists()) {
                    // Device does not exist, save it with value true
                    Coordinates c = new Coordinates(0,0,0);
                    ref.setValue(true);
                    CarManager.SetBluetooth(car.getId(),deviceName);
                } else {
                    // Device already exists, you might want to notify the user or log this event
                    Log.d("BluetoothDevices", "Device already exists: " + deviceName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("BluetoothDevices", "Failed to read value.", databaseError.toException());
            }
        });
    }
    //@Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Add a marker at the specified coordinates and move the camera
        LatLng location = new LatLng(latitude, longitude); // Replace with your desired coordinates
        googleMap.addMarker(new MarkerOptions().position(location).title("Marker Title"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }
    //הצגת המיקום
    private void openGoogleMaps() {
        // Create a Uri with the location coordinates
        Uri gmmIntentUri = Uri.parse("geo:"+latitude+","+longitude+"?q="+latitude+","+longitude+"(Marker+Title)");

        // Create an Intent to launch Google Maps with the specified location
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps"); // Specify the package to ensure it opens in Google Maps

        // Verify that the Google Maps app is available before launching the Intent
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Handle the case where the Google Maps app is not installed
            // You can open the browser or prompt the user to install the app from the Play Store
        }
    }

}
