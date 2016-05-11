package com.example.me.parse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cengalabs.flatui.views.FlatEditText;
import com.cengalabs.flatui.views.FlatTextView;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class Login extends CredentialsBaseActivity {

    private FlatEditText nameWrapper, passwordWrapper;

    private final static String LOG_TAG = Login.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameWrapper = (FlatEditText) findViewById(R.id.username);
        passwordWrapper = (FlatEditText) findViewById(R.id.password);
        Button loginButton = (Button) findViewById(R.id.loginButton);


        assert loginButton != null;
        loginButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               loginUser();
                                           }
                                       }

        );

        FlatTextView registerText = (FlatTextView) findViewById(R.id.register_here);
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

        String username = nameWrapper.getText().toString();
        String password = passwordWrapper.getText().toString();
        signIn(username, password);

    }

    private void signIn(final String username, final String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {

                if (user != null && e == null) {
                    Log.d(LOG_TAG, " Successfully logged in");
                    startPostLoginActivity();
                } else {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("email", username);
                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            Log.d(LOG_TAG, "Clicked :|");
                            if (!objects.isEmpty() && e == null) {
                                ParseUser temp = objects.get(0);
                                String usernameFromServer = temp.getUsername();
                                ParseUser.logInInBackground(usernameFromServer, password, new LogInCallback() {
                                    @Override
                                    public void done(ParseUser user, ParseException e) {
                                        if (user != null && e == null) {
                                            Log.d(LOG_TAG, " Successfully logged in");
                                            startPostLoginActivity();
                                        }
                                    }
                                });
                            } else {
                                Log.d(LOG_TAG, "Invalid login parameters ! ");
                                displayErrorDialog("Invalid login parameters ! ");
                            }

                        }
                    });
                }
            }
        });
    }

    public void displayErrorDialog(String errorMessage) {

        FlatUIDialog errorDialog = new FlatUIDialog(Login.this, errorMessage);
        errorDialog.show();

    }

}
