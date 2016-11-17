package innovations.tcs.com.mlcpuser.GCM;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import innovations.tcs.com.mlcpuser.Activities.MainActivity;
import innovations.tcs.com.mlcpuser.R;
import innovations.tcs.com.mlcpuser.Utilities.Constants;

public class GcmIntentService extends GcmListenerService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MY TAG";
    private GoogleApiClient mGoogleApiClient;
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mGoogleApiClient.connect();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        mGoogleApiClient.connect();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {

        String message = data.getString("message");
        //Log.d("My Tag","Message : "+ message);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);


        final NotificationCompat.Builder phoneNotificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("MLCP")
                .setContentText(message)
                .setGroup("GROUP")
                .setGroupSummary(false);

        phoneNotificationBuilder.setContentIntent(intent);
        phoneNotificationBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        phoneNotificationBuilder.setSound(soundUri);
        notificationManager.notify(NOTIFICATION_ID, phoneNotificationBuilder.build());

        sendNotification(message);


    }


    private void sendNotification(String message) {

        if (mGoogleApiClient.isConnected()) {
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(Constants.NOTIFICATION_PATH);
            dataMapRequest.getDataMap().putDouble(Constants.NOTIFICATION_TIMESTAMP, System.currentTimeMillis());
            dataMapRequest.getDataMap().putString(Constants.NOTIFICATION_TITLE, "MLCP");
            dataMapRequest.getDataMap().putString(Constants.NOTIFICATION_CONTENT, message);
            PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
            Log.d("My TAG", "DATA Sent to wearable");
        } else {
            Log.d("My Tag", "Error");
            try {
                Thread.sleep(1000);
                sendNotification(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("My Tag", "Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("My Tag", "Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("My Tag", "Connection failed");
    }
}