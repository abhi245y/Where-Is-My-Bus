package com.abhi245y.whereismybus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

public class Splashscreen extends AppCompatActivity {

    public int resume = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashscreen = new Intent(Splashscreen.this, MainActivity.class);
                startActivity(splashscreen);
                finish();
                resume = 1;
            }
        }, 1000);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
    }
}
