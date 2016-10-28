package innovations.tcs.com.mlcpuser.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import innovations.tcs.com.mlcpuser.Beans.MySlotBean;
import innovations.tcs.com.mlcpuser.Databases.DatabaseHandlerMySlot;
import innovations.tcs.com.mlcpuser.R;
import innovations.tcs.com.mlcpuser.RecyclerAdapters.MySlotRecyclerAdapter;
import innovations.tcs.com.mlcpuser.Utilities.ConnectionDetector;
import innovations.tcs.com.mlcpuser.Utilities.Utils;

@SuppressLint("SimpleDateFormat")
public class MySlotActivity extends AppCompatActivity {

    String serverURL = "http://mymlcp.co.in/mlcpapp/get_slot.php", carNumber;
    ProgressDialog progress;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ActionBar actionBar;
    ConnectionDetector cd;
    DatabaseHandlerMySlot db;
    ArrayList<MySlotBean> mySlotBeansList;
    boolean carFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_myslot);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationIcon(R.drawable.menu_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        db = new DatabaseHandlerMySlot(this);
        cd = new ConnectionDetector(getApplicationContext());

        ViewGroup vg = (ViewGroup) findViewById(R.id.root);
        Utils.setFontAllView(vg);

        recyclerView = (RecyclerView) findViewById(R.id.list_colors);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        //registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));

        /** Checking Internet Connection **/
        if (!checkInternetConnection()) {
            if (db.getMySlotDetailsCount() > 0) {
                List<String> mySlotDetails = db.getMySlotDetails();
                mySlotBeansList = new ArrayList<>();
                MySlotBean msb;
                msb = new MySlotBean("1", mySlotDetails.get(0));
                mySlotBeansList.add(msb);
                msb = new MySlotBean("2", mySlotDetails.get(1));
                mySlotBeansList.add(msb);
                msb = new MySlotBean("3", mySlotDetails.get(2));
                mySlotBeansList.add(msb);
                msb = new MySlotBean("4", mySlotDetails.get(3));
                mySlotBeansList.add(msb);
                MySlotRecyclerAdapter mySlotRecyclerAdapter = new MySlotRecyclerAdapter(MySlotActivity.this, mySlotBeansList);
                recyclerView.setAdapter(mySlotRecyclerAdapter);
                toolbar.setSubtitle(mySlotDetails.get(4));
            }
        }
    }

    public void getMySlot() {
        toolbar.setSubtitle(carNumber);
        new LongOperation_bookslot(serverURL, carNumber, this).execute("");
    }

    public boolean checkInternetConnection() {
        if (!cd.isConnectingToInternet()) {
            Snackbar snackbar = Snackbar.make(recyclerView, "No internet connection.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkInternetConnection();
                        }
                    });
            snackbar.show();
            carFlag = false;
            return false;
        } else {
            if (carFlag) {
                Bundle extras = getIntent().getExtras();
                carNumber = extras.getString("carNumber");
            } else {
                List<String> mySlotDetails = db.getMySlotDetails();
                carNumber = mySlotDetails.get(4);
                carFlag = true;
            }
            getMySlot();
            return true;
        }
    }

    /*private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            WakeLocker.acquire(getApplicationContext());
            Toast.makeText(MySlotActivity.this, "My Slot New Message: " + newMessage, Toast.LENGTH_LONG).show();
            new LongOperation_bookslot(serverURL, carNumber, MySlotActivity.this).execute("");
            WakeLocker.release();
        }
    };*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_custom, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            checkInternetConnection();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LongOperation_bookslot extends AsyncTask<String, Void, String> {
        String _url, _slotdate, _empimei, jsonStr = "";
        Activity _context;
        String _emp_vehicle_number;
        HttpURLConnection conn;
        private String Content;
        private String Error = null;

        public LongOperation_bookslot(String url, String emp_vehicle_number, Activity context) {
            _url = url;
            _context = context;
            _emp_vehicle_number = emp_vehicle_number;
            mySlotBeansList = new ArrayList<>();
        }

        @SuppressLint("SimpleDateFormat")
        protected void onPreExecute() {

            progress = ProgressDialog.show(MySlotActivity.this, "", "");
            progress.setContentView(R.layout.progress);
            progress.show();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            _slotdate = dateFormat.format(date);
            TelephonyManager tManager = (TelephonyManager) getBaseContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            try {
                _empimei = tManager.getDeviceId();
            } catch (Exception Ex) {
                Toast.makeText(MySlotActivity.this, "Please enable Telephone Permission in App Permission.", Toast.LENGTH_LONG).show();
            }

        }

        protected String doInBackground(String... urls) {
            try {
                String link = "http://mymlcp.co.in/mlcpapp/get_slot.php?vehiclenumber=" + _emp_vehicle_number;
                URL url = new URL(link);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                switch (conn.getResponseCode()) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        br.close();
                        jsonStr = sb.toString();
                        break;
                    default:
                        jsonStr = "Error";
                }

            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (conn != null) {
                    try {
                        conn.disconnect();
                    } catch (Exception ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            return "";
        }

        protected void onPostExecute(String unused) {

            if (progress.isShowing())
                progress.dismiss();

            if (Error != null) {
                Toast.makeText(_context, "Error due to some network problem! Please connect to internet. "
                        + unused, Toast.LENGTH_LONG).show();
            } else {
                try {
                    if (!jsonStr.equalsIgnoreCase("")) {
                        JSONObject myJson = new JSONObject(jsonStr);

                        if (myJson.getString("booking_id").equalsIgnoreCase("NULL") || myJson.getString("booking_id") == null) {
                            setContentView(R.layout.activity_no_slot);
                            TextView car = (TextView) findViewById(R.id.textView2);
                            car.setText(carNumber);
                        } else {
                            String floor_name = myJson.getString("floorname");
                            String slot_name = myJson.getString("slotname");
                            String book_timein = myJson.getString("book_timein");

                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                            Date d1 = null;
                            Date d2 = new Date();

                            long diffMinutes = 0, diffHours = 0, diffDays = 0;

                            try {
                                d1 = format.parse(book_timein);

                                long diff = d2.getTime() - d1.getTime();
                                diffMinutes = diff / (60 * 1000) % 60;
                                diffHours = diff / (60 * 60 * 1000) % 24;
                                long diffHours1 = diff / (60 * 60 * 1000);
                                diffDays = (int) diffHours1 / 24;

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            MySlotBean msb;
                            msb = new MySlotBean("1", floor_name);
                            mySlotBeansList.add(msb);
                            msb = new MySlotBean("2", slot_name);
                            mySlotBeansList.add(msb);
                            msb = new MySlotBean("3", String.valueOf(d1).substring(4, 10) + ", " + book_timein.substring(11, 19));
                            mySlotBeansList.add(msb);

                            String duration;
                            if (diffHours == 0) {
                                msb = new MySlotBean("4", diffMinutes + " mins");
                                duration = diffMinutes + " mins";
                                mySlotBeansList.add(msb);
                            } else if (diffDays == 0) {
                                msb = new MySlotBean("4", diffHours + " hrs " + diffMinutes + " mins");
                                duration = diffHours + " hrs " + diffMinutes + " mins";
                                mySlotBeansList.add(msb);
                            } else {
                                msb = new MySlotBean("4", diffDays + " days " + diffHours + " hrs " + diffMinutes + " mins");
                                duration = diffHours + " hrs " + diffMinutes + " mins";
                                mySlotBeansList.add(msb);
                            }

                            db.addMySlotDetails(floor_name, slot_name, String.valueOf(d1).substring(4, 10) + ", " + book_timein.substring(11, 19), duration, carNumber);
                        }
                    }


                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                }
            }
            MySlotRecyclerAdapter mySlotrecyclerAdapter = new MySlotRecyclerAdapter(MySlotActivity.this, mySlotBeansList);
            recyclerView.setAdapter(mySlotrecyclerAdapter);
        }
    }
}