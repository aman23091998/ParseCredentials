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
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class Register extends CredentialsBaseActivity {

    private final static String LOG_TAG = Register.class.getSimpleName();

    private FlatEditText emailWrapper, passwordWrapper, passwordConfirmWrapper, nameWrapper, usernameWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication(), getResources().getString(R.string.facebook_app_id));

        if (ParseUser.getCurrentUser() != null) {
            if (ParseUser.getCurrentUser().getUsername().length() < 16)
                startPostLoginActivity();
            else ParseUser.logOutInBackground();
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

        ImageView FBSignUp = (ImageView) findViewById(R.id.fbSignup);
        assert FBSignUp != null;
        FBSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> permissions = Arrays.asList("email", "public_profile");

                ParseFacebookUtils.logInWithReadPermissionsInBackground(Register.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else {
                            Log.d(LOG_TAG, user.getUsername());
                            if (user.getUsername().length() > 15) {
                                getFBData();
                                displayErrorDialog("Please enter the details to complete registrations!");
                            } else {
                                startPostLoginActivity();
                                finish();
                            }
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
                        Log.d(LOG_TAG, object.toString());
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
        String password = passwordWrapper.getText().toString().trim();
        String confirmPassword = passwordConfirmWrapper.getText().toString().trim();
        String username = usernameWrapper.getText().toString().trim();

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

        if (username.length() > 15) usernameWrapper.setError("Must be less than 15 characters");


        if (validateEmail(email) && validatePassword(password) && confirmPasswords(password, confirmPassword) && name.length() > 0 && username.length() > 0) {
            usernameWrapper.setError(null);
            emailWrapper.setError(null);
            passwordWrapper.setError(null);
            passwordConfirmWrapper.setError(null);
            signUp(name, password, email, username);
        }

    }

    public void signUp(final String name, final String password, final String email, final String username) {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
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
        } else {
            currentUser.setEmail(email);
            currentUser.setUsername(username);
            currentUser.setPassword(password);
            currentUser.put("name", name);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d(LOG_TAG, currentUser.get("name") + "signed up");
                    } else {
                        Log.e(LOG_TAG, e.getMessage());
                    }
                }
            });
        }
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
/*    @Override
    public void onBackPressed() {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            FlatUIDialog registerNotComplete = new FlatUIDialog
                    (Register.this, "Registration not complete", "Cancel Registration", new FlatUIDialog.negativeButtonClick() {
                        @Override
                        public void onNegativeClick() {

                            if (currentUser.getUsername().length() >= 20) {
                                ParseUser.logOutInBackground(new LogOutCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null)
                                            Log.d(LOG_TAG, "Logged off, registration not complete");
                                    }
                                });
                            }
                            Register.super.onBackPressed();
                        }
                    });
            registerNotComplete.show();
        } else Register.super.onBackPressed();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                final ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    FlatUIDialog registerNotComplete = new FlatUIDialog
                            (Register.this, "Registration not complete", "Cancel Registration", new FlatUIDialog.negativeButtonClick() {
                                @Override
                                public void onNegativeClick() {

                                    if (currentUser.getUsername().length() >= 20) {
                                        ParseUser.logOutInBackground(new LogOutCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null)
                                                    Log.d(LOG_TAG, "Logged off, registration not complete");
                                            }
                                        });
                                    }
                                    Register.super.onBackPressed();
                                }
                            });
                    registerNotComplete.show();
                } else Register.super.onBackPressed();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getUsername().length() >= 20) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null)
                            Log.d(LOG_TAG, "Logged off, registration not complete");
                    }
                });
            }
        }
        super.onDestroy();
    }*/

