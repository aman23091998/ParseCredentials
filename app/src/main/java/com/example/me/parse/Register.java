package com.example.me.parse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.cengalabs.flatui.views.FlatButton;
import com.cengalabs.flatui.views.FlatEditText;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class Register extends CredentialsBaseActivity {

    private final static String LOG_TAG = Register.class.getSimpleName();

    private FlatEditText emailWrapper, passwordWrapper, passwordConfirmWrapper, nameWrapper, usernameWrapper;

    private ImageView mFBSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setHomeButtonEnabled(true);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication(), getResources().getString(R.string.facebook_app_id));

        if ( ParseUser.getCurrentUser() == null) {
            startPostLoginActivity();
            return;
        }

        emailWrapper = (FlatEditText) findViewById(R.id.email_register);
        passwordWrapper = (FlatEditText) findViewById(R.id.password_register);
        passwordConfirmWrapper = (FlatEditText) findViewById(R.id.confirm_password_register);
        nameWrapper = (FlatEditText) findViewById(R.id.name_register);
        usernameWrapper = (FlatEditText) findViewById(R.id.username_register);

        FlatButton registerButton = (FlatButton) findViewById(R.id.registerButton);
        assert registerButton != null;
        registerButton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  registerUser();
                                              }
                                          }
        );

        mFBSignUp = (ImageView) findViewById(R.id.fbSignup);
        mFBSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> permissions = Arrays.asList("email", "public_profile");

                ParseFacebookUtils.logInWithReadPermissionsInBackground(Register.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            getFBData();
                            Log.d("MyApp", "User signed up and logged in through Facebook!");

                        } else {
                            Log.d("MyApp", "User logged in through Facebook!");
                            //startPostLoginActivity();

                        }
                    }
                });
            }
        });

    }

    public void getFBData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                if (object != null) {
                    try {

                        emailWrapper.setText(object.getString("email"));
                        nameWrapper.setText(object.getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void registerUser() {
        hideKeyboard();
        String name = nameWrapper.getText().toString().trim();
        String email = emailWrapper.getText().toString().trim();
        String password = passwordWrapper.getText().toString();
        String confirmPassword = passwordConfirmWrapper.getText().toString();
        String username = usernameWrapper.getText().toString();

        if (!validateEmail(email))
            emailWrapper.setError("Not a valid email address!");
        else emailWrapper.setError(null);

        if (!validatePassword(password))
            passwordWrapper.setError("Password should have at-least 8 characters ");

        else if (!confirmPasswords(password, confirmPassword)) {
            passwordWrapper.setError("Passwords don't match");
            passwordConfirmWrapper.setError("Password don't match ");
        } else {
            passwordWrapper.setError(null);
            passwordConfirmWrapper.setError(null);
        }

        if (name.length() == 0) nameWrapper.setError("Field can't be left Empty ");
        if (username.length() == 0) usernameWrapper.setError("Field can't be left Empty ");


        if (validateEmail(email) && validatePassword(password) && confirmPasswords(password, confirmPassword) && name.length() > 0 && username.length() > 0) {
            usernameWrapper.setError(null);
            emailWrapper.setError(null);
            passwordWrapper.setError(null);
            passwordConfirmWrapper.setError(null);
            signUp(name, password, email, username);
        }

    }

    public void signUp(final String name, final String password, final String email, final String username) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put("name", name);
        user.signUpInBackground(new SignUpCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.d(LOG_TAG, "user: " + username + "<" + email + ">" + "registered");
                                            startPostLoginActivity();
                                        } else {
                                            Log.e(LOG_TAG, "SignUpFailed " + e.getMessage());
                                            displayErrorDialog(e.getMessage());
                                        }
                                    }
                                }
        );
    }

    public void displayErrorDialog(String errorMessage) {

        FlatUIDialog errorDialog = new FlatUIDialog(Register.this, errorMessage);
        errorDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

}