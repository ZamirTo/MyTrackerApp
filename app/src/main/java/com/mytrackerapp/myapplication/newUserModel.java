package com.mytrackerapp.myapplication;

public class newUserModel{
    private String name;
    private boolean checked = false;

    public newUserModel(){}
    public newUserModel(String _name){
        this.name = _name;
    }
    public newUserModel(String name, boolean check){
        this.name = name;
        this.checked = check;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    public String toString() {
        return name ;
    }
    public void toggleChecked() {
        checked = !checked ;
    }
}