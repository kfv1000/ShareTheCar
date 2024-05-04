package com.example.sharethecarv05.user;

import java.io.Serializable;

public class Kid extends User implements Serializable {
    public Kid(String username,String password){
        super(username,password,"Kid");
    }
    public Kid(User user){
        super(user.getUsername(),user.getPassword(),"Kid");
    }
    public Kid(){}
}
