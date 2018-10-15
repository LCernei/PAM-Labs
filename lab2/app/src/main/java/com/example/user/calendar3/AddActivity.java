package com.example.user.calendar3;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;

public class AddActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private Button saveButton;
    private Button timeButton;
    private TextView timeText;
    private EditText titleField;
    private EditText descriptionField;
    private EditText locationField;

    private Context context;
    private int hour;
    private int minute;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        saveButton = findViewById(R.id.button2);
        timeButton = findViewById(R.id.button);
        titleField = findViewById(R.id.editText);
        descriptionField = findViewById(R.id.editText2);
        locationField = findViewById(R.id.editText3);
        timeText = findViewById(R.id.textView);

        context = getApplicationContext();
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        timeText.setText("" + hour + ":" + minute);

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = toJSon();
                File file = new File(context.getFilesDir(), "data.json");
                FileOutputStream outputStream;

                System.out.println(context.getFilesDir());
                try {
                    outputStream = openFileOutput("data.json", Context.MODE_PRIVATE);
                    outputStream.write(str.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                startAlarm();

                setResult(RESULT_OK, null);
                finish();
            }
        });
    }

    public String toJSon() {
        // parse existing/init new JSON
        File jsonFile = new File(context.getFilesDir(), "data.json");
        String previousJson = null;
        if (jsonFile.exists()) {
            previousJson = readFromFile(jsonFile);
        } else {
            previousJson = "{}";
        }

        // create new "complex" object
        JSONObject mO = null;
        try {
            mO = new JSONObject(previousJson);

            JSONArray arr;
            if (!mO.has(getIntent().getStringExtra("date"))) {
                arr = new JSONArray();
            }
            else{
                arr = mO.getJSONArray(getIntent().getStringExtra("date"));
            }
            JSONObject jo = new JSONObject();
            jo.put("title", titleField.getText().toString());
            jo.put("minute", minute);
            jo.put("hour", hour);
            jo.put("description", descriptionField.getText().toString());
            jo.put("location", locationField.getText().toString());

            arr.put(jo);

            mO.put(getIntent().getStringExtra("date"), arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // generate string from the object
        String jsonString = null;
        try {
            jsonString = mO.toString(4);
            return jsonString;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

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
        }catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {
        minute = minuteOfDay;
        hour = hourOfDay;
        timeText.setText("" + hourOfDay + ":" + minuteOfDay);
    }



    private void startAlarm() {

        String[] sDate = getIntent().getStringExtra("date").split("-");

        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.YEAR, new Integer(sDate[0]));
        calendar.set(Calendar.MONTH, new Integer(sDate[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, new Integer(sDate[2]));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        System.out.println("vasea " + calendar.getTime().toString());

        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmNotificationReceiver.class);
        intent.putExtra("title", titleField.getText().toString());
        alarmIntent = PendingIntent.getBroadcast(context,0, intent,0);

        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), alarmIntent);


    }
}
