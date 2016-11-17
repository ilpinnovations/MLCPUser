package innovations.tcs.com.mlcpuser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyMessageHeardReceiver extends BroadcastReceiver {
    private static final String TAG = MyMessageHeardReceiver.class.getSimpleName();

    public MyMessageHeardReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // If you set up the intent as described in
        // "Create conversation read and reply intents",
        // you can get the conversation ID by calling:
        int thisConversationId = intent.getIntExtra("conversation_id", -1);

        Log.i(TAG, "conversation_id: " + thisConversationId);

        // Remove the notification to indicate it has been read
        // and update the list of unread conversations in your app.
    }
}
