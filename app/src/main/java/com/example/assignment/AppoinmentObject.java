package com.example.assignment;

public class AppoinmentObject {
    String tutorId;
    String date;
    String time;

    public AppoinmentObject(String id, String date, String time){
        this.tutorId=id;
        this.date=date;
        this.time=time;
    }

    public String getTutorId(){
        return tutorId;
    }

    public String getDate(){
        return  date;
    }

    public String getTime(){
        return time;
    }
}
