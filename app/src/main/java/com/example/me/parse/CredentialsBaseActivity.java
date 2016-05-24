package com.example.me.parse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.ParseTwitterUtils;


public class CredentialsBaseActivity extends AppCompatActivity {


    protected String FACEBOOK_APP_ID ; // = "1725940584314538";
    protected String TWITTER_CONSUMER_KEY = "0R2WQBBxoDHVryvU6cLPNaW9I";
    protected String TWITTER_CONSUMER_SECRET = "MkhM27qez9Ikd32snMFoxPo6eqVtjWyCcPqtUBEV06k9GMk0e0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FACEBOOK_APP_ID = getResources().getString(R.string.facebook_app_id);
    }

    protected boolean validateEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    protected void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected boolean validatePassword(String password) {
        return password.length() >= 8;
    }

    protected boolean confirmPasswords(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    protected void startPostLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), PostLoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void initialiseParseLogin(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication(), FACEBOOK_APP_ID);

        ParseTwitterUtils.initialize(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);

    }

}
