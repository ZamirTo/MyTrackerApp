package com.mytrackerapp.myapplication;

public class newUserModel{
    private String name;
    private boolean checked = false;
    private String cordinates;
    private String email;

    public newUserModel(){}
    public newUserModel(String _name){
        this.name = _name;
    }
    public newUserModel(String name, boolean check){
        this.name = name;
        this.checked = check;
    }
    public newUserModel(String name, String cordinates, boolean check, String email){
        this.name = name;
        this.checked = check;
        this.cordinates = cordinates;
        this.email = email;
    }
    public newUserModel(String name, String cordinates, boolean check){
        this.name = name;
        this.checked = check;
        this.cordinates = cordinates;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    public void setCordinates(String _cordinates){
        this.cordinates = _cordinates;
    }
    public boolean isChecked() {
        return checked;
    }
    public String toString() {
        return name ;
    }
    public String getCordinates(){
        return this.cordinates;
    }
    public void toggleChecked() {
        checked = !checked ;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
}