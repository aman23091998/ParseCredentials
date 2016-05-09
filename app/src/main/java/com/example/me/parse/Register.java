package com.example.me.parse;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class Register extends CredentialsBaseActivity {

    private final static String LOG_TAG = Register.class.getSimpleName();

    private TextInputEditText emailWrapper, passwordWrapper, passwordConfirmWrapper, nameWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        initialiseParse();

        emailWrapper = (TextInputEditText) findViewById(R.id.email_register);
        passwordWrapper = (TextInputEditText) findViewById(R.id.password_register);
        passwordConfirmWrapper = (TextInputEditText) findViewById(R.id.confirm_password_register);
        nameWrapper = (TextInputEditText) findViewById(R.id.name_register);

        Button registerButton = (Button) findViewById(R.id.registerButton);
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
        if (validateEmail(email) && validatePassword(password) && confirmPasswords(password, confirmPassword)) {
            emailWrapper.setError(null);
            passwordWrapper.setError(null);
            passwordConfirmWrapper.setError(null);
            signUp(name, password, email);
        }


    }

    public void signUp(final String name, final String password, final String email) {
        ParseUser user = new ParseUser();
        user.put("Name", name);
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);
        user.put("EmailVerified", false);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(LOG_TAG, "user: " + name + "<" + email + ">" + "registered");
                    startPostLoginActivity();
                }
                    else Log.e(LOG_TAG, e.getMessage());
                }
            }

            );
        }
    }