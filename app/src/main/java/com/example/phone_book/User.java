package com.example.phone_book;

import android.net.Uri;

public class User {
    public String id,name ,number;
    public  int flag;
    public static String searchString = "";
    public  String url;
    public  User(){}
    public  User (String id, String name, String  number, int flag, String url){
        this.id= id;
        this.name= name;
        this.number=number;
        this.flag=flag;
        this.url=url;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int isFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
