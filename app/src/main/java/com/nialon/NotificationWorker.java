package com.nialon;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String txt = getInputData().getString("textNotif");

        showNotification(txt);
        //Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG).show();
        return Result.success();
    }

    private void showNotification(String txtNotif) {
        //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(getApplicationContext(), Lunoid.class);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        //If on Oreo then notification required a notification channel.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.drawable.lunoid30white)
                .setContentTitle(getApplicationContext().getResources().getString(R.string.rappel))
                .setContentText(txtNotif)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultIntent);
        notificationManager.notify(1000, notifBuilder.build());
    }
}