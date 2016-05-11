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
    private String mMessage;
    private FlatTextView mErrorTextView ;


    public FlatUIDialog(Activity activity, String message) {
        super(activity);
        this.mMessage = message ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.flat_ui_dialog);


        mPositiveBtn = (FlatButton) findViewById(R.id.dialog_positive_btn);
        mPositiveBtn.setOnClickListener(this);
        mErrorTextView = (FlatTextView) findViewById(R.id.dialog_error_message);
        mErrorTextView.setText(mMessage);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){

            case R.id.dialog_positive_btn:
                dismiss();
                break ;

        }
    }
}
