package net.emittam.isswatcher;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by emittam on 15/04/12.
 */
public class PushBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification =
                new Notification(android.R.drawable.btn_default, "最新の通知情報が届きました", System.currentTimeMillis());
        Intent newIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, newIntent, 0);
        notification.setLatestEventInfo(context.getApplicationContext(), "ISSが上空を通過します", "写真が取れるかも-", contentIntent);
        notificationManager.cancelAll();
        notificationManager.notify(R.string.app_name, notification);
    }
}
