package com.example.me.parse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.cengalabs.flatui.views.FlatEditText;
import com.cengalabs.flatui.views.FlatTextView;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

import java.util.List;

public class Login extends CredentialsBaseActivity {

    private FlatEditText nameWrapper, passwordWrapper;

    private final static String LOG_TAG = Login.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialiseParseLogin();

        if (ParseUser.getCurrentUser() != null) {
            if (ParseUser.getCurrentUser().getUsername().length() < 16)
                startPostLoginActivity();
            else ParseUser.logOutInBackground();
            return;
        }

        nameWrapper = (FlatEditText) findViewById(R.id.username);
        passwordWrapper = (FlatEditText) findViewById(R.id.password);
        Button loginButton = (Button) findViewById(R.id.loginButton);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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

        ImageView twitterLoginButton = (ImageView) findViewById(R.id.twitter_login_button);
        assert twitterLoginButton != null;
        twitterLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Working on Twitter");
                ParseTwitterUtils.logIn(Login.this, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if ( user != null ) {
                            if (validateUser(user)) {
                                startPostLoginActivity();
                                finish();
                            }
                        } else if( e != null) Log.d(LOG_TAG, e.getMessage());
                    }
                });
            }
        });

        ImageView fbLogin = (ImageView) findViewById(R.id.fbSignin);
        assert fbLogin != null;
        fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(Login.this, null, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        Log.d(LOG_TAG, "Working on FB");
                        if (e == null) {
                            if (validateUser(user)) {
                                startPostLoginActivity();
                                finish();
                            }
                        } else Log.d(LOG_TAG, e.getMessage());
                    }
                });
            }
        });
    }

    public void loginUser() {
        hideKeyboard();

        String username = nameWrapper.getText().toString();
        String password = passwordWrapper.getText().toString();
        if (!(username.length() > 0)) nameWrapper.setError("Field can't be empty");
        if (!(password.length() > 0)) passwordWrapper.setError("Field can't be empty");
        if (username.length() > 0 && password.length() > 0)
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

    public boolean validateUser(ParseUser user) {

        if (user.getUsername().length() <= 15) return true;
        else {
            ParseUser.logOutInBackground();
            FlatUIDialog notRegistered = new FlatUIDialog(this, "User is not registered or account hasn't been linked.", "Register", new FlatUIDialog.negativeButtonClick() {
                @Override
                public void onNegativeClick() {
                    Intent intent = new Intent(Login.this, Register.class);
                    startActivity(intent);
                }
            });
            notRegistered.show();
            return false;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
