package innovations.tcs.com.mlcpuser.Utilities;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {
    public static final String DISPLAY_MESSAGE_ACTION = "com.androidhive.pushnotifications.DISPLAY_MESSAGE";
    public static final String EXTRA_MESSAGE = "m";

    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}