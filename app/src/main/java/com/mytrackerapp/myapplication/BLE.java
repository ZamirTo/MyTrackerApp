package com.mytrackerapp.myapplication;

public class BLE {
    private String macAddress;
    private String cordinate1;
    private String cordinate2;

    public BLE(){}
    public BLE(String _macAddress, String _crod1, String _cord2){
        this.macAddress = _macAddress;
        this.cordinate1 = _crod1;
        this.cordinate2 = _cord2;
    }

    public void setCordinates(String _cord1, String _cord2){
        this.cordinate1 = _cord1;
        this.cordinate2 = _cord2;
    }

    public void setMacAddress(String _macAddress){
        this.macAddress = _macAddress;
    }

    public String getCordinate1(){
        return this.cordinate1;
    }

    public String getCordinate2(){
        return this.cordinate2;
    }

    public String getMacAddress(){
        return this.macAddress;
    }
}
