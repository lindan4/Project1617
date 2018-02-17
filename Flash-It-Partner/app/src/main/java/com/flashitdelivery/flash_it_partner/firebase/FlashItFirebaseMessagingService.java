package com.flashitdelivery.flash_it_partner.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

import com.flashitdelivery.flash_it_partner.R;
import com.flashitdelivery.flash_it_partner.activity.MainActivity;
import com.flashitdelivery.flash_it_partner.activity.SimpleCore;
import com.flashitdelivery.flash_it_partner.event.NotificationEvent;
import com.flashitdelivery.flash_it_partner.util.NotificationEventHandler;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/**
 * Created by yon on 07/07/16.
 */
public class FlashItFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        String from = remoteMessage.getFrom();
        Map<String,String> data = remoteMessage.getData();
        EventBus.getDefault().post(new NotificationEvent(from, data));
        showNotification(remoteMessage.getData().get("TITLE"), remoteMessage.getData().get("MESSAGE"));
    }


    private void showNotification(String title, String msg){
        //Creating a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Intent intent = new Intent(this, SimpleCore.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle(title);
        builder.setContentText(msg);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}
