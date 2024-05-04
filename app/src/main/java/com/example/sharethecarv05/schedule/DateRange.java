package com.example.sharethecarv05.schedule;

import java.io.Serializable;
import java.util.Date;

public class DateRange implements Serializable {
    Date dateStart;
    Date dateEnd;

    Integer day;
    Integer month;
    Integer year;

    public DateRange(Integer year, Integer month, Integer day, Integer dSh, Integer dSm, Integer dEh, Integer dEm) {
        this.day = day;
        this.month = month;
        this.year = year;
        dateStart = new Date(year, month, day, dSh, dSm);
        dateEnd = new Date(year, month, day, dEh, dEm);
    }

    public DateRange() {

    }

    public Boolean overlap(DateRange d) {
        if(dateStart.after(d.getDateEnd()) || dateEnd.before(d.getDateStart()))
            return false;
        return true;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public Date getDateEnd() {

        return dateEnd;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getTimeForSort(){
        Integer num = 0;
        num += dateStart.getYear();
        num *= 100;
        num += dateStart.getMonth();
        num *= 100;
        num += dateStart.getDay();
        num *= 100;
        num += dateStart.getHours();
        num *= 100;
        num += dateStart.getMinutes();
        return num;
    }
    public String StartTimeToString(){
        if (dateStart.getMinutes()<10)
            return dateStart.getHours()+":0"+dateStart.getMinutes();
        else
            return dateStart.getHours()+":"+dateStart.getMinutes();
    }
    public String EndTimeToString(){

        if (dateEnd.getMinutes()<10)
            return dateEnd.getHours()+":0"+dateEnd.getMinutes();
        else
            return dateEnd.getHours()+":"+dateEnd.getMinutes();
    }
    public String DateToString(){
        return day.toString()+"/"+month.toString()+"/"+year.toString();
    }
}
