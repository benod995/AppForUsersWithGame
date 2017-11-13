package com.example.benodonnell.a3rdyearproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by benodonnell on 02/11/2017.
 */


public class SetGeofence extends Activity{

    private EditText lngInput;
    private EditText latInput;
    private EditText radiusInput;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_geofence);

        latInput = (EditText) findViewById(R.id.latText);
        lngInput = (EditText) findViewById(R.id.lngText);
        radiusInput = (EditText) findViewById(R.id.radiusText);

        final Button setGeo = (Button) findViewById(R.id.setGeo);

        setGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double lat = Double.parseDouble(String.valueOf(latInput.getText()));
                double lng = Double.parseDouble(String.valueOf(lngInput.getText()));
                int radius = Integer.parseInt(String.valueOf(radiusInput.getText()));
                setGeoFence(lat,lng,radius);

            }
        });
    }

    private void setGeoFence(double lat, double lng, int radius){

       FirebaseDatabase database = FirebaseDatabase.getInstance();
        //Getting the a reference to the data we need

        DatabaseReference dbRef = database.getReference("Patients/Patient1/GeoFence");

        dbRef.child("lat").setValue(lat);
        dbRef.child("lng").setValue(lng);
        dbRef.child("radius").setValue(radius);

        startActivity(new Intent(SetGeofence.this, MapsPage.class));
    }
}
