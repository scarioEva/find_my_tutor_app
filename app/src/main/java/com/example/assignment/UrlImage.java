package com.example.assignment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlImage extends AsyncTask<String, Void, Bitmap> {

    String src;
    ShapeableImageView imgView;

    public UrlImage(String src, ShapeableImageView imgView) {
        this.src = src;
        this.imgView=imgView;
    }


    @Override
    protected void onPostExecute(Bitmap feed) {
        super.onPostExecute(feed);
        imgView.setImageBitmap(feed);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
