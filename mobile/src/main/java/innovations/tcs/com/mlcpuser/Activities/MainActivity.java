package innovations.tcs.com.mlcpuser.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import innovations.tcs.com.mlcpuser.AsyncTasks.ParkingAvailable;
import innovations.tcs.com.mlcpuser.Beans.OptionBean;
import innovations.tcs.com.mlcpuser.Databases.DatabaseHandler;
import innovations.tcs.com.mlcpuser.Databases.DatabaseHandlerCarList;
import innovations.tcs.com.mlcpuser.Interfaces.Communicator;
import innovations.tcs.com.mlcpuser.R;
import innovations.tcs.com.mlcpuser.RecyclerAdapters.MainRecyclerAdapter;
import innovations.tcs.com.mlcpuser.Utilities.ConnectionDetector;
import innovations.tcs.com.mlcpuser.Utilities.Utils;

public class MainActivity extends AppCompatActivity implements Communicator {

    Toolbar toolbar;
    ActionBar actionBar;
    RecyclerView recyclerView;
    ArrayList<OptionBean> optionBeansList;
    ConnectionDetector cd;
    public static TextView result = null;
    AppCompatDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

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

        MainRecyclerAdapter recyclerAdapter = new MainRecyclerAdapter(getApplicationContext(), optionBeansList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
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
        }
        return super.onOptionsItemSelected(item);
    }
}