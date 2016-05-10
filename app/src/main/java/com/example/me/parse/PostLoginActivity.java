package com.example.me.parse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class PostLoginActivity extends AppCompatActivity {

    private final static String LOG_TAG = PostLoginActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.logout) {
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        Log.d(LOG_TAG, "Successfully logged out !");
                        Intent intent = new Intent(PostLoginActivity.this,  MainActivity.class);
                        startActivity(intent);
                    }
                    else Log.e(LOG_TAG, e.getMessage());
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

}
