package com.example.user.calendar3;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
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

public class ListActivity extends AppCompatActivity {
    private Button addButton;
    private TextView dateText;
    private ListView listView;
    private Context context;
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        addButton = findViewById(R.id.button);
        dateText = findViewById(R.id.dateText);
        listView = findViewById(R.id.list);
        context = getApplicationContext();

        dateText.setText(getIntent().getStringExtra("date"));

        JSONObject jObj = null;
        StringBuilder text = new StringBuilder();
        try {

            File file = new File(context.getFilesDir(), "data.json");

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close() ;
        }catch (IOException e) {
            e.printStackTrace();
        }

        try {
            jObj = new JSONObject(text.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (text.toString() != "") {
            try {
                jObj = new JSONObject(text.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                jObj = new JSONObject("{}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        JSONArray arr = null;
        try {
            arr = jObj.getJSONArray(getIntent().getStringExtra("date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<Event> eventList = new ArrayList<>();
        if (jObj.has(getIntent().getStringExtra("date"))) {
            for (int i = 0; i < arr.length(); i++) {
                try {
                    eventList.add(new Event(arr.getJSONObject(i).getString("title"),
                            arr.getJSONObject(i).getInt("hour"),
                            arr.getJSONObject(i).getInt("minute"),
                            getIntent().getStringExtra("date"), i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        eventAdapter = new EventAdapter(this, eventList);
        listView.setAdapter(eventAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListActivity.this, EditActivity.class);
                intent.putExtra("position", new Long(id).toString());
                intent.putExtra("date", getIntent().getStringExtra("date"));
                startActivityForResult(intent, 1);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, AddActivity.class);
                intent.putExtra("date", getIntent().getStringExtra("date"));
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(true){
            Intent refresh = new Intent(this, ListActivity.class);
            refresh.putExtra("date", getIntent().getStringExtra("date"));
            startActivity(refresh);
            this.finish();
        }
    }
}