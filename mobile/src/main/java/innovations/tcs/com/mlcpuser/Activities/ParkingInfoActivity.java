package innovations.tcs.com.mlcpuser.Activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import innovations.tcs.com.mlcpuser.AsyncTasks.ParkingInfo;
import innovations.tcs.com.mlcpuser.Beans.ParkingInfoBean;
import innovations.tcs.com.mlcpuser.Databases.DatabaseHandlerParkingInfo;
import innovations.tcs.com.mlcpuser.R;
import innovations.tcs.com.mlcpuser.RecyclerAdapters.ParkingInfoRecyclerAdapter;
import innovations.tcs.com.mlcpuser.Utilities.ConnectionDetector;
import innovations.tcs.com.mlcpuser.Utilities.Utils;

public class ParkingInfoActivity extends AppCompatActivity {
    public static TextView result = null;
    Toolbar toolbar;
    ActionBar actionBar;
    RecyclerView recyclerView;
    ArrayList<ParkingInfoBean> parkingInfoBeansList;
    ConnectionDetector cd;
    DatabaseHandlerParkingInfo db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parking_info);

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

        recyclerView = (RecyclerView) findViewById(R.id.list_colors);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        db = new DatabaseHandlerParkingInfo(this);
        cd = new ConnectionDetector(getApplicationContext());

        /** Calling Listener **/
        listener();

        /** Setting up fonts **/
        ViewGroup vg = (ViewGroup) findViewById(R.id.root);
        Utils.setFontAllView(vg);

        /** Checking Internet Connection **/
        if (checkInternetConnection()) {
            new ParkingInfo(ParkingInfoActivity.this).execute("");
        } else {
            if (db.getParkingInfoDetailsCount() > 0) {
                List<String> parkingInfoDetails = db.getParkingInfoDetails();
                parkingInfoBeansList = new ArrayList<>();
                ParkingInfoBean msb;
                msb = new ParkingInfoBean("1", parkingInfoDetails.get(0));
                parkingInfoBeansList.add(msb);
                msb = new ParkingInfoBean("2", parkingInfoDetails.get(1));
                parkingInfoBeansList.add(msb);

                ParkingInfoRecyclerAdapter parkingInfoRecyclerAdapter = new ParkingInfoRecyclerAdapter(ParkingInfoActivity.this, parkingInfoBeansList);
                recyclerView.setAdapter(parkingInfoRecyclerAdapter);
            }
        }
    }

    void listener() {
        result = new TextView(this);
        result.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (!result.getText().toString().trim().contains("Exception")) {
                        JSONObject obj = new JSONObject(result.getText().toString().trim());
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
                        parkingInfoBeansList = new ArrayList<>();
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

                        db.addParkingInfoDetails(yesterdaysBusiestHrs, todaysBusiestHrs);

                        pib = new ParkingInfoBean("2", todaysBusiestHrs);
                        parkingInfoBeansList.add(pib);

                        ParkingInfoRecyclerAdapter parkingInfoRecyclerAdapter = new ParkingInfoRecyclerAdapter(ParkingInfoActivity.this, parkingInfoBeansList);
                        recyclerView.setAdapter(parkingInfoRecyclerAdapter);
                    }
                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });
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
        } else {
            new ParkingInfo(ParkingInfoActivity.this).execute("");
            return true;
        }
    }

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
}