package dev.map.myroute;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    ListView listView;
    ArrayList<String> list = new ArrayList<>();
    ArrayAdapter<String> adapter = null;
    boolean flagLong = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ListActivity.this, MapsActivity.class));
                finish();

            }
        });

        listView = (ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        refreshList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(flagLong)
        {
            flagLong = false;
            return;
        }
        Constants.route = list.get(position).trim();
        startActivity(new Intent(ListActivity.this, MapViewActivity.class));
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        flagLong = true;

        Snackbar.make(view, "Delete this route", Snackbar.LENGTH_LONG).setAction("Delete ?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = list.get(position).trim();
                JSONArray nJArray = new JSONArray();
                SharedPreferences s = getSharedPreferences(Constants.mainCache, MODE_PRIVATE);
                try
                {
                    JSONArray jarray = new JSONArray(s.getString("data", "[]"));
                    for(int i = 0; i < jarray.length(); i++)
                    {
                        String token = jarray.get(i).toString().trim();
                        if(token.equals(str))
                        {
                            continue;
                        }
                        nJArray.put(token.toString());
                    }
                }
                catch (Exception e)
                {

                }

                SharedPreferences.Editor se = getSharedPreferences(Constants.mainCache, MODE_PRIVATE).edit();
                se.putString("data", nJArray.toString());
                se.commit();
                se = getSharedPreferences(str.trim(), MODE_PRIVATE).edit();
                se.remove("data");
                se.commit();
                refreshList();

            }
        }).show();

        return false;
    }

    private void refreshList()
    {
        list.clear();
        SharedPreferences s = getSharedPreferences(Constants.mainCache, MODE_PRIVATE);
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
        adapter.notifyDataSetChanged();
    }
}
