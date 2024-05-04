package com.example.sharethecarv05.user;

import java.io.Serializable;

public class User implements Serializable {

    String username;
    String password;
    String type;

    public User(String username, String password, String type) {
        this.username = username;
        this.password = password;
        this.type = type;
    }
    public User(){

    }
    //מקבל סיסמה ובודק אים היא הסיסמה הנכונה
    public Boolean isPassword(String password){
        if(this.password.equals(password))
            return true;
        else
            return false;
    }
    public String getUsername(){
        return(username);
    }
    public String getPassword(){
        return password;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}