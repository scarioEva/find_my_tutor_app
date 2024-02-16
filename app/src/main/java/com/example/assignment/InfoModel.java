package com.example.assignment;

public class InfoModel {
    private String name;
    private String location;
    private String department;
    private String profileUrl;

    public InfoModel(String name, String location,String department,String profileUrl){
        this.name=name;
        this.location=location;
        this.department=department;
        this.profileUrl=profileUrl;

    }

    public String getName(){
        return name;
    }

    public String getLocation(){
       return location;
    }

    public String getDepartment(){
        return  department;
    }

    public String getProfileUrl(){
        return profileUrl;
    }
}
