package com.example.sharethecarv05.schedule;

public class Entry {
    String userName;
    String carId;
    DateRange dateRange;

    public Entry(String userName, String carId, DateRange dateRange) {
        this.userName = userName;
        this.carId = carId;
        this.dateRange = dateRange;
    }

    public Entry() {

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

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }
}
