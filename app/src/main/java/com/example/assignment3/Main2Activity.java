package com.example.assignment3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math;


public class Main2Activity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;

    LocationManager locationManager;

    private LatLng currentLocation;

    private Location lastLocation;

    private FusedLocationProviderClient fusedLocationClient;

    CircleOptions circleOptions = new CircleOptions();
    Circle mapCircle;

    String email;

    Button locateFriends;

    private DatabaseReference mDatabase;

    double longitude;
    double latitude ;
    String id;

    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;

    Location loc;

    String newUser;

    private List<User> userList_;

    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        userList_ = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            //return;
        }

        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, (LocationListener) this);

/*
        if(isGPS) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
        }else if(isNetwork){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) this);
        }*/
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        id = intent.getStringExtra("userID");
        newUser = intent.getStringExtra("newUser");

        //FirebaseUser user = mDatabase.

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Logic to handle location object
                    mMap.setMinZoomPreference(18);
                    LatLng newLocation;
                    newLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    currentLocation = newLocation;

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    //Log.d("location1", "location in onSuccess lat:"+latitude+" long:"+longitude);


                    lastLocation = location;


                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("You"));

                    //mMap.addMarker(new MarkerOptions().position(newLocation).title("Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
                    mMap.resetMinMaxZoomPreference();
                } else {
                    Log.d("NullLocation", "The last known location is NULL");
                }

            }
        });

        mDatabase.child(id).child("latitude").setValue(latitude);
        mDatabase.child(id).child("longitude").setValue(longitude);


    }

    @Override
    public void onLocationChanged(Location location) {


        mDatabase.child(id).child("latitude").setValue(latitude);
        mDatabase.child(id).child("longitude").setValue(longitude);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //clear old markers
                //get array of all locations
                //check if its the user (don't place marker)
                //do math to see if they are within 1 mile
                //make new markers of the ones that are

                mMap.clear();

                for(DataSnapshot user: dataSnapshot.getChildren()){

                    User tmpUser = user.getValue(User.class);

                        double lat1 = latitude;
                        double lat2 = tmpUser.getLatitude();
                        double lon1 = longitude;
                        double lon2 = tmpUser.getLongitude();

                        double R = 6371e3; // metres
                        double phi1 = Math.toRadians(lat1);
                        double phi2 = Math.toRadians(lat2);
                        double deltaPhi = Math.toRadians(lat2-lat1);
                        double deltaLon = Math.toRadians(lon2-lon1);

                        double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) +
                                Math.cos(phi1) * Math.cos(phi2) *
                                        Math.sin(deltaLon/2) * Math.sin(deltaLon/2);
                        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

                        double distance = R * c;

                        if(distance <= 1609.34){

                            //add to list
                            userList_.add(user.getValue(User.class));
                            LatLng userLoc = new LatLng(user.getValue(User.class).getLatitude(), user.getValue(User.class).getLongitude());
                            mMap.addMarker(new MarkerOptions().position(userLoc).title(tmpUser.getName()));
                            mMap.resetMinMaxZoomPreference();
                        }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
