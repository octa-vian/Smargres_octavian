package id.net.gmedia.semargres2019.Util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import id.net.gmedia.semargres2019.Kuis.KuisActivity;
import id.net.gmedia.semargres2019.Kupon.KuponActivity;
import id.net.gmedia.semargres2019.LoginActivity;
import id.net.gmedia.semargres2019.PromoEvent.PromoDetailActivity;
import id.net.gmedia.semargres2019.R;

public class AppFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        AppSharedPreferences.setFcmId(this, s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(Constant.TAG, "onMessageReceivedFrom: " + remoteMessage.getFrom());

        String title = "Semargres 2019";
        String body = "anda mendapat notifikasi";
        String type = "";

        if(remoteMessage.getData().size() > 0){
            Log.d(Constant.TAG, "Data : " + remoteMessage.getData());
            body = remoteMessage.getData().get("message");
            type = remoteMessage.getData().get("type");
        }

        if(remoteMessage.getNotification() != null){
            Log.d(Constant.TAG, "NotifTitle: " + remoteMessage.getNotification().getTitle());
            Log.d(Constant.TAG, "NotifBody : " + remoteMessage.getNotification().getBody());
            Log.d(Constant.TAG, "NotifTag : " + remoteMessage.getNotification().getTag());

            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
            //sendNotification(remoteMessage.getNotification().getTitle(),
            // remoteMessage.getNotification().getBody(), new HashMap<String, String>(extra));
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = getResources().getString(R.string.app_channel_id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Semargres 2019",
                            NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Semargres 2019");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Semargres 2019")
                //     .setPriority(Notification.PRIORITY_MAX)
                /*.setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setContentInfo(remoteMessage.getNotification().getTag());*/
                .setContentTitle(title)
                .setContentText(body);
        //.setContentInfo(remoteMessage.getData().get("key_1"));

        /*Intent backIntent = new Intent(this, MainActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
        Intent notificationIntent;
        if(!AppSharedPreferences.isLoggedIn(this)){
            notificationIntent = new Intent(this, LoginActivity.class);
        }
        else if("quiz".equals(type)){
            /*notificationIntent = new Intent(this, KuisActivity.class);*/
            notificationIntent = new Intent(this, KuisActivity.class);
            notificationIntent.putExtra(Constant.EXTRA_START_KUIS, false);
        }
        else if(remoteMessage.getData() != null && remoteMessage.getData().containsKey("id_promo")){
            /*notificationIntent = new Intent(this, KuponActivity.class);*/
            notificationIntent = new Intent(this, PromoDetailActivity.class);
            notificationIntent.putExtra(Constant.EXTRA_PROMO_ID, remoteMessage.getData().get("id_promo"));
        }
        else{
            notificationIntent = new Intent(this, KuponActivity.class);
        }

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity
                (this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);
        notificationManager.notify(1, notificationBuilder.build());
    }
}