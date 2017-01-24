/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package innovations.tcs.com.mlcpuser.BroadCastReciever;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import innovations.tcs.com.mlcpuser.R;

/**
 * A receiver that gets called when a reply is sent to a given conversationId.
 */
public class MessageReplyReceiver extends BroadcastReceiver {

    private static final String TAG = MessageReplyReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
      /*  if (MessagingService.REPLY_ACTION.equals(intent.getAction())) {

        }*/

        int conversationId = intent.getIntExtra("conversation_id", -1);
        CharSequence reply = getMessageText(intent);
        if (conversationId != -1) {
            Log.d(TAG, "Got reply (" + reply + ") for ConversationId " + conversationId);

            // Update the notification to stop the progress spinner.
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context);
            Notification repliedNotification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher )
                    .setLargeIcon(BitmapFactory.decodeResource(
                            context.getResources(), R.mipmap.ic_launcher ))
                    .setContentText("Replied")
                    .build();
            notificationManager.notify(conversationId, repliedNotification);
        }

    }

    /**
     * Get the message text from the intent.
     * Note that you should call {@code RemoteInput#getResultsFromIntent(intent)} to process
     * the RemoteInput.
     */
    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(
                    "extra_remote_reply");
        }
        return null;
    }
}
