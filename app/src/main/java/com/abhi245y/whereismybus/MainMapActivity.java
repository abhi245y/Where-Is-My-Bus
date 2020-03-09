package com.abhi245y.whereismybus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.abhi245y.whereismybus.models.BusList;
import com.abhi245y.whereismybus.models.StopList;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static android.widget.Toast.LENGTH_SHORT;


public class MainMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    public static final String TAG = "MainMapActivity";
    private GoogleMap mMap;
//    private LatLngBounds mMapBoundary;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//    public String bus_num;
    public ArrayList<String> bus_from = new ArrayList<>();
    public ArrayList<String> bus_to = new ArrayList<>();
//    public DocumentReference bus;
//    public LatLng zoom;
//    public BusList busList;
//    public Marker marker;
//    public double bottomBoundary, leftBoundary, topBoundary, rightBoundary;
    public FirebaseAuth mAuth;
    public GeoApiContext geoApiContext = null;
    public LatLng  locuser;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private static final int LOCATION_UPDATE_INTERVAL = 3000;
//    public int camZoom = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public int code = 122;
    public int codeFrom = 123;
    public int codeTo = 124;
    public TextView From, To, from_location, to_loccation, common_bus;
    private static final String GOOGLE_PLACES_API_KEY = "AIzaSyCnkn0VsVabsU0rPFVq8Kh2bpHNIMu9Msc";
    public String location_name_form, location_name_to;
    public Button search;
    public LatLng FromLocation, ToLocation;
    public BottomSheetBehavior bottomSheetBehavior;
    public CollectionReference stopref = db.collection("Stop");
    public CollectionReference busref = db.collection("Bus List");

   


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);


        Places.initialize(getApplicationContext(), GOOGLE_PLACES_API_KEY);


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        To = findViewById(R.id.To);
        From = findViewById(R.id.From);
        search = findViewById(R.id.search);
        from_location = findViewById(R.id.from_location);
        to_loccation = findViewById(R.id.to_location);
        common_bus = findViewById(R.id.commonbus);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        getFromAndTo();
        bottomsheetbehave();
    }

    private void bottomsheetbehave() {

        // get the bottom sheet view
        ConstraintLayout bottomSheetLayout = findViewById(R.id.bottom_sheet);
        // init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);

    }


    //=====================================================================================================================================================================================================================

    /*
    //              Get From And To From PlacesAutofill Api and change text
    //
     */

    private void getFromAndTo() {

        From.setOnClickListener(v -> {
//                Toast.makeText(MainMapActivity.this, "On Click From", Toast.LENGTH_SHORT).show();
            code = 123;
            getStop();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        });

        To.setOnClickListener(v -> {
//                Toast.makeText(MainMapActivity.this, "On Click To", Toast.LENGTH_SHORT).show();
            code = 124;
            getStop();
        });

        search.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ToLocation.latitude, ToLocation.longitude), 16));
            calculateDirections(ToLocation);
//                getsearchresult();
            getfrom();


        });

    }

    public void getStop() {
        Log.i("tag", "Clicked From");
        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(MainMapActivity.this);
        startActivityForResult(intent, code);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == codeFrom) {
            if (resultCode == RESULT_OK) {
                Log.i("tag", "From ActivityResult");
                assert data != null;
                Place place = Autocomplete.getPlaceFromIntent(data);
                From.setText(place.getName());
                location_name_form = place.getName();
                FromLocation = place.getLatLng();

                assert FromLocation != null;
                mMap.addMarker(new MarkerOptions().position(FromLocation).title(location_name_form));


            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                assert data != null;
                Status status = Autocomplete.getStatusFromIntent(data);
                assert status.getStatusMessage() != null;
                Log.i("tag", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Toast.makeText(this, "cancelled", LENGTH_SHORT).show();
            }
        } else if (requestCode == codeTo) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Place place = Autocomplete.getPlaceFromIntent(data);
                To.setText(place.getName());
                location_name_to = place.getName();
                ToLocation = place.getLatLng();

                assert ToLocation != null;
                mMap.addMarker(new MarkerOptions().position(ToLocation).title(location_name_to));

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                assert data != null;
                Status status = Autocomplete.getStatusFromIntent(data);
                assert status.getStatusMessage() != null;
                Log.i("tag", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Toast.makeText(this, "cancelled", LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Nothing Selected", LENGTH_SHORT).show();
        }
    }

    //=====================================================================================================================================================================================================================


    /*
    //              Calculating Distance direction and other details from point A To B
    //
     */

    private void calculateDirections(LatLng toLocation) {
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                toLocation.latitude,
                toLocation.longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        FromLocation.latitude, FromLocation.longitude
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage());

            }
        });
    }

    /*
        //             Adding Poly Lines
        //
         */
    private void addPolylinesToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Log.d(TAG, "run: result routes: " + result.routes.length);

            for (DirectionsRoute route : result.routes) {
                Log.d(TAG, "run: leg: " + route.legs[0].toString());
                List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                List<LatLng> newDecodedPath = new ArrayList<>();

                // This loops through all the LatLng coordinates of ONE polyline.
                for (com.google.maps.model.LatLng latLng : decodedPath) {

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                    newDecodedPath.add(new LatLng(
                            latLng.lat,
                            latLng.lng
                    ));
                }
                Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                polyline.setColor(ContextCompat.getColor(getApplicationContext(), R.color.darkGrey));
                polyline.setClickable(true);

            }
        });
    }


//=====================================================================================================================================================================================================================

//    private void setCameraView() {
//        if (camZoom == 1) {
//            Toast.makeText(this, "Zoom", LENGTH_SHORT).show();
//            bottomBoundary = zoom.latitude - .1;
//            leftBoundary = zoom.longitude - .1;
//            topBoundary = zoom.latitude + .1;
//            rightBoundary = zoom.longitude + .1;
//            mMapBoundary = new LatLngBounds(
//                    new LatLng(bottomBoundary, leftBoundary),
//                    new LatLng(topBoundary, rightBoundary)
//            );
//
//            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 12));
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoom, 12));
//        }
//        camZoom = 2;
//    }
//=====================================================================================================================================================================================================================

    /*
    //              Map initialisation
    //
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            if (!success) {
                Toast.makeText(this, "Map Style Failed", LENGTH_SHORT).show();
                // Handle map style load failure
            }
        } catch (Resources.NotFoundException e) {
            // Oops, looks like the map style resource couldn't be found!
        }

        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            MarkerOptions mp = new MarkerOptions();

            mp.position(new LatLng(location.getLatitude(), location.getLongitude()));

            mp.title("my position");

            mMap.addMarker(mp);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 16));
            locuser = new LatLng(location.getLatitude(), location.getLongitude());

        });

        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_api_key))
                    .build();
        }
    }

    /*
    //             Custom Map Marker Function
    //
     */

    private BitmapDescriptor bitmapDescriptorFromVector(Context context) {

        Drawable vectorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_car);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getMinimumWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }


//=====================================================================================================================================================================================================================

    /*
    //             Getting Bus Location and showing it on map
    //
     */

    public void getfrom() {


        Toast.makeText(this, "get From initiated", LENGTH_SHORT).show();
        stopref.whereEqualTo("bus_stop_name", location_name_form).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                StopList stopList = documentSnapshot.toObject(StopList.class);

                String stop_name = stopList.getBus_stop_name();
                GeoPoint bus_stop_location = stopList.getBus_stop_location();
                from_location.setText(stop_name);
                Toast.makeText(MainMapActivity.this, "Bus Stop Name From " + stop_name, LENGTH_SHORT).show();
                Toast.makeText(MainMapActivity.this, "Bus Stop Location From " + bus_stop_location, LENGTH_SHORT).show();

                //                    Toast.makeText(MainMapActivity.this,  bus_that_come_here, Toast.LENGTH_SHORT).show();
                //                        ArrayList<String> buslist=  new ArrayList<String>();
                //                        buslist.add(bus_that_come_here);
                if (bus_from.isEmpty()) {

                    bus_from.addAll(stopList.getBus_that_come_here());

                } else {

                    bus_from.clear();
                    bus_from.addAll(stopList.getBus_that_come_here());

                }

                Log.d(TAG, "Bus From Array" + bus_from);
                getsearchresult();
            }
        });
    }

    public void getsearchresult() {

        Toast.makeText(this, "result Function Initiated", LENGTH_SHORT).show();
        stopref.whereEqualTo("bus_stop_name", location_name_to).whereArrayContainsAny("bus_that_come_here", bus_from).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                StopList stopList = documentSnapshot.toObject(StopList.class);
                Toast.makeText(MainMapActivity.this, "Success", LENGTH_SHORT).show();
                String stop_name_to = stopList.getBus_stop_name();
                GeoPoint bus_stop_location = stopList.getBus_stop_location();
                to_loccation.setText(stop_name_to);
                Toast.makeText(MainMapActivity.this, "Bus Stop Name To " + stop_name_to, LENGTH_SHORT).show();
                Toast.makeText(MainMapActivity.this, "Bus Stop Location To " + bus_stop_location, LENGTH_SHORT).show();


                //                        Toast.makeText(MainMapActivity.this,  bus_that_come_here, Toast.LENGTH_SHORT).show();
                //                        ArrayList<String> buslist = new ArrayList<String>();
                //                        buslist.add(bus_that_come_here);
                //                        buslist.stream().filter(bus_from::contains).collect(Collectors.toList());
                if (bus_to.isEmpty()){

                    bus_to.addAll(stopList.getBus_that_come_here());

                }
                else{

                    bus_to.clear();
                    bus_to.addAll(stopList.getBus_that_come_here());

                }
                bus_to.retainAll(bus_from);
                Log.d(TAG, "Bus To Common Array" + bus_to);

//                    StringBuilder builder = new StringBuilder();
//                    for (String value : bus_to) {
//                        builder.append(value);
//                    }
//
//                    String common_bus_number = builder.toString();
                String common_bus_number = bus_to.toString().replace("[", "").replace("]", "");

                common_bus.setText(common_bus_number);

                busref.whereEqualTo("bus_no", common_bus_number).get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                    for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots1) {
                        Toast.makeText(MainMapActivity.this, "common Bus initialed", LENGTH_SHORT).show();
                        BusList busListC = documentSnapshot1.toObject(BusList.class);

                        GeoPoint selected_bus = busListC.getBus_location();

                        LatLng latLng = new LatLng(selected_bus.getLatitude(), selected_bus.getLongitude());
                        Toast.makeText(MainMapActivity.this, "Common Bus Location" + latLng, LENGTH_SHORT).show();
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Common Bus" + common_bus_number).icon(bitmapDescriptorFromVector(getApplicationContext())));
                    }
                });

//                    for (String bus_that_come_here : stopList.getBus_that_come_here()) {
////                    Toast.makeText(MainMapActivity.this,  bus_that_come_here, Toast.LENGTH_SHORT).show();
//                        bus_to = Arrays.asList(bus_that_come_here);
//                        bus_to.retainAll(bus_from);
//                        String bus_found=  bus_to.toString();
//                        Toast.makeText(MainMapActivity.this, "Bus Found: "+bus_found, LENGTH_SHORT).show();
//                    }
            }

        });
    }

//    public void getBusNumber() {
//        db.collection("Bus List")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                if (task.getResult() != null) {
//                                    bus_num = document.getId();
//                                    final GeoPoint loc = document.getGeoPoint("bus_location");
//                                    final String Bus_num = bus_num;
////                                    Toast.makeText(MainMapActivity.this, "getNumber(): " + bus_num, Toast.LENGTH_LONG).show();
////                                    Toast.makeText(MainMapActivity.this, "Location loc Bus: "+loc, Toast.LENGTH_SHORT).show();
//                                    bus = db.collection("Bus List").document(bus_num);
//                                    bus.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                                        @Override
//                                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                                            if (e != null) {
//                                                Toast.makeText(MainMapActivity.this, "Error getBusList(): " + e, Toast.LENGTH_SHORT).show();
//                                            }
//                                            if (documentSnapshot != null && documentSnapshot.exists()) {
//                                                latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
//
//                                                zoom = latLng;
//                                                busList = new BusList(Bus_num, loc);
////                                                Toast.makeText(MainMapActivity.this, "Added Marker", Toast.LENGTH_SHORT).show();
//                                                marker = mMap.addMarker(new MarkerOptions().position(latLng).title(Bus_num).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_car)));
//                                                setCameraView();
//                                            }
//
//                                        }
//                                    });
//                                }
//                            }
//                        } else {
//                            Log.w("MainMainActivity", "Error getting documents.", task.getException());
//                        }
//                    }
//                });
//    }


//=====================================================================================================================================================================================================================

    /*
    //              Activity Life Cycle Factions
    //
     */
    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "Refreshed", LENGTH_SHORT).show();
//        getBusNumber();
    }


    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mAuth.signOut();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startUserLocationsRunnable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.signOut();
        stopLocationUpdates();

    }

    //=====================================================================================================================================================================================================================

    //=============================================Marker update code=====================================================================


    private void startUserLocationsRunnable() {
        Log.d(TAG, "startUserLocationsRunnable: starting runnable for retrieving updated locations.");
        mHandler.postDelayed(mRunnable = () -> {
//                Toast.makeText(MainMapActivity.this, "Auto Refreshed", Toast.LENGTH_SHORT).show();
//                getBusNumber();
            mHandler.postDelayed(mRunnable, LOCATION_UPDATE_INTERVAL);
        }, LOCATION_UPDATE_INTERVAL);
    }

    private void stopLocationUpdates() {
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(marker.getSnippet())
                .setCancelable(true).setMessage("Hi You have clicked on: " + marker.getTitle())
                .setPositiveButton("Yes", (dialog, id) -> {
//                        calculateDirections(marker);
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

//================================================================================================================================================================================================================================


}
