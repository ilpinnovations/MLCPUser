package innovations.tcs.com.mlcpuser.Activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import innovations.tcs.com.mlcpuser.AsyncTasks.ParkingAvailable;
import innovations.tcs.com.mlcpuser.Beans.OptionBean;
import innovations.tcs.com.mlcpuser.Databases.DatabaseHandler;
import innovations.tcs.com.mlcpuser.Databases.DatabaseHandlerCarList;
import innovations.tcs.com.mlcpuser.GPSTrackerService;
import innovations.tcs.com.mlcpuser.GeoFencingService;
import innovations.tcs.com.mlcpuser.Interfaces.Communicator;
import innovations.tcs.com.mlcpuser.R;
import innovations.tcs.com.mlcpuser.RecyclerAdapters.MainRecyclerAdapter;
import innovations.tcs.com.mlcpuser.Utilities.ConnectionDetector;
import innovations.tcs.com.mlcpuser.Utilities.Constants;
import innovations.tcs.com.mlcpuser.Utilities.Utils;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity implements Communicator,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    private static final String TAG = MainActivity.class.getSimpleName();

    Toolbar toolbar;
    ActionBar actionBar;
    RecyclerView recyclerView;
    ArrayList<OptionBean> optionBeansList;
    ConnectionDetector cd;
    public static TextView result = null;
    AppCompatDialog dialog;

    private static final int NOTIFICATION_ID = 1;
    String MY_VOICE_REPLY_KEY = "voice_reply_key";

    private Menu menu;
    private static boolean OPTION_FLAG = true;

    // GPSTracker class
    GPSTrackerService gps;
    private final static String FENCE_ID = "com.tcs.innovations.geofence";
    private final int RADIUS = 200;
    private Geofence mGeofence;
    private Intent mIntent;
    private PendingIntent mPendingIntent;

    private GoogleApiClient googleApiClient;

    private final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 0;
    private final int REQUEST_CODE_ALL = 42;
    private static final double MLCP_LATITUDE = 10.010926;
    private static final double MLCP_LONGITUDE = 76.362945;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int hasLocationPermission = checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION );
            List<String> permissions = new ArrayList<String>();
            if( hasLocationPermission != PackageManager.PERMISSION_GRANTED ) {
                permissions.add( Manifest.permission.ACCESS_FINE_LOCATION );
            }

            if( !permissions.isEmpty() ) {
                requestPermissions( permissions.toArray( new String[permissions.size()] ), REQUEST_CODE_SOME_FEATURES_PERMISSIONS );
            }
        }

        verifyPlayServices();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mIntent = new Intent( this, GeoFencingService.class );
        mPendingIntent = PendingIntent.getService( this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT );

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        ViewGroup vg = (ViewGroup) findViewById(R.id.root);
        Utils.setFontAllView(vg);

        cd = new ConnectionDetector(getApplicationContext());
        final DatabaseHandler db = new DatabaseHandler(this);
        recyclerView = (RecyclerView) findViewById(R.id.list_colors);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        if (db.getContactsCount() <= 0) {
            db.close();

            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            toolbar.setSubtitle(db.getAllContacts().get(0).getName());
            if (checkInternetConnection()) {
                listener();
                new ParkingAvailable(MainActivity.this).execute("");
            }
        }

        OptionBean ob;
        optionBeansList = new ArrayList<>();
        ob = new OptionBean("1", "My Slot");
        optionBeansList.add(ob);
        ob = new OptionBean("2", "Parking Info");
        optionBeansList.add(ob);
        ob = new OptionBean("3", "Parking Stats");
        optionBeansList.add(ob);
        ob = new OptionBean("4", "My Cars");
        optionBeansList.add(ob);

        MainRecyclerAdapter recyclerAdapter = new MainRecyclerAdapter(this, optionBeansList);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);

    }

    private void getLocation() {
        // create class object
        gps = new GPSTrackerService(this);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_SHORT).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    void listener() {
        final AlertDialog.Builder build = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        result = new TextView(this);
        result.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (!result.getText().toString().trim().contains("Exception")) {
                        JSONObject obj = new JSONObject(result.getText().toString().trim());
                        JSONArray obj1 = obj.getJSONArray("values");
                        JSONObject temp = obj1.getJSONObject(0);

                        if (temp.optString("available").equals("0")) {
                            build.setTitle("Notice");
                            build.setMessage("Parking is full right now.");
                            build.setPositiveButton("OKAY", null);
                            build.show();
                        }
                    }
                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    @Override
    public void callBack(OptionBean optionBean) {

        switch (optionBean.getOptionID()) {
            case "1":

                if (cd.isConnectingToInternet()) {
                    dialog = new AppCompatDialog(this, R.style.AppCompatAlertDialogStyle);
                    dialog.setContentView(R.layout.car_list_dialog);
                    dialog.setCancelable(true);
                    dialog.setTitle("Select Your Car");
                    ListView lv = (ListView) dialog.findViewById(R.id.lv);
                    final DatabaseHandlerCarList carListDB = new DatabaseHandlerCarList(this);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.car_list_item, carListDB.getCarList());
                    lv.setAdapter(adapter);
                    dialog.show();
                    final Intent intent = new Intent(this, MySlotActivity.class);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            dialog.dismiss();
                            intent.putExtra("carNumber", String.valueOf(parent.getItemAtPosition(position)));
                            startActivity(intent);
                        }
                    });
                } else {
                    Intent intent = new Intent(this, MySlotActivity.class);
                    startActivity(intent);
                }
                break;

            case "2":
                Intent intent2 = new Intent(this, ParkingInfoActivity.class);
                startActivity(intent2);

                break;

            case "3":
                Intent intent3 = new Intent(this, ParkingStatsActivity.class);
                startActivity(intent3);
                break;

            case "4":
                Intent intent4 = new Intent(this, MyCarsActivity.class);
                startActivity(intent4);
                break;

            default:
                break;
        }
    }

    public boolean checkInternetConnection() {
        if (!cd.isConnectingToInternet()) {
            Snackbar snackbar = Snackbar
                    .make(recyclerView, "No internet connection.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkInternetConnection();
                        }
                    });
            snackbar.show();
            return false;
        } else
            return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("About MLCP");
            builder.setMessage(R.string.about);
            builder.setPositiveButton("OK, GOT IT", null);
            builder.show();
            return true;
        }else if (id == R.id.action_send){
            if (OPTION_FLAG){
                OPTION_FLAG = false;
                menu.findItem(R.id.action_send).setTitle("Stop");
                startGeofence();
            }else {
                OPTION_FLAG = true;
                menu.findItem(R.id.action_send).setTitle("Start");
                stopGeofence();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    private void verifyPlayServices() {
        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
            case ConnectionResult.SUCCESS: {
                break;
            }
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED: {
                finish();
            }
            default: {
                finish();
            }
        }
    }

    private void startGeofence() {
        Log.i("TAG", "startGeoFence");
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        Geofence.Builder builder = new Geofence.Builder();
        mGeofence = builder.setRequestId(FENCE_ID)
                .setCircularRegion(location.getLatitude(), location.getLongitude(), RADIUS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(mGeofence)
                .build();


        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    geofencingRequest,
                    mPendingIntent
            ).setResultCallback(this);

    }

    private void stopGeofence() {
        Log.i("TAG", "stopGeoFence");
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, mPendingIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Call GoogleApiClient connection when starting the Activity
        googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ( !googleApiClient.isConnected() && !googleApiClient.isConnecting() ) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected()");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed()");
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()){
            Log.i("TAG", "status == success");
            Intent intent = new Intent( mIntent );
            startService( intent );
        }else if (status.isCanceled()){
            stopService(mIntent);
        }else if (status.isInterrupted()){
            stopService(mIntent);
        }
    }

    private void generateNotification(){
        int thisConversationId = 42;

        String message = "Number of free slots are 732 out of 947.";
        //Log.d("My Tag","Message : "+ message);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        Intent msgHeardIntent = new Intent()
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction("innovations.tcs.com.mlcpuser.MY_ACTION_MESSAGE_HEARD")
                .putExtra("conversation_id", thisConversationId);

        PendingIntent msgHeardPendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(),
                        thisConversationId,
                        msgHeardIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


//        sendNotification(message);

        Intent msgReplyIntent = new Intent()
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction("innovations.tcs.com.mlcpuser.MY_ACTION_MESSAGE_REPLY")
                .putExtra("conversation_id", thisConversationId);

        PendingIntent msgReplyPendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                thisConversationId,
                msgReplyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Build a RemoteInput for receiving voice input in a Car Notification
        RemoteInput remoteInput = new RemoteInput.Builder(MY_VOICE_REPLY_KEY)
                .setLabel(getApplicationContext().getString(R.string.notification_reply))
                .build();
//
        // Create an unread conversation object to organize a group of messages
        // from a particular sender.
        String conversationName = "MLCP";
        NotificationCompat.CarExtender.UnreadConversation.Builder unreadConvBuilder =
                new NotificationCompat.CarExtender.UnreadConversation.Builder(conversationName)
                        .setReadPendingIntent(msgHeardPendingIntent)
                        .setReplyAction(msgReplyPendingIntent, remoteInput);

        unreadConvBuilder.addMessage(message)
                .setLatestTimestamp(System.currentTimeMillis());

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("MLCP")
                        .setContentText(message)
                        .setGroup("GROUP")
                        .setGroupSummary(false)
                        .setContentIntent(msgHeardPendingIntent)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setSound(soundUri);

        notificationBuilder.extend(new NotificationCompat.CarExtender()
                .setUnreadConversation(unreadConvBuilder.build()));

        NotificationManagerCompat msgNotificationManager =
                NotificationManagerCompat.from(this);
        msgNotificationManager.notify("tag",
                thisConversationId, notificationBuilder.build());

        sendNotification(message);
    }

    private void sendNotification(String message) {

        if (googleApiClient.isConnected()) {
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(Constants.NOTIFICATION_PATH);
            dataMapRequest.getDataMap().putDouble(Constants.NOTIFICATION_TIMESTAMP, System.currentTimeMillis());
            dataMapRequest.getDataMap().putString(Constants.NOTIFICATION_TITLE, "MLCP");
            dataMapRequest.getDataMap().putString(Constants.NOTIFICATION_CONTENT, message);
            PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(googleApiClient, putDataRequest);
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
}