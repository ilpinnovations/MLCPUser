package innovations.tcs.com.mlcpuser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;

public class MyMessageReplyReceiver extends BroadcastReceiver {
    String MY_VOICE_REPLY_KEY = "voice_reply_key";

    public MyMessageReplyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        int thisConversationId = intent.getIntExtra("conversation_id", -1);
    }

    /**
     * Get the message text from the intent.
     * Note that you should call
     * RemoteInput.getResultsFromIntent() to process
     * the RemoteInput.
     */
    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput =
                RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(MY_VOICE_REPLY_KEY);
        }
        return null;
    }
}
