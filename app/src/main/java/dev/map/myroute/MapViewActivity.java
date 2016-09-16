package dev.map.myroute;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Switch;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {

    //Map and Location objects
    private GoogleMap mMap;
    private Polyline line = null;

    //Map point markers
    private Marker markerStart = null;
    private Marker markerEnd = null;

    //LatLng points
    private LatLng locStart = null;
    private LatLng locEnd = null;

    public ArrayList<LatLng> list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        drawRoute();
    }

    /**
     * @method drawRoute
     * @desc plots the routed path on google map from listPoints (ArrayList points LatLng).
     */
    private void drawRoute()
    {

        SharedPreferences s = getSharedPreferences(Constants.route, MODE_PRIVATE);
        String str = s.getString("data", "[]");
        list = new ArrayList<LatLng>();
        try
        {
            JSONArray jarray = new JSONArray(str);
            for(int i = 0; i < jarray.length(); i++)
            {
                JSONObject json = jarray.getJSONObject(i);
                double lat = json.getDouble("lat");
                double lng = json.getDouble("lng");
                list.add(new LatLng(lat, lng));
            }
        }
        catch (Exception e)
        {
            Toast.makeText(MapViewActivity.this, "Exception: parsing error", Toast.LENGTH_SHORT).show();
        }

        if(list.size() < 1)
        {
            Toast.makeText(MapViewActivity.this, "Empty", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //Get all points and plot the polyLine route.
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        Iterator<LatLng> iterator = list.iterator();
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

        double startLat = list.get(0).latitude;
        double startLng = list.get(0).longitude;
        double endLat = list.get(list.size() - 1).latitude;
        double endLng = list.get(list.size() - 1).longitude;

        locStart = new LatLng(startLat, startLng);
        markerStart = mMap.addMarker(new MarkerOptions().position(locStart).title("Start").icon(BitmapDescriptorFactory.fromResource(R.drawable.ba)));

        locEnd = new LatLng(endLat, endLng);
        markerEnd = mMap.addMarker(new MarkerOptions().position(locEnd).title("End").icon(BitmapDescriptorFactory.fromResource(R.drawable.bb)));

        //Focus on map bounds
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(locStart);
        builder.include(locEnd);
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

    }

}
