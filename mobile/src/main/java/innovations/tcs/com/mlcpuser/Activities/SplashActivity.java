package innovations.tcs.com.mlcpuser.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import innovations.tcs.com.mlcpuser.AsyncTasks.ParkingAvailable;
import innovations.tcs.com.mlcpuser.Beans.Info;
import innovations.tcs.com.mlcpuser.Databases.DatabaseHandler;
import innovations.tcs.com.mlcpuser.R;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 0;
    private static boolean FLAG = false;

    public static boolean end = false;

    List<String> permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        end = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if (checkPermission()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions( permissions.toArray( new String[permissions.size()] ), REQUEST_CODE_SOME_FEATURES_PERMISSIONS );
            }
        }else if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }else {
            startApplication();
        }

    }

    private boolean checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int hasContactPermission = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            permissions = new ArrayList<String>();
            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (hasContactPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }

            return !permissions.isEmpty();

        }

        return true;
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();

        if (FLAG){
            FLAG = false;
            startApplication();
        }
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
//        FLAG = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "onRequestPermissions");
        switch ( requestCode ) {
            case REQUEST_CODE_SOME_FEATURES_PERMISSIONS: {
                int count = 0;
                for( int i = 0; i < permissions.length; i++ ) {
                    if( grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                        Log.d( "Permissions", "Permission Granted: " + permissions[i] );
                        count++;
                    } else if( grantResults[i] == PackageManager.PERMISSION_DENIED ) {
                        Log.d( "Permissions", "Permission Denied: " + permissions[i] );
                    }

                    if (count == 2){
                        Log.i(TAG, "in onRequestPermissionsResult: count = " + count);
                        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

                        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                            buildAlertMessageNoGps();
                        }else {
                            startApplication();
                            finish();
                        }
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    public void startApplication(){

        int SPLASH_TIME_OUT = 2000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final DatabaseHandler db = new DatabaseHandler(SplashActivity.this);
                if (db.getContactsCount() <= 0) {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);

                    int count = db.getContactsCount();
                    Log.i("TAG", "Login count: " + count);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        FLAG = true;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "Enable the GPS first!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                        finish();
                        startActivity(intent);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}