package innovations.tcs.com.mlcpuser;

/**
 * Created by 1115394 on 11/17/2016.
 */
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import innovations.tcs.com.mlcpuser.AsyncTasks.ParkingInfo;
import innovations.tcs.com.mlcpuser.AsyncTasks.ParkingStats;
import innovations.tcs.com.mlcpuser.Beans.ParkingInfoBean;
import innovations.tcs.com.mlcpuser.Beans.ParkingStatsBean;
import innovations.tcs.com.mlcpuser.Beans.ParkingStatsBean2;
import innovations.tcs.com.mlcpuser.RecyclerAdapters.ParkingInfoRecyclerAdapter;
import innovations.tcs.com.mlcpuser.RecyclerAdapters.ParkingStatsRecyclerAdapter;
import innovations.tcs.com.mlcpuser.Utilities.ConnectionDetector;

/**
 * Created by PaulTR on 5/26/14.
 */
public class GeoFencingService extends IntentService {

    private static final String TAG = GeoFencingService.class.getSimpleName();

    private static final int URL_TYPE_INFO = 1;
    private static final int URL_TYPE_STATUS = 2;
    private static final int CONVERSATION_ID_SLOTS = 41;
    private static final int CONVERSATION_ID_HOURS = 42;

    ConnectionDetector cd;

    private NotificationManager mNotificationManager;
    String MY_VOICE_REPLY_KEY = "voice_reply_key";

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

        cd = new ConnectionDetector(getApplicationContext());

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        Log.i("TAG", "onHandleIntent");
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this );
        builder.setSmallIcon( R.mipmap.ic_launcher );
        builder.setDefaults( Notification.DEFAULT_ALL );
        builder.setOngoing( false );

        int transitionType = geofencingEvent.getGeofenceTransition();
        if( transitionType == Geofence.GEOFENCE_TRANSITION_ENTER ) {
            builder.setContentTitle( "Geofence Transition" );
            builder.setContentText( "Entering Geofence" );
            mNotificationManager.notify( 1, builder.build() );

            /** Checking Internet Connection **/
            if (checkInternetConnection()) {
                sendRequest(URL_TYPE_STATUS);
                sendRequest(URL_TYPE_INFO);
            }
        }
        else if( transitionType == Geofence.GEOFENCE_TRANSITION_EXIT ) {
            builder.setContentTitle( "Geofence Transition" );
            builder.setContentText( "Exiting Geofence" );
            mNotificationManager.notify( 1, builder.build() );
        }


    }

    public boolean checkInternetConnection() {
        if (!cd.isConnectingToInternet()) {
            Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    private void generateNotification(String message, String conversationName, int conversationId){

        //Log.d("My Tag","Message : "+ message);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        Intent msgHeardIntent = new Intent()
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction("innovations.tcs.com.mlcpuser.MY_ACTION_MESSAGE_HEARD")
                .putExtra("conversation_id", conversationId);

        PendingIntent msgHeardPendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(),
                        conversationId,
                        msgHeardIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


//        sendNotification(message);

        Intent msgReplyIntent = new Intent()
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction("innovations.tcs.com.mlcpuser.MY_ACTION_MESSAGE_REPLY")
                .putExtra("conversation_id", conversationId);

        PendingIntent msgReplyPendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                conversationId,
                msgReplyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Build a RemoteInput for receiving voice input in a Car Notification
        RemoteInput remoteInput = new RemoteInput.Builder(MY_VOICE_REPLY_KEY)
                .setLabel(getApplicationContext().getString(R.string.notification_reply))
                .build();
//
        // Create an unread conversation object to organize a group of messages
        // from a particular sender.
        NotificationCompat.CarExtender.UnreadConversation.Builder unreadConvBuilder =
                new NotificationCompat.CarExtender.UnreadConversation.Builder(conversationName)
                        .setReadPendingIntent(msgHeardPendingIntent)
                        .setReplyAction(msgReplyPendingIntent, remoteInput);

        unreadConvBuilder.addMessage(message)
                .setLatestTimestamp(System.currentTimeMillis());

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(conversationName)
                        .setContentText(message)
                        .setGroup("GROUP")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setGroupSummary(false)
                        .setContentIntent(msgHeardPendingIntent)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setSound(soundUri);

        notificationBuilder.extend(new NotificationCompat.CarExtender()
                .setUnreadConversation(unreadConvBuilder.build()));

        NotificationManagerCompat msgNotificationManager =
                NotificationManagerCompat.from(this);
        msgNotificationManager.notify("tag",
                conversationId, notificationBuilder.build());
    }

    private void sendRequest(final int urlType){
        String jsonUrl = generateUrl(urlType);

        StringRequest stringRequest = new StringRequest(jsonUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String message = null;

                        switch (urlType){
                            case URL_TYPE_STATUS:
                                ParkingStatsBean2 bean = parseJSONStatus(response);
                                if (bean != null) {
                                    message = bean.getAvailable() + " slots are currently available out of " + bean.getTotal() + " slots.";
                                    generateNotification(message, "MLCP", CONVERSATION_ID_SLOTS);
                                }
                                break;

                            case URL_TYPE_INFO:
                                ArrayList<ParkingInfoBean> beanArrayList = parseJSONInfo(response);
                                if (beanArrayList != null) {
                                    message = "Yesterdays busiest hours were " + beanArrayList.get(0).getParkingInfoName() + ", and today's predicted busiest hours are " + beanArrayList.get(1).getParkingInfoName();
                                    generateNotification(message, "MLCP", CONVERSATION_ID_HOURS);
                                }
                                break;

                            default:
                                break;
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: " + error.getMessage() + " | " + error.getStackTrace() + " | " + error.getCause());
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private ParkingStatsBean2 parseJSONStatus(String json){
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray obj1 = obj.getJSONArray("values");
            JSONObject temp = obj1.getJSONObject(0);

            ParkingStatsBean2 bean = new ParkingStatsBean2(temp.getString("available"), temp.getString("total"));

            return bean;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<ParkingInfoBean> parseJSONInfo(String json){
        try {
                JSONObject obj = new JSONObject(json);
                JSONArray obj1 = obj.getJSONArray("values");
                JSONObject temp = obj1.getJSONObject(0);

                Integer slots[] = new Integer[9];

                slots[0] = Integer.parseInt(temp.optString("slot7A"));
                slots[1] = Integer.parseInt(temp.optString("slot7B"));
                slots[2] = Integer.parseInt(temp.optString("slot7C"));
                slots[3] = Integer.parseInt(temp.optString("slot7D"));
                slots[4] = Integer.parseInt(temp.optString("slot7E"));
                slots[5] = Integer.parseInt(temp.optString("slot7F"));
                slots[6] = Integer.parseInt(temp.optString("slot7G"));
                slots[7] = Integer.parseInt(temp.optString("slot7H"));
                slots[8] = Integer.parseInt(temp.optString("slot7I"));

                int largest = slots[0], index = 0;

                for (int i = 1; i < 9; i++) {
                    if (slots[i] >= largest) {
                        largest = slots[i];
                        index = i;
                    }
                }

                String yesterdaysBusiestHrs = "No Records Yet.";

                if (index == 0) {
                    yesterdaysBusiestHrs = "7:00 am - 7:30 am";
                } else if (index == 1) {
                    yesterdaysBusiestHrs = "7:31 am - 8:00 am";
                } else if (index == 2) {
                    yesterdaysBusiestHrs = "8:01 am - 8:30 am";
                } else if (index == 3) {
                    yesterdaysBusiestHrs = "8:31 am - 9:00 am";
                } else if (index == 4) {
                    yesterdaysBusiestHrs = "9:01 am - 9:30 am";
                } else if (index == 5) {
                    yesterdaysBusiestHrs = "9:31 am - 10:00 am";
                } else if (index == 6) {
                    yesterdaysBusiestHrs = "10:01 am - 10:30 am";
                } else if (index == 7) {
                    yesterdaysBusiestHrs = "10:31 am - 11:00 am";
                } else if (index == 8) {
                    yesterdaysBusiestHrs = "11:01 am - 11:30 am";
                }

                ParkingInfoBean pib;
                ArrayList<ParkingInfoBean> parkingInfoBeansList = new ArrayList<>();
                pib = new ParkingInfoBean("1", yesterdaysBusiestHrs);
                parkingInfoBeansList.add(pib);

                slots[0] = slots[0]
                        + Integer.parseInt(temp.optString("slot6A"))
                        + Integer.parseInt(temp.optString("slot5A"))
                        + Integer.parseInt(temp.optString("slot4A"))
                        + Integer.parseInt(temp.optString("slot3A"))
                        + Integer.parseInt(temp.optString("slot2A"))
                        + Integer.parseInt(temp.optString("slot1A"));

                slots[1] = slots[1]
                        + Integer.parseInt(temp.optString("slot6B"))
                        + Integer.parseInt(temp.optString("slot5B"))
                        + Integer.parseInt(temp.optString("slot4B"))
                        + Integer.parseInt(temp.optString("slot3B"))
                        + Integer.parseInt(temp.optString("slot2B"))
                        + Integer.parseInt(temp.optString("slot1B"));

                slots[2] = slots[2]
                        + Integer.parseInt(temp.optString("slot6C"))
                        + Integer.parseInt(temp.optString("slot5C"))
                        + Integer.parseInt(temp.optString("slot4C"))
                        + Integer.parseInt(temp.optString("slot3C"))
                        + Integer.parseInt(temp.optString("slot2C"))
                        + Integer.parseInt(temp.optString("slot1C"));

                slots[3] = slots[3]
                        + Integer.parseInt(temp.optString("slot6D"))
                        + Integer.parseInt(temp.optString("slot5D"))
                        + Integer.parseInt(temp.optString("slot4D"))
                        + Integer.parseInt(temp.optString("slot3D"))
                        + Integer.parseInt(temp.optString("slot2D"))
                        + Integer.parseInt(temp.optString("slot1D"));

                slots[4] = slots[4]
                        + Integer.parseInt(temp.optString("slot6E"))
                        + Integer.parseInt(temp.optString("slot5E"))
                        + Integer.parseInt(temp.optString("slot4E"))
                        + Integer.parseInt(temp.optString("slot3E"))
                        + Integer.parseInt(temp.optString("slot2E"))
                        + Integer.parseInt(temp.optString("slot1E"));

                slots[5] = slots[5]
                        + Integer.parseInt(temp.optString("slot6F"))
                        + Integer.parseInt(temp.optString("slot5F"))
                        + Integer.parseInt(temp.optString("slot4F"))
                        + Integer.parseInt(temp.optString("slot3F"))
                        + Integer.parseInt(temp.optString("slot2F"))
                        + Integer.parseInt(temp.optString("slot1F"));

                slots[6] = slots[6]
                        + Integer.parseInt(temp.optString("slot6G"))
                        + Integer.parseInt(temp.optString("slot5G"))
                        + Integer.parseInt(temp.optString("slot4G"))
                        + Integer.parseInt(temp.optString("slot3G"))
                        + Integer.parseInt(temp.optString("slot2G"))
                        + Integer.parseInt(temp.optString("slot1G"));

                slots[7] = slots[7]
                        + Integer.parseInt(temp.optString("slot6H"))
                        + Integer.parseInt(temp.optString("slot5H"))
                        + Integer.parseInt(temp.optString("slot4H"))
                        + Integer.parseInt(temp.optString("slot3H"))
                        + Integer.parseInt(temp.optString("slot2H"))
                        + Integer.parseInt(temp.optString("slot1H"));

                slots[8] = slots[8]
                        + Integer.parseInt(temp.optString("slot6I"))
                        + Integer.parseInt(temp.optString("slot5I"))
                        + Integer.parseInt(temp.optString("slot4I"))
                        + Integer.parseInt(temp.optString("slot3I"))
                        + Integer.parseInt(temp.optString("slot2I"))
                        + Integer.parseInt(temp.optString("slot1I"));

                largest = slots[0];
                index = 0;

                for (int i = 1; i < 9; i++) {
                    if (slots[i] >= largest) {
                        largest = slots[i];
                        index = i;
                    }
                }

                String todaysBusiestHrs = "No Records Yet.";

                if (index == 0) {
                    todaysBusiestHrs = "7:00 am - 7:30 am";
                } else if (index == 1) {
                    todaysBusiestHrs = "7:31 am - 8:00 am";
                } else if (index == 2) {
                    todaysBusiestHrs = "8:01 am - 8:30 am";
                } else if (index == 3) {
                    todaysBusiestHrs = "8:31 am - 9:00 am";
                } else if (index == 4) {
                    todaysBusiestHrs = "9:01 am - 9:30 am";
                } else if (index == 5) {
                    todaysBusiestHrs = "9:31 am - 10:00 am";
                } else if (index == 6) {
                    todaysBusiestHrs = "10:01 am - 10:30 am";
                } else if (index == 7) {
                    todaysBusiestHrs = "10:31 am - 11:00 am";
                } else if (index == 8) {
                    todaysBusiestHrs = "11:01 am - 11:30 am";
                }

                pib = new ParkingInfoBean("2", todaysBusiestHrs);
                parkingInfoBeansList.add(pib);

            return parkingInfoBeansList;

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }

        return null;
    }

    private String generateUrl(int urlType){
        String link;
        String url = "Invalid Url";

        switch (urlType){
            case URL_TYPE_STATUS:
                link = "http://www.mymlcp.co.in/mlcpapp/?tag=GetAvailableSlots";
                Log.d("myTag", link);
                url = new String(link.trim().replace(" ", "xyzzyspoonshift1"));
                break;

            case URL_TYPE_INFO:
                link = "http://mymlcp.co.in/mlcpapp/?tag=GetParkingStatus";
                Log.d("myTag", link);
                url = new String(link.trim().replace(" ", "xyzzyspoonshift1"));
                break;

            default:
                url = "Invalid Url";
                break;
        }

        Log.i(TAG, "Schedule URL: " + url);

        return url;
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