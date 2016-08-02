package com.mowares.massagerexpressclient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;

import com.mowares.massagerexpressclient.utils.AppLog;
import com.mowares.massagerexpressclient.utils.Const;

/**
 * Created by mobilefirst on 6/7/16.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AppLog.Log(Const.TAG, intent.getExtras() + "");
        String message = intent.getExtras().getString("message");
        String team = intent.getExtras().getString("team");
        AppLog.Log("Notificaton", message);
        AppLog.Log("Team", team);
        String title = intent.getExtras().getString("title");
        Intent pushIntent = new Intent(Const.INTENT_WALKER_STATUS);
        pushIntent.putExtra(Const.EXTRA_WALKER_STATUS, team);
        CommonUtilities.displayMessage(context, message);
        // notifies user
        if (!TextUtils.isEmpty(message))
            generateNewNotification(context, message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(pushIntent);
    }

    private void generateNewNotification(Context context, String message) {
        Intent notificationIntent = new Intent(context, ChooseYourNok.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(res.getString(R.string.app_name))
                .setWhen(System.currentTimeMillis()).setAutoCancel(true)
                .setContentTitle(res.getString(R.string.app_name))
                .setContentText(message);
        Uri uri = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);
        Notification n = builder.build();

        nm.notify(Const.NOTIFICATION_ID++, n);
    }
}
