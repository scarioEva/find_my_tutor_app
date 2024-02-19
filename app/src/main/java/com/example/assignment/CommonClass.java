package com.example.assignment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommonClass {
    public String generateRandomString(int n) {
        StringBuilder stringBuilder = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            // generate a random number between
            // 0 to AlphaNumericString variable length
            String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
            int index = (int) (alphaNumericString.length() * Math.random());

            // add Character one by one in end of sb
            stringBuilder.append(alphaNumericString.charAt(index));
        }
        return stringBuilder.toString();
    }

    public String getDateTime(String c_date, String time) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/d/yyyy", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
        Date date = inputFormat.parse(c_date);
        String formattedDate = outputFormat.format(date);

        String final_d = formattedDate + " (" + time + ")";
        return final_d;
    }

    public void setImageView(Context context, String url, ShapeableImageView imageView) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable caching if needed
                        .skipMemoryCache(true)) // Skip memory cache if needed
                .into(imageView);
    }

//public String getCurrentDate(){
//    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//}
    public boolean checkDatePassed(String date, String time) {
        boolean flag = false;
        int dashIndex = time.lastIndexOf('-');
        String lastTime = time.substring(dashIndex + 1).trim();

        String dateTime = date + " " + lastTime;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date targetDateTime = sdf.parse(dateTime);

            Date currentDateTime = new Date();

            flag = currentDateTime.after(targetDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public int getImageOrientation(Uri imageUri, Activity activity) {
        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                ExifInterface exifInterface = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    exifInterface = new ExifInterface(inputStream);
                }
                return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ExifInterface.ORIENTATION_UNDEFINED;
    }

    public Bitmap getRotatedBitmap(Uri imageUri, int orientation, Activity activity) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
            if (orientation != ExifInterface.ORIENTATION_UNDEFINED) {
                Matrix matrix = new Matrix();
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                    default:
                        return bitmap;
                }
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Uri saveImage(Bitmap image, Context context) {
        File imageFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;

        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder, "capture_images.jpg");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context.getApplicationContext(), "com.example.assignment" + ".provider", file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }

    public void sendNotification(String userId, String title, String body, String token) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", title);
            notificationObj.put("body", body);

            JSONObject dataObj = new JSONObject();
            dataObj.put("userId", userId);

            jsonObject.put("notification", notificationObj);
            jsonObject.put("data", dataObj);
            jsonObject.put("to", token);
            Log.d("MainAct", "called api" + userId + title + body);
            callApi(jsonObject);
        } catch (Exception e) {

        }
    }

    private void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json");

        OkHttpClient client = new OkHttpClient();

        String url = "https://fcm.googleapis.com/fcm/send";
//        String url = "https://fcm.googleapis.com/v1/projects/findmytutorapp-dda11/messages:send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAAVUscTIk:APA91bEvHWIZGKlG3rh7H_2WZEcwsQdBw3CKxcV9OhY4z6zS3BdRRqwI7JKBAPs0L7ZmYTL8w0XTgyn6ZhN9s7YUwAmdCBRyFtlu4xOGLlRW7gfbQikVHfQMDATatgixrtJL-SP5OaTK")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("Claa", "fail" + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("Claa", "fail" + response);
            }
        });

    }

}
