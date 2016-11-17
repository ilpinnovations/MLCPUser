package innovations.tcs.com.mlcpuser;

/**
 * Created by 1115394 on 11/17/2016.
 */
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by PaulTR on 5/26/14.
 */
public class GeoFencingService extends IntentService {

    private NotificationManager mNotificationManager;

    public GeoFencingService(String name) {
        super(name);
    }

    public GeoFencingService() {
        this( "Geofencing Service" );
    }

    @Override
    public IBinder onBind(Intent intent ) {
        return null;
    }

    @Override
    protected void onHandleIntent( Intent intent ) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        Log.i("TAG", "onHandleIntent");
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this );
        builder.setSmallIcon( R.mipmap.ic_launcher );
        builder.setDefaults( Notification.DEFAULT_ALL );
        builder.setOngoing( true );

        int transitionType = geofencingEvent.getGeofenceTransition();
        if( transitionType == Geofence.GEOFENCE_TRANSITION_ENTER ) {
            builder.setContentTitle( "Geofence Transition" );
            builder.setContentText( "Entering Geofence" );
            mNotificationManager.notify( 1, builder.build() );
        }
        else if( transitionType == Geofence.GEOFENCE_TRANSITION_EXIT ) {
            builder.setContentTitle( "Geofence Transition" );
            builder.setContentText( "Exiting Geofence" );
            mNotificationManager.notify( 1, builder.build() );
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TAG", "onCreate Service");
        mNotificationManager = (NotificationManager) getSystemService( NOTIFICATION_SERVICE );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("TAG", "onStartCommand");
        onHandleIntent( intent );
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNotificationManager.cancel( 1 );
    }
}