package com.example.sharethecarv05.car;

import java.io.Serializable;

public class Car implements Serializable {

    String id;
    String model;
    String bluetooth;

    public Car(String id, String model) {
        this.id = id;
        this.model = model;
    }

    public Car(){

    }


    public void setModel(String model) {
        this.model = model;
    }

    public void setNum(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public String getBluetooth() {
        return bluetooth;
    }
    public void setBluetooth(String bluetooth) {
        this.bluetooth = bluetooth;
    }
}
