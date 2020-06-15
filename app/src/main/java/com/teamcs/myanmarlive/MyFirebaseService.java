package com.teamcs.myanmarlive;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.content.Context;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MyFirebaseService extends  FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        Log.e("NEW_TOKEN", s);
        //FirebaseMessaging.getInstance().subscribeToTopic("all");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);
        Log.e("JSON_OBJECT", object.toString());
        String str_title = "မြန်မာ တီဗွီ ရဲ့ အစီအစဉ် အလန်းများ";
        String str_content = "ဒီနေ့ ဘာအစီ အစဉ် အသစ်တွေ ရှိနေလဲ ဆိုတာ သိပြီးပြီလား";
        String str_text = "မြန်မာနိုင်ငံရဲ့ ထိပ်တန်း TV Channel တွေ ဖြစ်တဲ့\n" +
                "Channel K, Mahar တို့မှာ ဘယ်လို အစီအစဉ်ကောင်းတွေ ရှိနေလဲ ဆိုတာသိဖို့ အခုပဲ ဖွင့်ကြည့်လိုက်ပါျ";
        String str_image_url = "https://i.ibb.co/rG8Z1p7/team-cs.gif";
        String str_app_url = "https://play.google.com/store/apps/details?id=com.teamcs.hellosayawon";
        try {
            str_title = object.getString("title");
            str_content = object.getString("content");
            str_text = object.getString("text");
            str_image_url = object.getString("image_url");
            str_app_url = object.getString("app_url");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("parse fail ", e.toString());
        }
        // do all the data operation
        // saving to local file
        String NOTIFICATION_CHANNEL_ID = "TEAM_CS_CHANNEL";
        long pattern[] = {0, 1000, 500, 1000};
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Your Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(str_title);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(pattern);
            notificationChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        // to diaplay notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = mNotificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
            channel.canBypassDnd();
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setContentTitle(str_title)
                .setContentText(str_content)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_tick)
                .setAutoCancel(true);

        // Create pending intent, mention the Activity which needs to be
        //triggered when user clicks on notification(StopScript.class in this case)

//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
         //       new Intent(this, FullscreenActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);


        Intent intent = new Intent(this, FullscreenActivity.class);
        intent.putExtra("title", str_title);
        intent.putExtra("content", str_content);
        intent.putExtra("text", str_text);
        intent.putExtra("image_url", str_image_url);
        intent.putExtra("app_url", str_app_url);
        intent.setAction("fcm"); //just to make it unique from the next one
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder.setContentIntent(pIntent);

        mNotificationManager.notify(NotificationID.getID(), notificationBuilder.build());
    }
}
