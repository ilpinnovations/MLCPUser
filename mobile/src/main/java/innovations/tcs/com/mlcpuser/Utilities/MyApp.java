package innovations.tcs.com.mlcpuser.Utilities;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

@SuppressLint("NewApi")
public class MyApp extends Application {

    private static MyApp instance;

    public MyApp() {
        super();
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.loadFonts();
    }

    public static MyApp getApp() {
        return instance;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onTerminate() {

        super.onTerminate();
    }
}
