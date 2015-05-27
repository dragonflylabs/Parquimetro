package lib.lennken.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import lib.lennken.R;

/**
 * Created by caprinet on 10/13/14.
 */

public abstract class Alerts {

    static ProgressDialog dialog;

    public static void showLoadingDialog(Context context){
        showProgressDialog(context, R.string.__dialog_loading);
    }

    public static void showProgressDialog(Context context, int title, int message){
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
        dialog = new ProgressDialog(context);
        dialog.setTitle(context.getString(title));
        dialog.setMessage(context.getString(message));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void showProgressDialog(Context context, int message){
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(message));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void showProgressDialog(Context context, String message){
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
        dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void showProgressDialog(Context context, int message, boolean cancelable, DialogInterface.OnCancelListener cancelCallback){
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(message));
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        dialog.setOnCancelListener(cancelCallback);
        dialog.show();
    }

    public static void stopProgressDialog(){
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public static void showAlertMessage(Activity activity, String message){
        Crouton.makeText(activity,message, Style.ALERT).show();
    }

    public static void showAlertMessage(Activity activity, int message){
        Crouton.makeText(activity,message, Style.ALERT).show();
    }

    public static void showInfoMessage(Activity activity, String message){
        Crouton.makeText(activity,message, Style.INFO).show();
    }

    public static void showInfoMessage(Activity activity, int message){
        Crouton.makeText(activity,message, Style.INFO).show();
    }

    public static void showConfirmMessage(Activity activity, String message){
        Crouton.makeText(activity,message, Style.CONFIRM).show();
    }

    public static void showConfirmMessage(Activity activity, int message){
        Crouton.makeText(activity, message, Style.CONFIRM).show();
    }

    public static void showDialogWithCallback(Context context, int message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context).setMessage(message).setNegativeButton(R.string.__dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setPositiveButton(R.string.__dialog_accept, listener).create().show();
    }

    public static void showDialogWithCallback(Context context, String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context).setMessage(message).setNegativeButton(R.string.__dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setPositiveButton(R.string.__dialog_accept, listener).create().show();
    }

    public static void showDialogWithCallbacks(Context context, int message, DialogInterface.OnClickListener acceptListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(context).setMessage(message).setNegativeButton(R.string.__dialog_cancel, cancelListener)
                .setPositiveButton(R.string.__dialog_accept, acceptListener).create().show();
    }

    public static void showDialogWithCallbacks(Context context, String message, DialogInterface.OnClickListener acceptListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(context).setMessage(message).setNegativeButton(R.string.__dialog_cancel, cancelListener)
                .setPositiveButton(R.string.__dialog_accept, acceptListener).create().show();
    }

    public static void showDialog(Context context, int message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context).setMessage(message).setPositiveButton(R.string.__dialog_accept, listener).create().show();
    }
    public static void showDialog(Context context, String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context).setMessage(message).setPositiveButton(R.string.__dialog_accept, listener).create().show();
    }

    public static void showDialogWithOutside(Context context, String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context).setMessage(message).setCancelable(false).setPositiveButton(R.string.__dialog_accept, listener).create().show();
    }

    public static void showDialogButtons(Context context, String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context).setMessage(message).setPositiveButton(R.string.__dialog_accept, listener).setNegativeButton(R.string.__dialog_cancel,listener).create().show();
    }

    public static void showDialogButtons(Context context, int message, DialogInterface.OnClickListener listener,DialogInterface.OnClickListener listenerNegative) {
        new AlertDialog.Builder(context).setMessage(message).setPositiveButton(R.string.__dialog_accept, listener).setNegativeButton(R.string.__dialog_cancel,listenerNegative).create().show();
    }
    public static void showDialogButtons(Context context, String message, DialogInterface.OnClickListener listener,DialogInterface.OnClickListener listenerNegative) {
        new AlertDialog.Builder(context).setMessage(message).setPositiveButton(R.string.__dialog_accept, listener).setNegativeButton(R.string.__dialog_cancel,listenerNegative).create().show();
    }

    public static void showDialogButtons(Context context, String message, DialogInterface.OnClickListener listener,DialogInterface.OnClickListener listenerNegative,String btnpositive, String btnnegative) {
        new AlertDialog.Builder(context).setMessage(message).setPositiveButton(btnpositive, listener).setNegativeButton(btnnegative,listenerNegative).create().show();
    }

    public static void showToastMessage(Context context, int msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToastMessage(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
