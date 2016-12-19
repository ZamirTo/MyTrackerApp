package com.mytrackerapp.myapplication;

public class newBLEModel {
    private String name;
    private boolean checked = false;
    private String cordinates;

    public newBLEModel(){}
    public newBLEModel(String _name){
        this.name = _name;
    }
    public newBLEModel(String name, boolean check){
        this.name = name;
        this.checked = check;
    }
    public newBLEModel(String name, String cordinates, boolean check){
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
}
