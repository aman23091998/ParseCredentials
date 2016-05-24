package com.example.me.parse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.cengalabs.flatui.views.FlatButton;
import com.cengalabs.flatui.views.FlatEditText;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class Register extends CredentialsBaseActivity {

    private final static String LOG_TAG = Register.class.getSimpleName();

    private FlatEditText emailWrapper, passwordWrapper, passwordConfirmWrapper, nameWrapper, usernameWrapper;

    private boolean linkTwitter = false;


    private TwitterLoginButton loginButton;

    private boolean fbLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);

        initialiseParseLogin();

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

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
                fbLogin = true;
                List<String> permissions = Arrays.asList("email", "public_profile");

                ParseFacebookUtils.logInWithReadPermissionsInBackground(Register.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d(LOG_TAG, "Uh oh. The user cancelled the Facebook login.");
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

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_signup_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {


                String Username = result.data.getUserName();
                Toast.makeText(Register.this, Username, Toast.LENGTH_LONG).show();
                AccountService accountService = Twitter.getApiClient(result.data).getAccountService();
                accountService.verifyCredentials(true, true, new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        nameWrapper.setText(result.data.name);
                        emailWrapper.setText(result.data.email);
                        usernameWrapper.setText(result.data.screenName);
                        linkTwitter = true;
                        displayErrorDialog("Please enter the details to complete registrations!");
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.d(LOG_TAG, "Login with Twitter failure", exception);
                    }
                });

            }

            @Override
            public void failure(TwitterException exception) {
                Log.d(LOG_TAG, "Login with Twitter failure", exception);
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

        if (username.length() > 15)
            usernameWrapper.setError("Must be less than 15 characters");


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
            final ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.put("name", name);
            user.signUpInBackground(new SignUpCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                if (linkTwitter) {
                                                    if (!ParseTwitterUtils.isLinked(user)) {
                                                        ParseTwitterUtils.link(user, Register.this, new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException ex) {
                                                                if (ParseTwitterUtils.isLinked(user)) {
                                                                    Log.d(LOG_TAG, "Woohoo, user logged in with Twitter!");
                                                                }

                                                                if (ex != null)
                                                                    Log.d(LOG_TAG, "Error Linking Twitter" + ex.getMessage());
                                                                Log.d(LOG_TAG, "user: " + username + "<" + email + ">" + "registered");
                                                                startPostLoginActivity();
                                                            }
                                                        });
                                                    }
                                                    linkTwitter = false;

                                                } else {
                                                    Log.d(LOG_TAG, "user: " + username + "<" + email + ">" + "registered");
                                                    startPostLoginActivity();
                                                }

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
                        startPostLoginActivity();
                        finish();
                    } else {
                        Log.e(LOG_TAG, "SignUpFailed " + e.getMessage());
                        displayErrorDialog(e.getMessage());
                    }
                }
            });
        }
    }

    public void displayErrorDialog(String errorMessage) {

        FlatUIDialog errorDialog = new FlatUIDialog(Register.this, errorMessage);
        errorDialog.show();

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fbLogin) {
            ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
            fbLogin = false;
        } else loginButton.onActivityResult(requestCode, resultCode, data);
    }
}