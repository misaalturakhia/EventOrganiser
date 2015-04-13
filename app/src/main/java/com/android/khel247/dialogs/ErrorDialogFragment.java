package com.android.khel247.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/** A DialogFragment that lets a dialog be specified and provides the base for its display.
 * Created by Misaal on 15/12/2014.
 */
public class ErrorDialogFragment extends DialogFragment {

    private Dialog mDialog;

    public ErrorDialogFragment(){
        mDialog = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return mDialog;
    }

    /**
     * setter
     * @param mDialog
     */
    public void setDialog(Dialog mDialog) {
        this.mDialog = mDialog;
    }
}
