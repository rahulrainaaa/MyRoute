package dev.map.myroute;

import android.widget.ArrayAdapter;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Constants class
 */
public class Constants
{
    public static String cacheName = "routelist";       //main cache stores cache names
    public static String routeCache = "routecache";     //stores the path individually
    public static ArrayList<LatLng> list = null;        //list to show route on map
}
