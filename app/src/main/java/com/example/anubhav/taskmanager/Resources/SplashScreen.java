package com.example.anubhav.taskmanager.Resources;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.example.anubhav.taskmanager.MainActivity;
import com.example.anubhav.taskmanager.R;

public class SplashScreen extends AppCompatActivity {

    int timespan = 2500;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_spash_screen);
        final Intent i = new Intent(this, MainActivity.class);

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivityForResult(i, 5);

            }
        }, timespan);

    }
}
