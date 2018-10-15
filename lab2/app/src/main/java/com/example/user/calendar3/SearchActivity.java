package com.example.user.calendar3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class SearchActivity extends AppCompatActivity {


    private Context context;
    private EventAdapter eventAdapter;
    private ListView listView;
    private TextView searchText;
    private ArrayList<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = getApplicationContext();
        listView = findViewById(R.id.list);
        searchText = findViewById(R.id.searchText);

        File jsonFile = new File(context.getFilesDir(), "data.json");
        String str = readFromFile(jsonFile);

        String toSearch = getIntent().getStringExtra("search");
        eventList = new ArrayList<>();

        searchText.setText(toSearch);

        try {
            JSONObject json = new JSONObject(str);
            Iterator<String> temp = json.keys();

            while (temp.hasNext()) {
                String key = temp.next();
                JSONArray arr = json.getJSONArray(key);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject event = arr.getJSONObject(i);
                    if (event.getString("title").toLowerCase().contains(toSearch.toLowerCase()))
                    {
                        eventList.add(new Event(arr.getJSONObject(i).getString("title"),
                                arr.getJSONObject(i).getInt("hour"),
                                arr.getJSONObject(i).getInt("minute"),
                                key, i));
                    }

                }
            }

            eventAdapter = new EventAdapter(this, eventList);
            listView.setAdapter(eventAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(SearchActivity.this, EditActivity.class);
                    intent.putExtra("position", eventList.get((int) id).getPosition().toString());
                    intent.putExtra("date", eventList.get((int) id).getDate());
                    startActivityForResult(intent, 1);
                }
            });


//            JSONArray arr = json.getJSONArray(getIntent().getStringExtra("date"));


//            JSONObject element = arr.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String readFromFile(File file) {
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(true){
            Intent refresh = new Intent(this, SearchActivity.class);
            refresh.putExtra("search", getIntent().getStringExtra("search"));
            startActivity(refresh);
            this.finish();
        }
    }
}
