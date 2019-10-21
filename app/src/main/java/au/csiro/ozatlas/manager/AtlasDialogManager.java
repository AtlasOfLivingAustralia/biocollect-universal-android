package au.csiro.ozatlas.manager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.function.Function;

import au.csiro.ozatlas.R;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;

/**
 * Created by sad038 on 3/5/17.
 */

/**
 * A class to create Alert Dialogs
 */
public class AtlasDialogManager {

    public static String TAG = "AtlasDialogManager";

    /**
     * @param context
     * @param message               message to show in the Alert Dialog
     * @param title                 Dialog Title
     * @param positiveButtonText    The text for the positive button such as "OK"
     * @param positiveClickListener listener when the user click "OK" or positive button
     * @param negativeClickListener listener when the user click "CANCEL" or negative button
     */
    public static void alertBox(Context context, String message, String title, String positiveButtonText, DialogInterface.OnClickListener positiveClickListener, String negativeText, DialogInterface.OnClickListener negativeClickListener, String neutralText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setTitle(title)
                .setPositiveButton(positiveButtonText, positiveClickListener)
                .setNegativeButton(negativeText, negativeClickListener)
                .setNeutralButton(neutralText, null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * @param context
     * @param message               message to show in the Alert Dialog
     * @param title                 Dialog Title
     * @param positiveButtonText    The text for the positive button such as "OK"
     * @param positiveClickListener listener when the user click "OK" or positive button
     * @param negativeClickListener listener when the user click "CANCEL" or negative button
     */
    public static void alertBox(Context context, String message, String title, String positiveButtonText, DialogInterface.OnClickListener positiveClickListener, String negativeText, DialogInterface.OnClickListener negativeClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setTitle(title)
                .setPositiveButton(positiveButtonText, positiveClickListener)
                .setNegativeButton(negativeText, negativeClickListener);
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * @param context
     * @param message               message to show in the Alert Dialog
     * @param title                 Dialog Title
     * @param positiveButtonText    The text for the positive button such as "OK"
     * @param positiveClickListener listener when the user click "OK" or positive button
     * @param negativeClickListener listener when the user click "CANCEL" or negative button
     */
    public static void alertBox(Context context, String message, String title, String positiveButtonText, DialogInterface.OnClickListener positiveClickListener, DialogInterface.OnClickListener negativeClickListener) {
        alertBox(context, message, title, positiveButtonText, positiveClickListener, "Cancel", negativeClickListener);
    }

    /**
     * @param context
     * @param message               message to show in the Alert Dialog
     * @param title                 Dialog Title
     * @param positiveButtonText    The text for the positive button such as "OK"
     * @param positiveClickListener listener when the user click "OK" or positive button
     */
    public static void alertBox(Context context, String message, String title, String positiveButtonText, DialogInterface.OnClickListener positiveClickListener) {
        alertBox(context, message, title, positiveButtonText, positiveClickListener, new DialogInterface.OnClickListener() {
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
    public static void alertBox(Context context, String message, String title, DialogInterface.OnClickListener positiveClickListener) {
        alertBox(context, message, title, "OK", positiveClickListener, (dialog, id) -> {
            // cancel the dialog box
            dialog.cancel();
        });
    }

    /**
     * @param context
     * @param message               message to show in the Alert Dialog
     * @param title                 Dialog Title
     * @param positiveClickListener listener when the user click "OK" or positive button
     */
    public static void alertBox(Context context, String message, String title, String positiveText, DialogInterface.OnClickListener positiveClickListener, boolean isOnlyPositiveButton) {
        if(isOnlyPositiveButton) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message)
                    .setCancelable(false)
                    .setTitle(title)
                    .setPositiveButton(positiveText, positiveClickListener);
            AlertDialog alert = builder.create();
            alert.show();
        }else{
            alertBox(context, message, title, positiveText, positiveClickListener);
        }
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

    public static void messageBox(Context context, String title, String message, @StringRes int editTextHint, @StringRes int positiveButtonLabel, @StringRes int negativeButtonLabel, StringCallback onClickListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_text_input, null);
        final EditText input = view.findViewById(R.id.dialog_text_input_input);
        final TextView messageView = view.findViewById(R.id.dialog_text_input_message);
        messageView.setText(message);
        builder.setView(view);
        builder.setPositiveButton(positiveButtonLabel, (dialog, which) -> {
            onClickListener.apply(input.getText());
        });
        builder.setNegativeButton(negativeButtonLabel, ((dialog, which) -> {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }));
        final Dialog dialog = builder.create();

        input.setHint(editTextHint);

        input.setOnFocusChangeListener((focusView, focus) -> {

            Log.d(TAG, "EditText input focus changed " + focus);
            if (focus) {
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }

            if (!focus) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        });


        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.show();

    }

    public interface StringCallback {
        void apply(CharSequence value);
    }
}
