package com.example.sunny.madproject;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleMap mGoogleMap;
    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleServiceAvailable()) {
            Toast.makeText(this, " Perfect!!!", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_main);
            initMap();
        }

    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.getMapAsync(this);

    }

    public boolean googleServiceAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int isAvailable = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApiAvailability.isUserResolvableError(isAvailable)) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, isAvailable, 0);
            dialog.show();

        } else {
            Toast.makeText(this, " Cannot connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mGoogleMap = googleMap;

        if(mGoogleMap!=null)
        {
            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    View view= getLayoutInflater().inflate(R.layout.info_window,null);

                    TextView tvLocality = (TextView) view.findViewById(R.id.tv_locality);
                    TextView tvlat = (TextView) view.findViewById(R.id.tv_lat);
                    TextView tvlng = (TextView) view.findViewById(R.id.tv_lng);
                    TextView tvsnippet = (TextView) view.findViewById(R.id.tv_snippet);

                    LatLng latLng = marker.getPosition();
                    tvLocality.setText(marker.getTitle());
                    tvlat.setText("Latitude: " + latLng.latitude);
                    tvlng.setText("longitude: " + latLng.longitude);
                    tvsnippet.setText(marker.getSnippet());

                    return view;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });
            {

            }
        }
        //goToLocationZoom(37.008224,-79.8984527, 15);
        locationButton();
       /* mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap
                googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();*/
    }

    private void locationButton() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            mGoogleMap.setMyLocationEnabled(true);
            return;
        }
        else
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 101:
            {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        mGoogleMap.setMyLocationEnabled(true);
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    }
                    else
                    {
                        Toast.makeText(this, "Need Location", Toast.LENGTH_LONG);
                    }
                }
            }
        }
    }

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mGoogleMap.animateCamera(cameraUpdate);


    }
    Marker marker;

    public void geoLocate(View view) throws IOException {
        EditText editText = (EditText) findViewById(R.id.editText);
        String location = editText.getText().toString();

        Geocoder geocoder = new Geocoder(this);

        List<Address> list = geocoder.getFromLocationName(location, 1);
        Address address = list.get(0);

        String locality = address.getLocality();
        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

        double lat = address.getLatitude();
        double lng = address.getLongitude();

        goToLocationZoom(lat, lng, 15);

        setMarker(locality, lat, lng);



    }

    private void setMarker(String locality, double lat, double lng) {
        if(marker!=null)
        {
            marker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions()
                    .title(locality)
                    //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.image))
                    .position(new LatLng(lat, lng))
                    .snippet("Im here");
        marker = mGoogleMap.addMarker(markerOptions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapTypeNone:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;

            case R.id.mapTypeNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;

            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;

            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;

            case R.id.mapTypeHybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    LocationRequest locationRequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null)
        {
            Toast.makeText(this, "Cant get current Location", Toast.LENGTH_LONG).show();
        }
        else {
            LatLng latLng= new LatLng(location.getLatitude(),location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,15);
            mGoogleMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
