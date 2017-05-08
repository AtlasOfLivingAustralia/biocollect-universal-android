package au.csiro.ozatlas.manager;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by sad038 on 3/5/17.
 */

public class AtlasDialogManager {

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

    public static void alertBoxForSetting(Context context, String message, String title, String positiveButtonText, DialogInterface.OnClickListener positiveClickListener) {
        alertBoxForSetting(context, message, title, positiveButtonText, positiveClickListener, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel the dialog box
                dialog.cancel();
            }
        });
    }

    public static void alertBoxForSetting(Context context, String message, String title, DialogInterface.OnClickListener positiveClickListener) {
        alertBoxForSetting(context, message, title, "OK", positiveClickListener, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel the dialog box
                dialog.cancel();
            }
        });
    }

    public static void alertBoxForMessage(Context context, String message, String positiveButtonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setPositiveButton(positiveButtonText, null);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
