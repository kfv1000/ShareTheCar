package com.example.sharethecarv05.permission;

public class CarUser {
    String userName;
    String carId;

    public CarUser(String userName, String carNum) {
        this.userName = userName;
        this.carId = carNum;
    }
    public CarUser(){

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }
}
