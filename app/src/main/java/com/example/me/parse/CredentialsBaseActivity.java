package com.example.me.parse;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


public class CredentialsBaseActivity extends AppCompatActivity {

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

}
