package com.abhi.testingwhb;

import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;


public class PolyLoadProgress extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poly_load_progress);

        ProgressBar progressBar=findViewById(R.id.polyLoading);
        progressBar.setIndeterminate(true);
    }
}
