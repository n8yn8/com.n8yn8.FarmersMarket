package com.n8yn8.farmersmarket.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

//Global constants
/*
 * Define a request code to send to Google Play services
 * This code is returned in Activity.onActivityResult
 */

// Define a DialogFragment that displays the error dialog
public class ErrorDialogFragment extends DialogFragment {
	
    // Global field to contain the error dialog
    private Dialog mDialog;
    // Default constructor. Sets the dialog field to null
    public ErrorDialogFragment() {
        super();
        mDialog = null;
    }
    // Set the dialog to display
    public void setDialog(Dialog dialog) {
        mDialog = dialog;
    }
    // Return a Dialog to the DialogFragment.
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return mDialog;
    }

}