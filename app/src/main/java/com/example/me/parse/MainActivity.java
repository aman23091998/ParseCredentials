package com.example.me.parse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cengalabs.flatui.FlatUI;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class MainActivity extends AppCompatActivity {

    private final static String PARSE_APPLICATION_ID = "uEJ7QPPwHtCLVEFqEeb2Ur5x9YfCpdjxL9yXxIkT";
    private final static String PARSE_SERVER = "TBA";
    private final static String PARSE_CLIENT_ID = "wRdsU4hrKXvZcZWiru3ZT6MJllIYzCCXFmq4YN2Q";
    private static boolean enableParse = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FlatUI.initDefaultValues(this);
        FlatUI.setDefaultTheme(FlatUI.ORANGE);

        if (enableParse) {
            initialiseParse();
            enableParse = false ;
        }


        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }


    protected void initialiseParse() {
        Parse.initialize(MainActivity.this, PARSE_APPLICATION_ID, PARSE_CLIENT_ID);
        ParseFacebookUtils.initialize(MainActivity.this);


        /*Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(PARSE_APPLICATION_ID)
                .server(PARSE_SERVER)
                .build());*/
    }
}
