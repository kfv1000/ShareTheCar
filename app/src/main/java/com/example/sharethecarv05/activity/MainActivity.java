package com.example.sharethecarv05.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import android.os.Build;

import com.example.sharethecarv05.service.BluetoothBackgroundService;
import com.example.sharethecarv05.permission.CarUser;
import com.example.sharethecarv05.schedule.DateRange;
import com.example.sharethecarv05.schedule.Entry;
import com.example.sharethecarv05.user.Kid;
import com.example.sharethecarv05.service.DBService;
import com.example.sharethecarv05.user.Parent;
import com.example.sharethecarv05.permission.PermissionManager;
import com.example.sharethecarv05.R;
import com.example.sharethecarv05.schedule.ScheduleManager;
import com.example.sharethecarv05.user.User;
import com.example.sharethecarv05.user.UserManager;
import com.example.sharethecarv05.car.Car;
import com.example.sharethecarv05.car.CarManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnLogin;
    Button btnSignUp;
    Button btnDialogSignUp;
    String userName;
    String password;
    String vPasswrod;
    EditText etUserName;
    EditText etPassword;
    EditText etVPasswrod;
    EditText etLoginUserName;
    EditText etLoginPassword;
    RadioButton rbKid;
    Dialog dialog;
    User user;
    ImageView img;

    public DatabaseReference myRef;
    public FirebaseDatabase database;
    public static final int MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //שומר גישה לקפתורים
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        img=findViewById(R.id.img);
        //מפעיל הקשבת לחיצה על קפתורים
        btnLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        //סם איתמים בפאיר ביס ליציבות
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Cars");
        myRef.child("Sup").setValue(new Car("sup","sup"));
        myRef = database.getReference("Users");
        myRef.child("hi").setValue(new Parent("hi","123"));
        myRef = database.getReference("Schedule");
        myRef.child("0").setValue(new Entry("mama mia","mama mia",new DateRange(2024,1,5,17,14,18,14)));
        myRef = database.getReference("Permissions");
        myRef.child("0").setValue(new CarUser("hi","sup"));


        user =new Parent("kfir","69");

        //מפעיל את המנג'ראים
        new UserManager();
        new CarManager();
        new PermissionManager();
        new ScheduleManager();

        UserManager.setNewUser(user);

        etLoginUserName=findViewById(R.id.etUserName);
        etLoginPassword=findViewById(R.id.etPassword);

        Intent intent = new Intent(this, DBService.class);
        startService(intent);
        //מתעים את המסך למצב אור או חושך
        int nightModeFlags = MainActivity.this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if(nightModeFlags == Configuration.UI_MODE_NIGHT_YES){
            img.setImageResource(R.drawable.sharethecard);
            btnLogin.setBackgroundColor(Color.WHITE);
            btnLogin.setTextColor(Color.BLACK);
            btnSignUp.setBackgroundColor(Color.BLACK);
        }
        else{
            img.setImageResource(R.drawable.sharethecar);
            btnLogin.setBackgroundColor(Color.BLACK);
            btnLogin.setTextColor(Color.WHITE);
            btnSignUp.setBackgroundColor(Color.BLACK);
        }
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.POST_NOTIFICATIONS},100);
        //מפעיל את הסרביס
        startService();
    }
    //פותאך את הדיאלוג של ההרשמה
    public void createSignUpDialog()
    {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.sin_up_dialog);
        //d.setTitle("pick a car");
        dialog.setCancelable(true);
        btnDialogSignUp= dialog.findViewById(R.id.btnDialogSignUp);
        btnDialogSignUp.setOnClickListener(this);
        dialog.show();
    }



    @Override
    public void onClick(View view) {
        //בודק אים יש מישתמש והסיסמה נכונה ומחניס את המישתמש
        if(view == btnLogin){
            Intent intent=new Intent(MainActivity.this, CarsActivity.class);
            User user1 = UserManager.getUser(etLoginUserName.getText().toString());
            if(user1==null){
                Toast.makeText(this, "Username does not exist", Toast.LENGTH_SHORT).show();
            }
            else if(user1.isPassword(etLoginPassword.getText().toString())){
                UserManager.setActiveUser(etLoginUserName.getText().toString());
                intent.putExtra("bili",user1);
                startActivity(intent);
            }
            else{
                Toast.makeText(this, "Password is incorrect", Toast.LENGTH_SHORT).show();
            }
        }
        //כורה לפעולה שפותחת את הדיאלוג
        else if(view==btnSignUp){
            createSignUpDialog();
        }
        //יוצר מישתמש לאחר שאוסה בדיקות מתעימות
        else if(view==btnDialogSignUp){
            //מקבל את המידה מהדיאלוג
            etUserName = dialog.findViewById(R.id.etUserName);
            etPassword = dialog.findViewById(R.id.etPassword);
            etVPasswrod = dialog.findViewById(R.id.etVPasswrod);
            userName = etUserName.getText().toString();
            password = etPassword.getText().toString();
            vPasswrod = etVPasswrod.getText().toString();
            rbKid= dialog.findViewById(R.id.rbKid);
            //בודק שלא שחחו למלות תיבת תכסת
            if(userName!=null&&password!=null&&vPasswrod!=null){
                if(password.equals(vPasswrod)) {
                    User user1;
                    if(rbKid.isChecked()){
                        user1 = new Kid(userName, password);
                    }
                    else {
                        user1 = new Parent(userName, password);
                    }
                    if(UserManager.setNewUser(user1)){
                        Toast.makeText(this, userName+" welcome", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                    else
                        Toast.makeText(this, "User name already exists - try a different one", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                if(userName == null) Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                else if (password == null) Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                else if (vPasswrod == null) Toast.makeText(this, "Verify password cannot be empty", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startService() {
        Intent serviceIntent = new Intent(MainActivity.this, BluetoothBackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }


}