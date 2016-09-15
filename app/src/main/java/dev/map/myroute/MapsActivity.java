package dev.map.myroute;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener {

    //Map and Location objects
    private GoogleMap mMap;
    private LocationManager locationManager = null;
    private Polyline line = null;

    //Map point markers
    private Marker markerStart = null;
    private Marker markerEnd = null;

    //LatLng points
    private LatLng locStart = null;
    private LatLng locEnd = null;
    private ArrayList<LatLng> listPoints = null;

    //flags
    private boolean flagFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        //Check for permissions
        int check1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int check2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        if((check1 != PackageManager.PERMISSION_GRANTED) || (check2 != PackageManager.PERMISSION_GRANTED))
        {
            Toast.makeText(getApplicationContext(), "Go to permissions and \nEnable all permission", Toast.LENGTH_LONG).show();
            startInstalledAppDetailsActivity();
            return;
        }

        //Reset all data and flags
        locStart = null;
        locEnd = null;
        listPoints = new ArrayList<LatLng>();

        //Initialize location listener
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //Check if GPS Sensor is on?
        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
        {
            Toast.makeText(getApplicationContext(), "Please Enable GPS Sensor", Toast.LENGTH_LONG).show();
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
        Toast.makeText(getApplicationContext(), "Fetching Current Location", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause()
    {
        //Remove the location listener callback
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(this);
        }
        locationManager = null;
        if(listPoints != null)
        {
            listPoints.clear();
            listPoints = null;
        }
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        //Get location and update map pin
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        locEnd = new LatLng(latitude, longitude);
        //Check if refreshed first time.
        if(flagFirst)
        {
            listPoints.add(new LatLng(latitude, longitude));
            locStart = new LatLng(latitude, longitude);
            markerStart = mMap.addMarker(new MarkerOptions().position(locStart).title("Start").icon(BitmapDescriptorFactory.fromResource(R.drawable.ba)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locStart));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            flagFirst = false;
            markerEnd = mMap.addMarker(new MarkerOptions().position(locEnd).title("End").icon(BitmapDescriptorFactory.fromResource(R.drawable.bb)));
        }
        else
        {
            listPoints.add(new LatLng(latitude, longitude));
            markerEnd.setPosition(locEnd);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locEnd));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            drawRoute();
        }
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private void drawRoute()
    {
        //Get all points and plot the polyLine route.

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        Iterator<LatLng> iterator = listPoints.iterator();
        while(iterator.hasNext())
        {
            LatLng data = iterator.next();
            options.add(data);
        }

        //If line not null then remove old polyline routing.
        if(line != null)
        {
            line.remove();
        }
        line = mMap.addPolyline(options);

        //Focus on map bounds
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(list.get(0).getLatLgnBounds().getCenter()));
//        LatLngBounds.Builder builder = new LatLngBounds().Builder();
//        builder.include(locStart);
//        builder.include(locEnd);
//        LatLngBounds bounds = builder.build();
//        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }

    /**
     * @method startInstalledAppDetailsActivity
     * @desc: launches the permission/setting intent UI for this app to assist for enabling permissions.
     */
    private void startInstalledAppDetailsActivity()
    {
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
    }
}
