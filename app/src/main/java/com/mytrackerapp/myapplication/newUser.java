package com.mytrackerapp.myapplication;

public class newUser{
    private String name;
    private String email;
    private String location;

    public newUser(){}

    public newUser(String _name, String _email, String _location){
        this.name = _name;
        this.email = _email;
        this.location = _location;
    }

    void setName(String _name){
        name = _name;
    }

    void setEmail(String _email){
        email = _email;
    }

    void setLocation(String _loc){
        location = _loc;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getLocation(){
        return location;
    }
}