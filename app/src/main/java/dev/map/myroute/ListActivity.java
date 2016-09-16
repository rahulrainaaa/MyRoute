package dev.map.myroute;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;
    ArrayList<String> list = new ArrayList<>();
    ArrayAdapter<String> adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = (ListView)findViewById(R.id.listView);
        SharedPreferences s = getSharedPreferences(Constants.cacheName, MODE_PRIVATE);
        try
        {
            JSONArray jsonArray = new JSONArray(s.getString("data", "[]").toString());
            for(int i = 0; i < jsonArray.length(); i++)
            {
               list.add(jsonArray.getString(i).trim());
            }
        }
        catch (Exception e)
        {
            Toast.makeText(ListActivity.this, "Exception: Cannot parse list", Toast.LENGTH_SHORT).show();
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Constants.routeCache = list.get(position).trim();
        startActivity(new Intent(ListActivity.this, MapViewActivity.class));
    }
}
