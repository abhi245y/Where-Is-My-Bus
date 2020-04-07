package com.abhi.testingwhb;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.abhi.testingwhb.model.BusListModel;
import com.abhi.testingwhb.model.BusStopList_BusList;
import com.abhi.testingwhb.model.SearchStopModel;
import com.abhi.testingwhb.model.StopList;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;
import static com.google.android.gms.maps.model.JointType.BEVEL;

public class MainMapActivity extends FragmentActivity implements OnMapReadyCallback {


    //Static Variables
    public static final String TAG = "MainMapActivity";
    private static final int LOCATION_UPDATE_INTERVAL = 3000;

    //Maps and Location Related
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public LatLng to_location_latlang_on_Db, from_location_latlang_on_Db, user_location, origin, destination;

    //Firebase & Firestore Related Variables
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public FirebaseAuth mAuth;
    public CollectionReference stopref = db.collection("Stop");
    public CollectionReference busref = db.collection("Bus List");
    public CollectionReference userref = db.collection(" User Location");

    //Views & Layouts
    private ConstraintLayout loading_screen;
    private RelativeLayout from_layout, to_layout, viewPagerLay;
    private AutoCompleteTextView from_editText, to_ediText;
    private BottomSheetBehavior bottomSheetBehavior;
    private ViewPager2 viewPager2;
    private ViewPager2Adapter vPAdapter;
    private Button search_btn;

    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    //Model class
    public List<BusListModel> busListModels = new ArrayList<>();

    //ArrayList and List Variables
    private ArrayList<String> stopList = new ArrayList<>();
    public ArrayList<String> bus_from = new ArrayList<>();
    public ArrayList<String> bus_to = new ArrayList<>();
    private ArrayList<Polyline> polylines = new ArrayList<>();
    private ArrayList<Marker> markerList = new ArrayList<>();
    private ArrayList<ArrayList<LatLng>> common_waypoints_array = new ArrayList<>();
    private ArrayList<ArrayList<String>> common_bus_array = new ArrayList<>();
    private ArrayList<LatLng> all_waypoints = new ArrayList<>();
    private ArrayList<String> all_bus = new ArrayList<>();
    ArrayList<Integer> from_stop_num = new ArrayList<>();
    ArrayList<Integer> to_stop_num = new ArrayList<>();

    //Normal Variables
    public String From_location_name, To_location_name;
    private int location_from_stop_num, location_to_stop_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        loading_screen = findViewById(R.id.loading_screen);
        from_editText = findViewById(R.id.from_search_editText);
        to_ediText = findViewById(R.id.to_editText);
        from_layout = findViewById(R.id.include);
        to_layout = findViewById(R.id.include2);
        viewPagerLay = findViewById(R.id.viewPagerLay);
        search_btn = findViewById(R.id.search_button);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getData();

    }

    /**
     * Other Fuctiosn like to clear data and all
     */
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void clearCards() {
        int size = busListModels.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                busListModels.remove(0);
            }

            vPAdapter.notifyItemRangeRemoved(0, size);
        }
    }

    public boolean clearAll() {

        location_from_stop_num = 0;
        location_to_stop_num = 0;
        common_waypoints_array.clear();
        all_bus.clear();
        all_waypoints.clear();
        common_bus_array.clear();
        from_stop_num.clear();
        to_stop_num.clear();
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();
        for (Marker marker : markerList) {
            marker.remove();
        }
        markerList.clear();
        Toast.makeText(this, "Cleared", LENGTH_SHORT).show();
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            if (!success) {
                Toast.makeText(this, "Map Style Failed", LENGTH_SHORT).show();
            }
        } catch (Resources.NotFoundException e) {
            // Oops, looks like the map style resource couldn't be found!
        }

        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                MarkerOptions mp = new MarkerOptions();

                mp.position(new LatLng(location.getLatitude(), location.getLongitude()));

                mp.title("my position");

                mMap.addMarker(mp);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 16));
                user_location = new LatLng(location.getLatitude(), location.getLongitude());

            }
        });
    }

    public void sign_out() {
        userref.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainMapActivity.this, "Sign out Failed: " + e.toString(), LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseAuth.getInstance().getCurrentUser().delete().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainMapActivity.this, "Deleting User From Database Failed: " + e.toString(), LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startUserLocationsRunnable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sign_out();
        stopLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void startUserLocationsRunnable() {
        Log.d(TAG, "startUserLocationsRunnable: starting runnable for retrieving updated locations.");
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(mRunnable, LOCATION_UPDATE_INTERVAL);
            }
        }, LOCATION_UPDATE_INTERVAL);
    }

    private void stopLocationUpdates() {
        mHandler.removeCallbacks(mRunnable);
    }


    /**
     * Strting Initial step Getting Data and Building Card
     */


    private void getData() {

        stopref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (documentSnapshot.exists()) {
                        SearchStopModel searchStopModel = documentSnapshot.toObject(SearchStopModel.class);
                        stopList.add(searchStopModel.getBus_stop_name());

                        searchStopModel.setBus_stop_name(String.valueOf(stopList));
                    }
                }
                //  Log.d(TAG, "Log Step 1: Getting Data:" + stopList);
                from_layout.setVisibility(View.VISIBLE);
            }
        });

        ArrayAdapter<String> adapterStop = new ArrayAdapter<>(this,
                R.layout.custom_stop_list_item, R.id.text_view_stop_list_item, stopList);
        from_editText.setAdapter(adapterStop);
        to_ediText.setAdapter(adapterStop);

        from_editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                From_location_name = parent.getItemAtPosition(position).toString();
                getfrom();
            }
        });

        to_ediText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                closeKeyboard();
                To_location_name = parent.getItemAtPosition(position).toString();
                clearCards();
                clearAll();
                search_btn.setVisibility(View.GONE);
                loading_screen.setVisibility(View.VISIBLE);
                if (clearAll()) {
                    getTo();
                }
            }
        });
    }

    /**
     * Get Details of Origin Bus
     */

    public void getfrom() {

        stopref.whereEqualTo("bus_stop_name", From_location_name).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    StopList stopList = documentSnapshot.toObject(StopList.class);

                    GeoPoint bus_stop_location = stopList.getBus_stop_location();
                    from_location_latlang_on_Db = new LatLng(bus_stop_location.getLatitude(), bus_stop_location.getLongitude());


                    if (bus_from.isEmpty()) {
                        bus_from.addAll(stopList.getBus_that_come_here());
                    } else {
                        bus_from.clear();
                        bus_from.addAll(stopList.getBus_that_come_here());
                    }

                    Log.d(TAG, "Log getting step3 : getFrom" + bus_from);
                }
                to_layout.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Get Details of Destination  Bus
     */

    public void getTo() {

        stopref.whereEqualTo("bus_stop_name", To_location_name).whereArrayContainsAny("bus_that_come_here", bus_from).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    StopList stopList = documentSnapshot.toObject(StopList.class);


                    GeoPoint bus_stop_location = stopList.getBus_stop_location();
                    to_location_latlang_on_Db = new LatLng(bus_stop_location.getLatitude(), bus_stop_location.getLongitude());


                    if (bus_to.isEmpty()) {

                        bus_to.addAll(stopList.getBus_that_come_here());

                    } else {

                        bus_to.clear();
                        bus_to.addAll(stopList.getBus_that_come_here());

                    }

                }

                Log.d(TAG, "Log step 3 : getTo: " + bus_to);
                sorting();
            }
        });
    }

    public void sorting() {
        bus_to.retainAll(bus_from);
        Log.d(TAG, "Log  step 4 :Common bus Numbers : " + bus_to);

        ArrayList<Integer> temp = new ArrayList<>();

        for (int i = 0; i < bus_to.size(); i++) {
            String bus_no = bus_to.get(i);
            int finalI = i;
            Log.d(TAG, "Log sorting: FinalI " + finalI + " Step: " + finalI);
            Log.d(TAG, "Log sorting: bus_no " + bus_no + " Step: " + finalI);


            busref.document(bus_no).collection("Bus stop list").whereEqualTo("bus_stop_name", From_location_name).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        BusStopList_BusList busStopListModel = documentSnapshot.toObject(BusStopList_BusList.class);
                        location_from_stop_num = busStopListModel.getBus_stop_number();
                    }
                    Log.d(TAG, "Log sorting: location_to_stop_num: " + location_to_stop_num + " Step: " + finalI);

                    busref.document(bus_no).collection("Bus stop list").whereEqualTo("bus_stop_name", To_location_name).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots12) {
                            for (QueryDocumentSnapshot documentSnapshot12 : queryDocumentSnapshots12) {
                                BusStopList_BusList busStopListModel = documentSnapshot12.toObject(BusStopList_BusList.class);
                                location_to_stop_num = busStopListModel.getBus_stop_number();
                            }
                            Log.d(TAG, "Log sorting: location_from_stop_num: " + location_from_stop_num + " Step: " + finalI);
                            get_bus_stop_locations(bus_no, location_from_stop_num, location_to_stop_num, finalI);
                        }
                    });

                }
            });


        }
    }

    public void get_bus_stop_locations(String bus_no, int stop_num_from, int stop_num_to, int finalI) {

        ArrayList<String> bus_common_to = new ArrayList<>();
        ArrayList<LatLng> waypoints_common_to = new ArrayList<>();

        bus_common_to.clear();
        waypoints_common_to.clear();
        busref.document(bus_no).collection("Bus stop list").orderBy("bus_stop_number").startAt(stop_num_from).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "===========================================  Step: " + finalI + "============================================");
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    BusStopList_BusList busStopListModel = documentSnapshot.toObject(BusStopList_BusList.class);
                    all_bus.add(busStopListModel.getBus_stop_name());
                    GeoPoint geoPoint = busStopListModel.getBus_stop_location();
                    all_waypoints.add(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
                }
                Log.d(TAG, "Log get_bus_stop_locations: all_bud:  " + all_bus + "Step: " + finalI);
                busref.document(bus_no).collection("Bus stop list").orderBy("bus_stop_number").limit(stop_num_to).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots2) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots2) {
                            BusStopList_BusList busStopListModel = documentSnapshot.toObject(BusStopList_BusList.class);
                            bus_common_to.add(busStopListModel.getBus_stop_name());
                            GeoPoint geoPoint = busStopListModel.getBus_stop_location();
                            waypoints_common_to.add(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
                        }
                        Log.d(TAG, "Log get_bus_stop_locations: bus_common_to Before " + bus_common_to + "Step: " + finalI);
                        Log.d(TAG, "                                ");
                        bus_common_to.retainAll(all_bus);
                        waypoints_common_to.retainAll(all_waypoints);
                        Log.d(TAG, "Log get_bus_stop_locations: bus_common_to After " + bus_common_to + "Step: " + finalI);
                        Log.d(TAG, "                                ");
                        calcDistance(waypoints_common_to, bus_no,bus_common_to);

                        Log.d(TAG, "===========================================  Step: " + finalI + "============================================");

                    }
                });

            }
        });
    }


    public void calcDistance(ArrayList<LatLng> waypoints, String bus_no, ArrayList<String> bus_common_to) {

        Log.d(TAG, "Log calcDistance: calcDistance initiated");
        waypoints.remove(from_location_latlang_on_Db);
        waypoints.remove(to_location_latlang_on_Db);

        Log.d(TAG, "Log calcDistance:  waypoints set: " + waypoints);
        GoogleDirection.withServerKey("AIzaSyCnkn0VsVabsU0rPFVq8Kh2bpHNIMu9Msc")
                .from(from_location_latlang_on_Db).and(waypoints)
                .to(to_location_latlang_on_Db).transportMode(TransportMode.DRIVING).alternativeRoute(false)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction) {
                        if (direction.isOK()) {
                            Log.d(TAG, "Log calcDistance: Direction Result calculated" + direction);

                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            Info distanceInfo = leg.getDistance();
                            Info durationInfo = leg.getDuration();
                            String distance = distanceInfo.getText();
                            String duration = durationInfo.getText();
                            Log.d(TAG, "Log duration: " + duration);
                            Log.d(TAG, "Log distance: " + distance);
                            createBusList(distance, duration, bus_no,bus_common_to);

                        } else {
                            Log.d(TAG, "Log calcDistance: Direction  not calculated" + direction);
                        }

                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {

                        Log.d(TAG, "Log calcDistance: Direction Error" + t.toString());

                    }
                });
    }

    public void createBusList(String distance, String duration, String bus_no, ArrayList<String> bus_common_to) {
        String fair = "25 Rs";

        bus_common_to.remove(From_location_name);
        bus_common_to.remove(To_location_name);
        String intermediateStops=bus_common_to.toString().replace("[", "").replace("]", "");;


        busListModels.add(new BusListModel(" " + distance, duration, From_location_name, intermediateStops, To_location_name, bus_no, fair));
        Log.d(TAG, "Log  createBusList :Created bus list data: " + busListModels);

        search_btn.setVisibility(View.VISIBLE);
        loading_screen.setVisibility(View.GONE);
    }

    /**
     * Initial step Getting From Card, Getting Route and Drawing polyline
     */


    public void Search(View view) {

        vPAdapter = new ViewPager2Adapter(busListModels);
        viewPagerLay.setVisibility(View.VISIBLE);
        viewPager2 = findViewById(R.id.list_view);
        viewPager2.setAdapter(vPAdapter);
        viewPager2.setPageTransformer(new ZoomOutPageTransformer());
//        vPAdapter.setOnItemClickListener(new ViewPager2Adapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//
//            }
//        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                getResult(position);
                loading_screen.setVisibility(View.VISIBLE);
            }
        });
        Log.d(TAG, "Log Search :Created Recycler view ");
    }

    public void getResult(int position) {
        clearAll();
        BusListModel pos = busListModels.get(position);
        String cardResult = pos.getBusNo();
        String From_bus = pos.getOrigin();
        String To_bus = pos.getDestination();
        Log.d(TAG, "Log  getResult : Got Bus Result: " + cardResult);
        Final_sorting(cardResult, From_bus, To_bus);
    }

    private void Final_sorting(String cardResult, String from_bus, String to_bus) {

        busref.document(cardResult).collection("Bus stop list").whereEqualTo("bus_stop_name", from_bus).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    BusStopList_BusList busStopListModel = documentSnapshot.toObject(BusStopList_BusList.class);
                    GeoPoint geoPoint = busStopListModel.getBus_stop_location();
                    origin = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                    location_from_stop_num = busStopListModel.getBus_stop_number();
                }


                busref.document(cardResult).collection("Bus stop list").whereEqualTo("bus_stop_name", to_bus).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots12) {
                        for (QueryDocumentSnapshot documentSnapshot12 : queryDocumentSnapshots12) {
                            BusStopList_BusList busStopListModel = documentSnapshot12.toObject(BusStopList_BusList.class);
                            GeoPoint geoPoint = busStopListModel.getBus_stop_location();
                            destination = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                            location_to_stop_num = busStopListModel.getBus_stop_number();
                        }
                        Log.d(TAG, "Log Getting From and to Stop Numbers" + location_from_stop_num + " " + location_to_stop_num);
                        getting_bus_stop_locations(cardResult, location_from_stop_num, location_to_stop_num, origin, destination);
                    }
                });

            }
        });

    }

    private void getting_bus_stop_locations(String cardResult, int from_num, int to_num, LatLng origin, LatLng destination) {

        ArrayList<String> bus_common_to = new ArrayList<>();
        ArrayList<String> all_bus = new ArrayList<>();
        all_bus.clear();
        bus_common_to.clear();
        busref.document(cardResult).collection("Bus stop list").orderBy("bus_stop_number").startAt(from_num).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    BusStopList_BusList busStopListModel = documentSnapshot.toObject(BusStopList_BusList.class);
                    all_bus.add(busStopListModel.getBus_stop_name());

                }

                busref.document(cardResult).collection("Bus stop list").orderBy("bus_stop_number").limit(to_num).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots2) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots2) {
                            BusStopList_BusList busStopListModel = documentSnapshot.toObject(BusStopList_BusList.class);
                            bus_common_to.add(busStopListModel.getBus_stop_name());
                        }
                        bus_common_to.retainAll(all_bus);
                        Log.d(TAG, "Log getting_bus_stop_locations " + bus_common_to);
                        adding_markers(bus_common_to, origin, destination);
                    }
                });

            }
        });
    }

    private void adding_markers(ArrayList<String> bus_common, LatLng origin, LatLng destination) {

        ArrayList<LatLng> waypoints = new ArrayList<>();
        for (String bus : bus_common) {
            Log.d(TAG, "Log adding_markers: bus name: " + bus);
            stopref.whereEqualTo("bus_stop_name", bus).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        StopList stopList = documentSnapshot.toObject(StopList.class);
                        String stop_name = stopList.getBus_stop_name();
                        GeoPoint geoPoint = stopList.getBus_stop_location();
                        LatLng position = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                        waypoints.add(position);
                        Marker stop_marker = mMap.addMarker(new MarkerOptions().position(position).title(stop_name));
                        markerList.add(stop_marker);
                    }
                    if (waypoints.size() == bus_common.size()) {
                        finalDirectionCalculation(waypoints, origin, destination);
                    }
                }
            });
        }

    }

    private void finalDirectionCalculation(ArrayList<LatLng> waypoints, LatLng origin, LatLng destination) {
        waypoints.remove(origin);
        waypoints.remove(destination);

        for (LatLng wayp : waypoints) {
            Log.d(TAG, "Log finalDirectionCalculation "+wayp);
        }

        GoogleDirection.withServerKey("AIzaSyCnkn0VsVabsU0rPFVq8Kh2bpHNIMu9Msc")
                .from(origin).and(waypoints)
                .to(destination).transportMode(TransportMode.DRIVING).alternativeRoute(false)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction) {
                        if (direction.isOK()) {
                            DrawPolyline(direction);
                            Log.d(TAG, "Log Direction Result : calculated" + direction);

                        } else {
                            Log.d(TAG, "Log Direction Result : not ok");

                        }

                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {

                        Log.d(TAG, "Log Direction Result : Error" + t.toString());

                    }
                });
    }

    private void DrawPolyline(Direction direction) {
        Route route = direction.getRouteList().get(0);
        int legCount = route.getLegList().size();
        for (int i = 0; i < legCount; i++) {

            Leg leg = route.getLegList().get(i);
            List<Step> stepList = leg.getStepList();

            List<PolylineOptions> polylineOptions = DirectionConverter.createTransitPolyline(this, stepList, 2, Color.rgb(41, 118, 195), 3, Color.BLUE);

            for (PolylineOptions polylineOption : polylineOptions) {
                polylines.add(mMap.addPolyline(polylineOption));

            }
            for (Polyline polyline : polylines) {
                polyline.setWidth(25);
                polyline.setJointType(BEVEL);
                polyline.setStartCap(new RoundCap());
                polyline.setEndCap(new RoundCap());
                zoomRoute(polyline.getPoints());
            }

        }
        Log.d(TAG, "Log DrawPolyline");
    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
        loading_screen.setVisibility(View.GONE);
    }


//===========================================       End Of Main Class     ===============================================================================================================================================
}





