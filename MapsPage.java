package com.example.benodonnell.a3rdyearproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private ProgressDialog mProgress;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener fireBaseAuthListner;
    private GoogleMap mMap;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(fireBaseAuthListner);
    }

    public void onStop() {
        super.onStop();
        if (fireBaseAuthListner != null) {
            firebaseAuth.removeAuthStateListener(fireBaseAuthListner);
            firebaseAuth.signOut();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //Getting the map fragment by ID
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        /**
         * Getting which user is currently logged in
         */
        firebaseAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);
        fireBaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            }
        };

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /*
        *  Getting data from fire base
        *
        */

        database = FirebaseDatabase.getInstance();
        //Getting the a reference to the data we need
        reference = database.getReference("Patients/Patient1");
        //Adding the event listener

        //The map will update  when the data changes
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Getting the latitude

                //First get the location reference
                DataSnapshot locationSnap = dataSnapshot.child("Location");

                DataSnapshot latSnapshot = locationSnap.child("lat");
                double lat = latSnapshot.getValue(Double.class);

                //Getting the longitde
                DataSnapshot lngSnapshot = locationSnap.child("lng");
                double lng = lngSnapshot.getValue(Double.class);

                //Making the marker for the map
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(lat,lng));

                //Clearing the old marker
                mMap.clear();
                //adding the current marker
                mMap.addMarker(markerOptions);
                //Postioning the view over the marker
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lng)));

                //Setting the geofence as with cordinates in firebase
                DataSnapshot x = dataSnapshot.child("GeoFence");

                double geoFenceLat = x.child("lat").getValue(Double.class);
                double geoFenceLng = x.child("lng").getValue(Double.class);
                int geoFenceRadius = x.child("radius").getValue(Integer.class);

                //Adding the circle to the map
                Circle mCircle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(geoFenceLat, geoFenceLng))
                        .radius(geoFenceRadius)
                        .strokeColor(Color.RED)
                        .fillColor(Color.TRANSPARENT));

                double  dist = (int) calcualteDistance(lat, lng, geoFenceLat, geoFenceLng);
                String distFromRadius = String.valueOf(dist);
                Toast.makeText(MapsPage.this, distFromRadius , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    /*    mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location location) {
                float[] distance = new float[2];

                Location.distanceBetween( location.getLatitude(), location.getLongitude(),
                        mCircle.getCenter().latitude, mCircle.getCenter().longitude, distance);

                if( distance[0] > mCircle.getRadius() ){
                    Toast.makeText(getBaseContext(), "Outside, distance from center: " + distance[0] + " radius: " + mCircle.getRadius(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "Inside, distance from center: " + distance[0] + " radius: " + mCircle.getRadius() , Toast.LENGTH_LONG).show();
                }

            }
        });
*/

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Method is called when map is ready
     * Assigns mMap a value (the map)
     * @param googleMap
     */
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(14);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {
            Intent i = new Intent(MapsPage.this, Game.class);
            startActivity(i);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            Intent i = new Intent(MapsPage.this, SetGeofence.class);
            startActivity(i);
        } else if (id == R.id.nav_send) {

            if (fireBaseAuthListner != null) {

                firebaseAuth.removeAuthStateListener(fireBaseAuthListner);
                mProgress.setMessage("Signing out......");
                mProgress.show();
                firebaseAuth.signOut();
                Intent i = new Intent(MapsPage.this, LoginPage.class);
                startActivity(i);
            }


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Calcustes the distance beween to points on a map and returns distance as a double
     * @param latA Lattitude of the point A
     * @param lonA Longitude of point A
     * @param latB Latitude of point B
     * @param lonB Longitiude of point B
     *
     * @return The distance between the two points
     */
    private double calcualteDistance(double latA, double lonA, double latB, double lonB ){
        double res = 0;
        final int RADIUS_OF_EARTH = 6371;
        // changing to radians
        double dLat = Math.toRadians(latB - latA);
        double dLon = Math.toRadians(lonB - lonA);

        latA = Math.toRadians(latA);
        latB = Math.toRadians(latB);
        // equation itslef
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(latA) * Math.cos(latB);
        double c = 2 * Math.asin(Math.sqrt(a));
        res =  RADIUS_OF_EARTH * c;

        return res;
    }
}
