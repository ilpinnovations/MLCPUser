package innovations.tcs.com.mlcpuser.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import innovations.tcs.com.mlcpuser.Beans.Info;
import innovations.tcs.com.mlcpuser.Beans.MyCarsBean;
import innovations.tcs.com.mlcpuser.Databases.DatabaseHandler;
import innovations.tcs.com.mlcpuser.Databases.DatabaseHandlerCarList;
import innovations.tcs.com.mlcpuser.R;
import innovations.tcs.com.mlcpuser.RecyclerAdapters.MyCarsRecyclerAdapter;
import innovations.tcs.com.mlcpuser.Utilities.ConnectionDetector;

public class MyCarsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<MyCarsBean> myCarsBeansList;
    Toolbar toolbar;
    ActionBar actionBar;
    FloatingActionButton fab;
    MyCarsRecyclerAdapter myCarsRecyclerAdapter;

    private CoordinatorLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycars);

        rootView = (CoordinatorLayout) findViewById(R.id.root);

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

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        final DatabaseHandlerCarList carListDB = new DatabaseHandlerCarList(this);
        DatabaseHandler db = new DatabaseHandler(this);
        recyclerView = (RecyclerView) findViewById(R.id.list_colors);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        myCarsBeansList = new ArrayList<>();

        MyCarsBean mcb;

        Log.i("TAG", "carListCount: " + carListDB.getCarListCount());
        Log.i("TAG", "ContactsCount: " + db.getContactsCount());

        if (carListDB.getCarListCount() > 0) {
            for (String car : carListDB.getCarList()) {
                mcb = new MyCarsBean("1", car);
                myCarsBeansList.add(mcb);
            }
        }
        myCarsRecyclerAdapter = new MyCarsRecyclerAdapter(MyCarsActivity.this, myCarsBeansList);
        recyclerView.setAdapter(myCarsRecyclerAdapter);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_car, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            // action with ID action_refresh was selected
//            case R.id.action_add:
//                Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT)
//                        .show();
//                showDialog();
//                break;
//            default:
//                break;
//        }
//
//        return true;
//    }

    private void showDialog(){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.new_car_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        userInput.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        // set dialog message
        alertDialogBuilder
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                String vehicleNumber = userInput.getText().toString();

                                DatabaseHandlerCarList db = new DatabaseHandlerCarList(getApplicationContext());
                                List<String> carList = db.getCarList();

                                if (!carList.contains(vehicleNumber)){
                                    DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
                                    String empName = db1.getAllContacts().get(0).getName();

                                    addNewCar(vehicleNumber, empName);
                                }else {
                                    Toast.makeText(getApplicationContext(), "Vehicle number already present in database!", Toast.LENGTH_SHORT).show();
                                }

                                myCarsRecyclerAdapter.notifyDataSetChanged();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void addNewCar(final String carNumber, String name){
        final String employeeId = carNumber;
        final String tag = "GetIsValidUser";
        final String employee_full_name = name;

        final String KEY_TAG = "tag";
        final String KEY_EMP_ID = "employeeId";
        final String KEY_EMP_NAME = "emp_full_name";

        final String REGISTER_URL = "http://mymlcp.co.in/mlcpapp/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();

                        try {
                            if (response != null) {
                                JSONObject myJson = new JSONObject(response);
                                String error = myJson.optString("error");

                                if (error.equals("false")) {
                                    Info info = new Info(carNumber, null, null);

                                    DatabaseHandlerCarList carListDB = new DatabaseHandlerCarList(getApplicationContext());
                                    carListDB.addCar(info);

                                    myCarsBeansList.clear();
                                    myCarsRecyclerAdapter.notifyDataSetChanged();
                                    for (String car : carListDB.getCarList()) {
                                        myCarsBeansList.add(new MyCarsBean("1", car));
                                    }

                                } else if (error.equals("true")) {
                                    String errorMsg = myJson.optString("errorMsg");
                                    Snackbar.make(rootView, "Sorry! Car number is not registered!", Snackbar.LENGTH_LONG).show();
//                                    Toast.makeText(getApplicationContext(), "Sorry! Number is not registered.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.d("Exception", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                        checkInternetConnection();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(KEY_TAG,tag);
                params.put(KEY_EMP_ID,employeeId);
                params.put(KEY_EMP_NAME, employee_full_name);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public boolean checkInternetConnection() {
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        if (!cd.isConnectingToInternet()) {
            Snackbar snackbar = Snackbar.make(recyclerView, "No internet connection.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkInternetConnection();
                        }
                    });
            snackbar.show();
            return false;
        }

        return true;
    }
}