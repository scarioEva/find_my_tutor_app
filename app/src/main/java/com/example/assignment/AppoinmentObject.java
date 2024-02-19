package com.example.assignment;

public class AppoinmentObject {
    String uid;
    String date;
    String time;
    String docId;

    public AppoinmentObject(String id, String date, String time, String docId){
        this.uid=id;
        this.date=date;
        this.time=time;
        this.docId=docId;
    }


    public String getUid(){
        return uid;
    }

    public String getDocId(){return docId;}

    public String getDate(){
        return  date;
    }

    public String getTime(){
        return time;
    }
}
