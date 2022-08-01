package com.rifcode.chatiw.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;

/**
 * Created by ibra_ on 31/01/2018.
 */

public class DialogUtils {

        public static AlertDialog.Builder CustomAlertDialog(View view, Activity activity){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
            alertDialogBuilder.setView(view);


            return alertDialogBuilder;
        }


}
