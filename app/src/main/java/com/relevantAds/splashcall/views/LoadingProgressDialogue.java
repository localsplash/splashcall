package com.relevantAds.splashcall.views;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;

import com.relevantAds.splashcall.R;

public class LoadingProgressDialogue {
    public Dialog dialog;

    public void showDialog(Activity activity){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_progress);
        dialog.show();

    }
    public void dismissDialogue(Activity activity)
    {
        dialog.dismiss();
    }

}
