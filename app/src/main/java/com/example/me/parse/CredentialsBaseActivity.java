package com.example.me.parse;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.parse.Parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by me on 09-05-2016.
 */
public class CredentialsBaseActivity extends AppCompatActivity {

    private final static String PARSE_APPLICATION_ID = "TBA";
    private final static String PARSE_SERVER = "TBA";

    protected static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;

    protected void initialiseParse() {
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(PARSE_APPLICATION_ID)
                .server(PARSE_SERVER)
                .build());
    }

    protected boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
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

    protected void  startPostLoginActivity(){
        Intent intent = new Intent(getApplicationContext(), PostLoginActivity.class);
        startActivity(intent);
        finish();
    }

}
