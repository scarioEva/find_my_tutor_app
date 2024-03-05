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

import com.airbnb.lottie.L;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
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

        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
        Date date = inputFormat.parse(c_date);
        String formattedDate = outputFormat.format(date);

        String final_d = formattedDate + " (" + time + ")";
        return final_d;
    }

    public String toTimeStamp(String date, String time) {
        Timestamp timestamp = null;
        try {
            int dashIndex = time.lastIndexOf('-');
            String lastTime = time.substring(dashIndex + 1).trim();
            Log.d("MainAct", time);
            String dateTimeString = date + " " + lastTime + ":00";
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            dateFormat.setLenient(false);

            Date new_date = dateFormat.parse(dateTimeString);
            Log.d("MainAct", "timest:" + new_date);
            long milliseconds = new_date.getTime();
            timestamp = new Timestamp(milliseconds);


        } catch (ParseException e) {
            Log.d("MainAct", e.toString());
        }
        return timestamp.toString();
    }

    public void setImageView(Context context, String url, ShapeableImageView imageView) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                .into(imageView);
    }

    public boolean timeValidate(String time1, String time2) {
        boolean flag;

        int hour1 = Integer.parseInt(time1.split(":")[0]);
        int minute1 = Integer.parseInt(time1.split(":")[1]);

        int hour2 = Integer.parseInt(time2.split(":")[0]);
        int minute2 = Integer.parseInt(time2.split(":")[1]);

        if (hour1 > hour2 || (hour1 == hour2 && minute1 > minute2)) {
            flag = false;
            System.out.println(time1 + " is greater than " + time2);
        } else if (hour1 < hour2 || (hour1 == hour2 && minute1 < minute2)) {
            flag = true;
            System.out.println(time1 + " is less than " + time2);
        } else {
            flag = false;
            System.out.println(time1 + " is equal to " + time2);
        }
        return flag;
    }

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
        File imgFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;

        try {
            imgFolder.mkdirs();
            File file = new File(imgFolder, "image1.jpg");
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

    public void sendNotification(String userId, String title, String body, String token, String image) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", title);
            notificationObj.put("body", body);
            notificationObj.put("image", image);
//            notificationObj.put("icon", R.mipmap.ic_launcher);
            notificationObj.put("color", "#5d865d");

            JSONObject dataObj = new JSONObject();
            dataObj.put("userId", userId);

            jsonObject.put("notification", notificationObj);
            jsonObject.put("data", dataObj);
            jsonObject.put("to", token);
            Log.d("Claa", "called api" + userId + title + body+" " + image);
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
                Log.d("Claa", "success" + response);
            }
        });
    }

}
