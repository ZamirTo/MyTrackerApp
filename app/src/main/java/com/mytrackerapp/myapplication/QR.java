package com.mytrackerapp.myapplication;

public class QR {
    private String id;
    private String cordinate1;
    private String cordinate2;

    public QR(){}
    public QR(String _id, String _crod1, String _cord2){
        this.id = _id;
        this.cordinate1 = _crod1;
        this.cordinate2 = _cord2;
    }

    public void setCordinates(String _cord1, String _cord2){
        this.cordinate1 = _cord1;
        this.cordinate2 = _cord2;
    }

    public void setID(String _id){
        this.id = _id;
    }

    public String getCordinate1(){
        return this.cordinate1;
    }

    public String getCordinate2(){
        return this.cordinate2;
    }

    public String getID(){
        return this.id;
    }
}
