package com.app_republic.dznews.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;

import com.app_republic.dznews.BuildConfig;
import com.app_republic.dznews.R;
import com.app_republic.dznews.activity.MainActivity;
import com.app_republic.dznews.activity.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Map;

import static com.app_republic.dznews.activity.SplashActivity.SETTINGS_TITLE;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            if (remoteMessage.getData().get("type") != null
                    && remoteMessage.getData().get("type").equals("change_settings")) {
                SharedPreferences.Editor editor = getSharedPreferences(SETTINGS_TITLE, MODE_PRIVATE).edit();
                SharedPreferences.Editor adsEditor = getSharedPreferences("custom_ads", MODE_PRIVATE).edit();

                adsEditor.clear();
                adsEditor.commit();

                editor.remove("last_read");
                editor.commit();
            }
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }


        sendNotification(remoteMessage.getData());



        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]

        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param data
     */
    private void sendNotification(Map<String, String> data) {
        Intent intent;
        if (data.get("type") != null) {

            if (data.get("type").equals("version")) {
                intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id="
                                + BuildConfig.APPLICATION_ID));
            } else if (data.get("type").equals("ad_app")) {
                intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id="
                                + data.get("app_id")));
            } else if (data.get("type").equals("ad_url")) {
                intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(data.get("url")));
            }  else if (data.get("type").equals("news")) {
                intent = new Intent(this, SplashActivity.class);
                intent.putExtra("article", data.get("article"));

            } else {
                intent = new Intent(this, SplashActivity.class);
            }
        } else
            intent = new Intent(this, MainActivity.class);


        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), data.get("body").hashCode() /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setContentTitle(getString(R.string.app_name))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText(data.get("body"))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("notifications", MODE_PRIVATE);

        if (!data.get("type").equals("news") || (data.get("type").equals("news") &&
                sharedPreferences.getBoolean("enabled", true))) {
            if (data.get("image") != null) {


                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmap));
                        notificationManager.notify(data.get("body").hashCode() /* ID of notification */, notificationBuilder.build());

                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        notificationManager.notify(data.get("body").hashCode() /* ID of notification */, notificationBuilder.build());
                    }


                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                };
                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(() -> Picasso.get().load(data.get("image")).into(target));

            } else {
                notificationManager.notify(data.get("body").hashCode() /* ID of notification */, notificationBuilder.build());

            }
        }



    }
}