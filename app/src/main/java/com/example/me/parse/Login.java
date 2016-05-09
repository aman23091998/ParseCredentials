package com.example.me.parse;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class Login extends CredentialsBaseActivity {

    private TextInputEditText emailWrapper, passwordWrapper;

    private final static String LOG_TAG = Login.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialiseParse();

        emailWrapper = (TextInputEditText) findViewById(R.id.email);
        passwordWrapper = (TextInputEditText) findViewById(R.id.password);
        Button loginButton = (Button) findViewById(R.id.loginButton);

        assert loginButton != null;
        loginButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               loginUser();
                                           }
                                       }

        );

        TextView registerText = (TextView) findViewById(R.id.register_here);
        assert registerText != null;
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(Login.this, Register.class);
                Login.this.startActivity(register);
            }
        });
    }

    public void loginUser() {
        hideKeyboard();
        String email = emailWrapper.getText().toString();
        String password = passwordWrapper.getText().toString();
        if (!validateEmail(email))
            emailWrapper.setError("Not a valid email address!");
        else emailWrapper.setError(null);
        if (!validatePassword(password))
            passwordWrapper.setError("Password should have at-least 8 characters ");
        else passwordWrapper.setError(null);
        if (validateEmail(email) && validatePassword(password)) {
            emailWrapper.setError(null);
            passwordWrapper.setError(null);
            signIn(email, password);
        }

    }

    private void signIn(final String email, final String password) {
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null && e == null) {
                    Toast.makeText(Login.this, email + " loxgged in", Toast.LENGTH_LONG).show();
                    startPostLoginActivity();
                } else {
                    Log.e(LOG_TAG, "Login failed: " + e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setMessage("Login failed: " + e.getMessage());
                    builder.setTitle("Error! ");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

}
