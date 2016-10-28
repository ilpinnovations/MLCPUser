package innovations.tcs.com.mlcpuser.GCM;

import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;
import innovations.tcs.com.mlcpuser.Databases.DatabaseHandlerCarList;
import innovations.tcs.com.mlcpuser.Utilities.AppConstant;

public class GcmIDListenerService extends InstanceIDListenerService {

    DatabaseHandlerCarList carListDB = new DatabaseHandlerCarList(this);
    RequestParams params = new RequestParams();
    String regId;

    @Override
    public void onTokenRefresh() {
        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
        try {
            regId = instanceID.getToken(String.valueOf(AppConstant.GOOGLE_PROJECT_ID), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (carListDB.getCarListCount() > 0) {
            for (String car : carListDB.getCarList()) {
                params.put("regId", regId);
                params.put("vehicleNumber", car);
                storeRegIdInServer();
            }
        }
    }

    private void storeRegIdInServer() {
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
                        Log.d("response", "Failure : " + vehicle);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {

                        String vehicle = responseString.substring(1);
                        Log.d("response", "Success :" + vehicle);
                    }
                });
    }
}