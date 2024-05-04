package com.example.sharethecarv05.service;

import static com.example.sharethecarv05.activity.MainActivity.MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.sharethecarv05.activity.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;

public class BluetoothBackgroundService extends Service {

    private BluetoothAdapter bluetoothAdapter;
    private String connectedDeviceName;
    private boolean isBluetoothConnected;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient locationClient;
    private Location lastKnownLocation;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // meters
    private Timer locationSaveTimer;
    private LocationRequest locationRequest;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
        }
        // Register for broadcasts when a device is connected
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(bluetoothConnectionReceiver, filter);

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        createLocationRequest();
        buildLocationCallback();

        startForegroundService();

        databaseReference = FirebaseDatabase.getInstance().getReference("BluetoothDevices");

        // Initialize your Bluetooth and location logic here
        return START_STICKY; // Ensures service is restarted if killed
    }
    private final BroadcastReceiver bluetoothConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                if (device != null) {
                    isBTDeviceRegisteredInDB(device, new BluetoothCheckCallback() {
                        @Override
                        public void onResult(boolean isBTDeviceRegisteredInDB) {
                            if (isBTDeviceRegisteredInDB) {
                                // Use service's context to show the toast
                                Toast.makeText(getApplicationContext(), "Bluetooth Device that is registered in the DB has been Connected", Toast.LENGTH_SHORT).show();
                                isBluetoothConnected = true;
                                // Place this code where you need to check the permission before proceeding with Bluetooth operations
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                    // Permission not granted, request it
                                    ActivityCompat.requestPermissions((MainActivity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT);
                                    return; // Stop further execution until permission is granted
                                }
                                // Continue with your Bluetooth operation here if permission is already granted
                                connectedDeviceName = device.getName();
                                startLocationUpdates();
                            } else {
                                // The device is not the car's Bluetooth.
                                // Handle the logic for this case accordingly.
                                stopLocationUpdates();
                            }
                        }
                    });
                }
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                isBTDeviceRegisteredInDB(device, new BluetoothCheckCallback() {
                    @Override
                    public void onResult(boolean isCarBluetooth) {
                        if (isCarBluetooth) {
                            // Use service's context to show the toast
                            Toast.makeText(getApplicationContext(), "Car Bluetooth Disconnected", Toast.LENGTH_SHORT).show();
                            isBluetoothConnected = false;
                            stopLocationUpdates();
                        } else {
                            // The device is not the car's Bluetooth.
                            // Handle the logic for this case accordingly.
                        }
                    }
                });
            }
        }
    };


    private void stopLocationUpdates() {
        locationClient.removeLocationUpdates(locationCallback);

        // Stop the timer
        if (locationSaveTimer != null) {
            locationSaveTimer.cancel();
            locationSaveTimer = null;
        }
    }


    private void startLocationUpdates() {
        if (isBluetoothConnected && hasLocationPermissions()) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */);
            }
        }
    }

    private boolean hasLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public interface BluetoothCheckCallback {
        void onResult(boolean isRegistered);
    }

    public void isBTDeviceRegisteredInDB(BluetoothDevice device, BluetoothCheckCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BluetoothDevices");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        databaseReference.child(device.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If dataSnapshot.exists(), the device is registered in the database
                callback.onResult(dataSnapshot.exists());
                //return null;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Log.e("Firebase", "Database error: " + databaseError.getMessage());
                callback.onResult(false);
            }
        });
    }

    private void startForegroundService() {
        String NOTIFICATION_CHANNEL_ID = "com.volcari.savecarlocation.channel";
        String channelName = "Background Service";
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Check and create the notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            assert manager != null;
            manager.createNotificationChannel(chan);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(60); // 60 seconds interval
        locationRequest.setFastestInterval(30); // 30 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Check if the new location is significantly different from the last known location
                    if (lastKnownLocation == null || location.distanceTo(lastKnownLocation) > MIN_DISTANCE_CHANGE_FOR_UPDATES) {
                        // Update the last known location
                        lastKnownLocation = location;

                        // Send location to Firebase
                        String key = databaseReference.push().getKey();
                        if (key != null) {
                            //databaseReference.child(connectedDeviceName).setValue(location);
                            SetLocation(location,connectedDeviceName);
                        }
                    }
                }
            }
        };
    }
    public void SetLocation(Location location, String c){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("BluetoothDevices");
        ref = ref.child(c);
        Coordinates coordinates = new Coordinates(location.getTime(),location.getLatitude(),location.getLongitude());
        ref.setValue(coordinates);
    }
}