package com.example.assignment;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class Loader{
    private Activity activity;
    private AlertDialog loaderDialog;

    Loader(Activity myAct){
        activity=myAct;
    }

    void startLoading(){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
            LayoutInflater inflator=activity.getLayoutInflater();
            builder.setView(inflator.inflate(R.layout.custom_loading, null));
            builder.setCancelable(true);
            loaderDialog=builder.create();
            loaderDialog.show();

    }
    void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
            }
        }).start();
    }

    void stopLoading(){
        setTimeout(()->loaderDialog.dismiss(), 1000);
    }
}
