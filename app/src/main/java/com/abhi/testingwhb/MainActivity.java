package com.abhi.testingwhb;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.abhi.testingwhb.model.User;
import com.abhi.testingwhb.model.UserLocation;
import com.abhi.testingwhb.services.LocationService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    public static final String TAG = "MainActivity";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public UserLocation mUserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        getLocationPermission();


    }

    private void getLocationPermission() {
        Log.d(TAG, "Step 1: getLocationPermission ");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            isMapsEnabled();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isMapsEnabled() {
        Log.d(TAG, "Step 2: isMapsEnabled ");
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        assert manager != null;
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        } else {
            signin();
        }
        return true;
    }


    private void buildAlertMessageNoGps() {
        Log.d(TAG, "Step 2a: isMapsEnabled ");
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            signin();
        }
    }


    public void signin() {
        Log.d(TAG, "Step 3: signin ");
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User();
                            user.setUser_id(mAuth.getUid());
                            Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                            getUserDetails();

                        } else {
                            Toast.makeText(MainActivity.this, "SignIn failed, Check Internet" + task.getException(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void getUserDetails() {

        Log.d(TAG, "Step 4: getUserDetails ");

        if (mUserLocation != null) {
            getLastKnowLocation();
            mUserLocation.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        } else {
            try {
                String uidNull = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Map<String, Object> userlcoation = new HashMap<>();
                userlcoation.put("user_id", uidNull);
                db.collection("User Location").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(userlcoation);
                getLastKnowLocation();
            } catch (Exception ignored) {

            }

        }
    }

    private void getLastKnowLocation() {
        Log.d(TAG, "Step 5: getLastKnowLocation ");

        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if (location != null) {
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        //  String uidNull = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Map<String, Object> userlcoation = new HashMap<>();
                        userlcoation.put("user_location", geoPoint);
                        db.collection("User Location").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(userlcoation);
                        startLocationService();
                        goToMap();
                    }

                }
            }
        });
    }

    private void startLocationService() {
        Log.d(TAG, "Step 6: startLocationService ");
        Intent serviceIntent = new Intent(this, LocationService.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            MainActivity.this.startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    public void goToMap() {
        Log.d(TAG, "Step 7: goToMap");
        Intent intent = new Intent(MainActivity.this, MainMapActivity.class);
        startActivity(intent);
        finish();
    }


}

