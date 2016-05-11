package com.example.me.parse;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.cengalabs.flatui.views.FlatButton;
import com.cengalabs.flatui.views.FlatEditText;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class Register extends CredentialsBaseActivity {

    private final static String LOG_TAG = Register.class.getSimpleName();

    private FlatEditText emailWrapper, passwordWrapper, passwordConfirmWrapper, nameWrapper, usernameWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



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
//        user.put("emailVerified", false);
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
}