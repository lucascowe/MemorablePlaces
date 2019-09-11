package com.example.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    static ArrayList<String> places = new ArrayList<String>();
    static ArrayList<LatLng> latLngs = new ArrayList<LatLng>();
    static ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        places.clear();
        latLngs.clear();
        ArrayList<String> lats = new ArrayList<>();
        ArrayList<String> longs = new ArrayList<>();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.memorableplaces", Context.MODE_PRIVATE);
        try {

            places = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",
                    ObjectSerializer.serialize(new ArrayList<String>())));
            lats = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lat",
                    ObjectSerializer.serialize(new ArrayList<String>())));
            longs = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lng",
                    ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (places.size() > 0 && lats.size() > 0 && longs.size() > 0) {
            if (places.size() == lats.size() && lats.size() == longs.size()) {
                for (int i = 0; i < lats.size(); i++) {
                    latLngs.add(new LatLng(Double.parseDouble(lats.get(i)), Double.parseDouble(longs.get(i))));
                }
            }
        } else {
            places.add("add a new place");
            latLngs.add(new LatLng(0,0));
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1,places);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("location",i);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}


