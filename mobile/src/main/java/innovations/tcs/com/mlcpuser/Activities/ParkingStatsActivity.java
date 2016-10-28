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

import innovations.tcs.com.mlcpuser.AsyncTasks.ParkingStats;
import innovations.tcs.com.mlcpuser.Beans.ParkingStatsBean;
import innovations.tcs.com.mlcpuser.Databases.DatabaseHandlerParkingStats;
import innovations.tcs.com.mlcpuser.R;
import innovations.tcs.com.mlcpuser.RecyclerAdapters.ParkingStatsRecyclerAdapter;
import innovations.tcs.com.mlcpuser.Utilities.ConnectionDetector;
import innovations.tcs.com.mlcpuser.Utilities.Utils;

public class ParkingStatsActivity extends AppCompatActivity {
    public static TextView result = null;
    ConnectionDetector cd;
    RecyclerView recyclerView;
    ArrayList<ParkingStatsBean> parkingStatsBeansList;
    Toolbar toolbar;
    ActionBar actionBar;
    DatabaseHandlerParkingStats db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_stats);

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
        cd = new ConnectionDetector(getApplicationContext());
        db = new DatabaseHandlerParkingStats(this);

        listener();
        ViewGroup vg = (ViewGroup) findViewById(R.id.root);
        Utils.setFontAllView(vg);

        /** Checking Internet Connection **/
        if (checkInternetConnection()) {
            new ParkingStats(ParkingStatsActivity.this).execute("");
        } else {
            if (db.getParkingStatsDetailsCount() > 0) {
                List<String> parkingStatsDetails = db.getParkingStatsDetails();
                parkingStatsBeansList = new ArrayList<>();
                ParkingStatsBean msb;
                msb = new ParkingStatsBean("1", parkingStatsDetails.get(0));
                parkingStatsBeansList.add(msb);
                msb = new ParkingStatsBean("2", parkingStatsDetails.get(1));
                parkingStatsBeansList.add(msb);

                ParkingStatsRecyclerAdapter parkingStatsRecyclerAdapter = new ParkingStatsRecyclerAdapter(ParkingStatsActivity.this, parkingStatsBeansList);
                recyclerView.setAdapter(parkingStatsRecyclerAdapter);
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

                        ParkingStatsBean psb;
                        parkingStatsBeansList = new ArrayList<>();
                        psb = new ParkingStatsBean("1", temp.optString("available"));
                        parkingStatsBeansList.add(psb);
                        psb = new ParkingStatsBean("2", temp.optString("total"));
                        parkingStatsBeansList.add(psb);

                        ParkingStatsRecyclerAdapter parkingStatsRecyclerAdapter = new ParkingStatsRecyclerAdapter(ParkingStatsActivity.this, parkingStatsBeansList);
                        recyclerView.setAdapter(parkingStatsRecyclerAdapter);

                        db.addParkingStatsDetails(temp.optString("available"), temp.optString("total"));
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
            new ParkingStats(ParkingStatsActivity.this).execute("");
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
