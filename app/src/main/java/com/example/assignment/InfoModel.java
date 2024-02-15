package com.example.assignment;

public class InfoModel {
    private String name;
    private String bio;
    private String department;
    private String profileUrl;

    public InfoModel(String name, String bio,String department,String profileUrl){
        this.name=name;
        this.bio=bio;
        this.department=department;
        this.profileUrl=profileUrl;

    }

    public String getName(){
        return name;
    }

    public String getBio(){
       return bio;
    }

    public String getDepartment(){
        return  department;
    }

    public String getProfileUrl(){
        return profileUrl;
    }
}
