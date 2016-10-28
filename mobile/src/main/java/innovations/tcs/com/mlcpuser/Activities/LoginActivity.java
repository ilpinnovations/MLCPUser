package innovations.tcs.com.mlcpuser.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import innovations.tcs.com.mlcpuser.Beans.Info;
import innovations.tcs.com.mlcpuser.Databases.DatabaseHandler;
import innovations.tcs.com.mlcpuser.Databases.DatabaseHandlerCarList;
import innovations.tcs.com.mlcpuser.R;
import innovations.tcs.com.mlcpuser.Utilities.AppConstant;
import innovations.tcs.com.mlcpuser.Utilities.ConnectionDetector;

public class LoginActivity extends AppCompatActivity implements OnClickListener, OnItemSelectedListener {

    LinearLayout more_vehicles;
    ConnectionDetector cd;
    String serverURL = AppConstant.BASE_URL, mlcp_location, emp_full_name, emp_vehicle_number, regId = "";
    EditText vehicleNumber, fullName, new_vehicle;
    DatabaseHandler db;
    DatabaseHandlerCarList carListDB;
    AppCompatButton login;
    Spinner location;
    ProgressDialog prgDialog;
    InstanceID instanceID;
    Context applicationContext;
    RequestParams params = new RequestParams();
    ScrollView scrollView;
    FloatingActionButton add;
    ImageButton close;
    int vehicles_added = 1, vehicleListCounter = 0, vehicleListSize;
    ArrayList<String> vehicleNumberList;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        applicationContext = getApplicationContext();
        vehicleNumber = (EditText) findViewById(R.id.input_vehicle);
        fullName = (EditText) findViewById(R.id.input_fullName);
        location = (Spinner) findViewById(R.id.spinner);
        login = (AppCompatButton) findViewById(R.id.btnSubmit);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        add = (FloatingActionButton) findViewById(R.id.addVehicle);
        more_vehicles = (LinearLayout) findViewById(R.id.added_vehicles);
        location.setOnItemSelectedListener(this);

        vehicleNumber.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        vehicleNumberList = new ArrayList<>();
        db = new DatabaseHandler(this);
        carListDB = new DatabaseHandlerCarList(this);
        cd = new ConnectionDetector(getApplicationContext());
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        loadSpinnerData();
        checkInternetConnection();

        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (vehicles_added == 1 && String.valueOf(vehicleNumber.getText()).equals("")) {
                    Snackbar snackbar = Snackbar.make(v, "Please enter a vehicle number.", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else if (vehicles_added > 1 && String.valueOf(new_vehicle.getText()).equals("")) {
                    Snackbar snackbar = Snackbar
                            .make(v, "Please enter a vehicle number.", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    alertDialogBuilder.setMessage("Add another car?");

                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {

                            dialog.dismiss();

                            if (vehicles_added == 1) {
                                if (!vehicleNumberList.contains(String.valueOf(vehicleNumber.getText())))
                                    vehicleNumberList.add(String.valueOf(vehicleNumber.getText()));
                            } else {
                                if (!vehicleNumberList.contains(String.valueOf(new_vehicle.getText())))
                                    vehicleNumberList.add(String.valueOf(new_vehicle.getText()));
                            }

                            LayoutInflater localLayoutInflater = LayoutInflater.from(getApplicationContext());
                            final View view = localLayoutInflater.inflate(R.layout.new_vehicle_input, null);
                            more_vehicles.addView(view);

                            close = (ImageButton) view.findViewById(R.id.close);
                            new_vehicle = (EditText) view.findViewById(R.id.new_vehicle);
                            new_vehicle.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

                            close.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    vehicleNumberList.remove(String.valueOf(new_vehicle.getText()));
                                    more_vehicles.removeView(view);
                                    vehicles_added--;
                                }
                            });
                            scrollView.scrollTo(0, 50 + (50 * vehicles_added));
                            vehicles_added++;
                        }
                    });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertDialogBuilder.show();
                }
            }
        });

        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                checkInternetConnection();

                if ((vehicleNumber.getText().toString().length() == 0)) {
                    Snackbar snackbar = Snackbar.make(arg0, "Please enter valid vehicle number.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else if (fullName.getText().toString().length() == 0) {
                    Snackbar snackbar = Snackbar.make(arg0, "Please enter full name.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else if (!isValidName(fullName.getText().toString())) {
                    Snackbar snackbar = Snackbar.make(arg0, "Please enter valid name.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    if (vehicles_added > 1) {
                        if (!vehicleNumberList.contains(String.valueOf(new_vehicle.getText())))
                            vehicleNumberList.add(String.valueOf(new_vehicle.getText()));
                    } else {
                        vehicleNumberList.add(String.valueOf(vehicleNumber.getText()));
                    }

                    emp_full_name = fullName.getText().toString();
                    emp_vehicle_number = vehicleNumber.getText().toString();

                    if (emp_full_name.trim().length() > 0) {
                        if (checkPlayServices()) {
                            /**GCM Reg ID Creation**/
                            createGCM(emp_vehicle_number);
                        }
                    } else {
                        Snackbar snackbar = Snackbar.make(arg0, "Please enter your details.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            }
        });
    }

    private void createGCM(final String emp_vehicle_number) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                prgDialog = new ProgressDialog(LoginActivity.this);
                prgDialog.setCancelable(false);
                prgDialog.setMessage("Logging In...");
                prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                prgDialog.setProgress(0);
                prgDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    instanceID = InstanceID.getInstance(applicationContext);
                    regId = instanceID.getToken(String.valueOf(AppConstant.GOOGLE_PROJECT_ID), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    msg = "Registration ID :" + regId;
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!TextUtils.isEmpty(regId)) {
                    /****Passing @param Vehicle Number****/
                    params.put("regId", regId);

                    vehicleListSize = vehicleNumberList.size();
                    /**Insert GCM Reg ID in database**/
                    for (int i = 0; i < vehicleListSize; i++) {
                        /****Passing @param Vehicle Number****/
                        params.put("vehicleNumber", vehicleNumberList.get(i));
                        storeRegIdInServer(emp_vehicle_number);
                    }

                    for (String j : vehicleNumberList) {
                        Log.d("responseCarListPrevious", j);
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
                //prgDialog.dismiss();
            }
        }.execute(null, null, null);
    }

    private void storeRegIdInServer(final String emp_vehicle_number) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(AppConstant.GCM_SERVER_URL, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                        String vehicle = responseString.substring(1);
                        if (responseString.charAt(0) == '0') {
                            Log.d("response", "Failure : " + vehicle);
                            vehicleNumberList.remove(vehicle);
                        } else {
                            Log.d("response", "Success :" + vehicle);
                        }
                        vehicleListCounter++;
                        if (vehicleListSize == vehicleListCounter) {
                            /**Checking details and Logging In**/
                            new login(serverURL, emp_full_name, emp_vehicle_number, vehicleNumberList, mlcp_location, LoginActivity.this).execute("");
                            Log.d("Login", "Done");
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {

                        String vehicle = responseString.substring(1);
                        if (responseString.charAt(0) == '0') {
                            Log.d("response", "Failure : " + vehicle);
                            vehicleNumberList.remove(vehicle);
                        } else {
                            Log.d("response", "Success :" + vehicle);
                        }

                        vehicleListCounter++;
                        if (vehicleListSize == vehicleListCounter) {
                            /**Checking details and Logging In**/
                            new login(serverURL, emp_full_name, emp_vehicle_number, vehicleNumberList, mlcp_location, LoginActivity.this).execute("");
                        }
                    }
                });
    }

    private class login extends AsyncTask<String, Void, String> {
        String _serverURL, _emp_full_name, _emp_vehicle_number, _mlcp_location;
        ArrayList<String> _vehicleNumberList;
        Activity _context;
        private String Content;
        private String Error = null;

        public login(String serverURL, String emp_full_name, String emp_vehicle_number, ArrayList<String> vehicleNumberList, String mlcp_location, Activity context) {
            _serverURL = serverURL;
            _emp_full_name = emp_full_name;
            _emp_vehicle_number = emp_vehicle_number;
            _vehicleNumberList = vehicleNumberList;
            _mlcp_location = mlcp_location;
            _context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... urls) {

            BufferedReader reader = null;

            try {
                String getIsValidUser = "GetIsValidUser";
                String data = URLEncoder.encode("tag", "UTF-8") + "=" + URLEncoder.encode(getIsValidUser, "UTF-8");
                data += "&" + URLEncoder.encode("employeeId", "UTF-8") + "=" + URLEncoder.encode(_emp_vehicle_number, "UTF-8");
                data += "&" + URLEncoder.encode("emp_full_name", "UTF-8") + "=" + URLEncoder.encode(_emp_full_name, "UTF-8");
                URL url = new URL(_serverURL);

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                Content = sb.toString();
            } catch (Exception ex) {
                Error = ex.getMessage();
            } finally {
                try {
                    reader.close();
                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                }
            }
            return "";
        }

        protected void onPostExecute(String unused) {
            if (Error != null) {
                Toast.makeText(_context, "Error due to some network problem! Please connect to internet. ", Toast.LENGTH_LONG).show();
            } else {
                try {
                    if (Content != null) {
                        JSONObject myJson = new JSONObject(Content);
                        String error = myJson.optString("error");

                        if (error.equals("false")) {
                            Info info = new Info(_emp_vehicle_number, _vehicleNumberList, _emp_full_name, _mlcp_location);

                            for (String j : _vehicleNumberList) {
                                Log.d("responseCarListFinal", j);
                            }

                            db.addContact(info);
                            carListDB.addCarList(info);

                            if (db.getContactsCount() > 0) {
                                db.close();
                                finish();
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                            }
                        } else if (error.equals("true")) {
                            String errorMsg = myJson.optString("errorMsg");
                            Toast.makeText(_context, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                }
            }
            prgDialog.dismiss();
        }
    }

    public void checkInternetConnection() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        alertDialogBuilder.setTitle("No Internet");
        alertDialogBuilder.setMessage("Please connect to a network.");

        alertDialogBuilder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                checkInternetConnection();
            }
        });

        alertDialogBuilder.setNegativeButton("Leave", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                System.exit(0);
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        if (!cd.isConnectingToInternet()) {
            alertDialog.show();
            return;
        } else {
            alertDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void loadSpinnerData() {
        List<String> label = new ArrayList<>();
        label.add("Kochi");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, label);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mlcp_location = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public void onClick(View v) {
    }

    private boolean isValidName(String name) {
        String NAME_PATTERN = "[A-Za-z-\\s]*";
        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        applicationContext,
                        "This device doesn't support Play services, App will not work!",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }
}