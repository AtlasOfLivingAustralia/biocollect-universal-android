package upload;

import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Class to Notify the registered Receiver
 */
public class BroadcastNotifier {

    private LocalBroadcastManager mBroadcaster;

    /**
     * Creates a BroadcastNotifier containing an instance of LocalBroadcastManager.
     * LocalBroadcastManager is more efficient than BroadcastManager; because it only
     * broadcasts to components within the app, it doesn't have to do parceling and so forth.
     *
     * @param context a Context from which to get the LocalBroadcastManager
     */
    public BroadcastNotifier(Context context) {

        // Gets an instance of the support library local broadcastmanager
        mBroadcaster = LocalBroadcastManager.getInstance(context);

    }

    /**
     * Uses LocalBroadcastManager to send an {@link String} containing a logcat message.
     * {@link Intent} has the action {@code BROADCAST_ACTION} and the category {@code DEFAULT}.
     */
    void notifyDataChange() {
        Log.d("NOTIFIER", "notifyDataChange");
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION);

        // The Intent contains the custom broadcast action for this app
        //localIntent.setAction();

        // Puts log data into the Intent
        //localIntent.addCategory(Intent.CATEGORY_DEFAULT);

        // Broadcasts the Intent
        mBroadcaster.sendBroadcast(localIntent);
    }
}
