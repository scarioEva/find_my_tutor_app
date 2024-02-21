package com.example.assignment;

import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        getFirebaseMessage(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());

//        if (remoteMessage.getData().size() > 0) {
//            Log.d("FCM Message", "Message data payload: " +
//                    remoteMessage.getData());
//            // Handle data payload
//        }
//        if (remoteMessage.getNotification() != null) {
//            Log.d("FCM Message", "Message Notification Body: " +
//                    remoteMessage.getNotification().getBody());
//            //
//
//            handleNotification(remoteMessage);
//
//        }
    }
//    private void handleNotification(RemoteMessage remoteMessage) {
//        NotificationCompat.Builder builder = new
//                NotificationCompat.Builder(getApplicationContext(), "firebase")
//                .setSmallIcon(R.drawable.home)
//                .setContentTitle(remoteMessage.getNotification().getTitle())
//                .setContentText(remoteMessage.getNotification().getBody())
//                .setPriority(NotificationCompat.PRIORITY_HIGH);
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        if (ActivityCompat.checkSelfPermission(this,
//                android.Manifest.permission.POST_NOTIFICATIONS) !=
//                PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        notificationManager.notify(1, builder.build());
//    }

    private void getFirebaseMessage(String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "firebase")
                .setSmallIcon(R.drawable.home)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true);
        Log.d("MainAct", "notification title: " + title);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        managerCompat.notify(102, builder.build());
    }
}
