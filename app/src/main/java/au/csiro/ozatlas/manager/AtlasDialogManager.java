package au.csiro.ozatlas.manager;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by sad038 on 3/5/17.
 */

/**
 * A class to create Alert Dialogs
 */
public class AtlasDialogManager {

    /**
     * @param context
     * @param message               message to show in the Alert Dialog
     * @param title                 Dialog Title
     * @param positiveButtonText    The text for the positive button such as "OK"
     * @param positiveClickListener listener when the user click "OK" or positive button
     * @param negativeClickListener listener when the user click "CANCEL" or negative button
     */
    public static void alertBoxForSetting(Context context, String message, String title, String positiveButtonText, DialogInterface.OnClickListener positiveClickListener, DialogInterface.OnClickListener negativeClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setTitle(title)
                .setPositiveButton(positiveButtonText, positiveClickListener)
                .setNegativeButton("Cancel", negativeClickListener);
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * @param context
     * @param message               message to show in the Alert Dialog
     * @param title                 Dialog Title
     * @param positiveButtonText    The text for the positive button such as "OK"
     * @param positiveClickListener listener when the user click "OK" or positive button
     */
    public static void alertBoxForSetting(Context context, String message, String title, String positiveButtonText, DialogInterface.OnClickListener positiveClickListener) {
        alertBoxForSetting(context, message, title, positiveButtonText, positiveClickListener, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel the dialog box
                dialog.cancel();
            }
        });
    }

    /**
     * @param context
     * @param message               message to show in the Alert Dialog
     * @param title                 Dialog Title
     * @param positiveClickListener listener when the user click "OK" or positive button
     */
    public static void alertBoxForSetting(Context context, String message, String title, DialogInterface.OnClickListener positiveClickListener) {
        alertBoxForSetting(context, message, title, "OK", positiveClickListener, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel the dialog box
                dialog.cancel();
            }
        });
    }

    /**
     * @param context
     * @param message            message to show in the Alert Dialog
     * @param positiveButtonText The text for the positive button such as "OK"
     */
    public static void alertBoxForMessage(Context context, String message, String positiveButtonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setPositiveButton(positiveButtonText, null);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
