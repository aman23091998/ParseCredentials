package com.example.me.parse;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.cengalabs.flatui.views.FlatButton;
import com.cengalabs.flatui.views.FlatTextView;

public class FlatUIDialog extends Dialog implements View.OnClickListener {

    private FlatButton mPositiveBtn;
    private FlatButton mNegativeBtn;
    private String mMessage;
    private FlatTextView mErrorTextView;
    private boolean showNegativeButton;
    private String negButtonText;


    public FlatUIDialog(Activity activity, String message) {
        super(activity);
        this.mMessage = message;
        showNegativeButton = false;
    }

    public FlatUIDialog(Activity activity, String message, String negButton, negativeButtonClick negativeBtnClick) {
        super(activity);
        this.mMessage = message;
        negButtonText = negButton;
        showNegativeButton = true;
        mNegativeButtonClick = negativeBtnClick ;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.flat_ui_dialog);


        mPositiveBtn = (FlatButton) findViewById(R.id.dialog_positive_btn);
        mPositiveBtn.setOnClickListener(this);
        if(showNegativeButton)
        {
            mNegativeBtn = (FlatButton) findViewById(R.id.dialog_negative_btn);
            mNegativeBtn.setText(negButtonText);
            mNegativeBtn.setVisibility(View.VISIBLE);
            mNegativeBtn.setOnClickListener(this);
        }
        mErrorTextView = (FlatTextView) findViewById(R.id.dialog_error_message);
        mErrorTextView.setText(mMessage);

    }

    public interface negativeButtonClick{
        void onNegativeClick ();
    }

    negativeButtonClick mNegativeButtonClick ;

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {

            case R.id.dialog_positive_btn:
                dismiss();
                break;


            case R.id.dialog_negative_btn:
                mNegativeButtonClick.onNegativeClick();
                dismiss();
                break;
        }
    }
}
