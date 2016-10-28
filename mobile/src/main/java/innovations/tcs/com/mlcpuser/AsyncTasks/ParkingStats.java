package innovations.tcs.com.mlcpuser.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import innovations.tcs.com.mlcpuser.Activities.ParkingStatsActivity;
import innovations.tcs.com.mlcpuser.R;

public class ParkingStats extends AsyncTask<String, Void, String> {

    private Context _context;
    private ProgressDialog progress;

    public ParkingStats(Context context) {
        _context = context;
    }

    protected void onPreExecute() {
        progress = ProgressDialog.show(_context, "", "");
        progress.setContentView(R.layout.progress3);
        progress.show();
    }

    @Override
    protected String doInBackground(String... arg0) {
        try {
            String link = "http://www.mymlcp.co.in/mlcpapp/?tag=GetAvailableSlots";
            Log.d("myTag", link);
            URL url = new URL(link.trim().replace(" ", "xyzzyspoonshift1"));
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.flush();
            BufferedReader reader = new BufferedReader
                    (new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }
            return sb.toString();
        } catch (Exception e) {
            return new String(e.getMessage() + "Exception: null");
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (progress.isShowing())
            progress.dismiss();
        ParkingStatsActivity.result.setText(result);
    }
}