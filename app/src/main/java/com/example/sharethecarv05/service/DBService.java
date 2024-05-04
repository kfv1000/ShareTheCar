package com.example.sharethecarv05.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.sharethecarv05.permission.CarUser;
import com.example.sharethecarv05.R;
import com.example.sharethecarv05.activity.MainActivity;
import com.example.sharethecarv05.user.UserManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

public class DBService extends Service {

    DatabaseReference myRef;
    FirebaseDatabase database;

    boolean first = true;
    public DBService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Permissions");

        //read single - קריאה חד פעמית של מה שרוצים לבדוק, למשל את הקאונטר של הגודל
        // אם הקראונטר למשל שונה ממספר הקודם, אז להציג הודעה
        myRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                GenericTypeIndicator<CarUser> t = new GenericTypeIndicator<CarUser>() {
                };
                CarUser carUser = (CarUser) snapshot.getValue(t);
                if(carUser.getUserName().equals(UserManager.activeUser))
                    alert("New Permission",
                            "You have been granted a permission to use car number "+carUser.getCarId());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<CarUser> t = new GenericTypeIndicator<CarUser>() {
                };
                CarUser carUser = (CarUser) snapshot.getValue(t);
                if(carUser.getUserName().equals(UserManager.activeUser))
                    alert("Removed Permission",
                            "Your permission has been removed from car number "+carUser.getCarId());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
    //פעולה להתרעות
    public void alert(String title, String text){
        if(first){}
        else {
            int NOTIFICATION_ID = 234;

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            String CHANNEL_ID = "SherTheCar";

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                CharSequence name = "Hi";

                String Description = "blablabla";

                int importance = NotificationManager.IMPORTANCE_HIGH;

                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                mChannel.setDescription(Description);

                mChannel.enableLights(true);

                mChannel.setLightColor(Color.RED);

                mChannel.enableVibration(true);

                mChannel.setShowBadge(false);

                notificationManager.createNotificationChannel(mChannel);
            }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)

                        .setSmallIcon(R.mipmap.ic_launcher)

                        .setContentTitle(title)

                        .setContentText(text);

                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());

                stackBuilder.addParentStack(MainActivity.class);

                stackBuilder.addNextIntent(resultIntent);

                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_MUTABLE);

                builder.setContentIntent(resultPendingIntent);

                notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

}