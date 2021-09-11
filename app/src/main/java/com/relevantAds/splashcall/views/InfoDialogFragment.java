package com.relevantAds.splashcall.views;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.relevantAds.splashcall.R;

public class InfoDialogFragment extends DialogFragment {

    public interface InfoDialogListener {

        public void onInfoDialogOkClick();
    }

    InfoDialogListener mListener;

    public static InfoDialogFragment newInstance(String notify_title,
                                                 String notify_detail)
    {
        InfoDialogFragment frag = new InfoDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", notify_title);

        args.putString("detail", notify_detail);
        frag.setArguments(args);
        return frag;
    }
    public void setOnInfoClickListener(InfoDialogListener mListener){
        this.mListener=mListener;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        MaterialDialog.Builder maBuilder=new MaterialDialog.Builder(getActivity());
        maBuilder.title(getArguments().getString("title"));
        maBuilder.titleColor(getResources().getColor(R.color.black));
        maBuilder.content(getArguments().getString("detail"));
        maBuilder.contentColor(getResources().getColor(R.color.black));
        maBuilder.positiveText("OK");
        //maBuilder.btnSelector(R.drawable.dialog_btn_positive_selector, DialogAction.POSITIVE);
        maBuilder.cancelable(false);
        maBuilder.canceledOnTouchOutside(false);

        //maBuilder.typeface("Helvetica/Helvetica-Bold.ttf", "Helvetica/Helvetica.otf");

//        maBuilder.typeface("Poppins-Light.ttf", "Poppins-Light.ttf");
        maBuilder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                mListener.onInfoDialogOkClick();
                //((InfoDialogListener) getActivity()).onInfoDialogOkClick();
                InfoDialogFragment.this.getDialog().dismiss();
            }

        });

        MaterialDialog dialog=maBuilder.build();
/*
        dialog.getWindow().getAttributes().windowAnimations= R.style.ConfirmDialogAnimation;*/
        return dialog;
    }
}
