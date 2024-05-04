package com.example.sharethecarv05.user;

import com.example.sharethecarv05.permission.PermissionManager;
import com.example.sharethecarv05.car.Car;
import com.example.sharethecarv05.car.CarManager;

import java.io.Serializable;

public class Parent extends User implements Serializable {


    public Parent(String username,String password){
        super(username,password,"Parent");
    }
    public Parent(User user){
        super(user.getUsername(),user.getPassword(),"Parent");
    }
    public Parent(){}
    //פעולה שאוסה את הדברים הנחוצים ליצירת רכב חדש
    public Boolean CreateCar(String model, String id){
        //מנסה ליצור את הרחב
        if(CarManager.setNewCar(new Car(id,model))) {
            //נותן למישתמש שיצר את הרכב הרשאת גישה
            PermissionManager.SetNewPermission(username,id);
            return true;
        }
        return false;
    }
}
