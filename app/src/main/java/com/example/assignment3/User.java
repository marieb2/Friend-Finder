package com.example.assignment3;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;

public class User {

    private String email;
    private String name;
    private double latitude;
    private double longitude;

    public User(){

    }

    public User(String name_, String email, double latitude_, double longitude_) {
        this.name = name_;
        this.latitude = latitude_;
        this.longitude = longitude_;

    }

    public String getName(){
        return name;
    }

   public double getLatitude(){

        return latitude;

   }

   public double getLongitude(){
        return longitude;
   }

}
