package com.example.user.calendar3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {

    private Context context;
    private List<Event> eventList = new ArrayList<>();

    public EventAdapter(@NonNull Context context, ArrayList<Event> list) {
        super(context, 0 , list);
        this.context = context;
        eventList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);

        Event currentEvent = eventList.get(position);

        TextView name = (TextView) listItem.findViewById(R.id.item_title);
        TextView details = (TextView) listItem.findViewById(R.id.item_details);
        name.setText(currentEvent.getTitle());
        details.setText(currentEvent.getDate() + "  |  " + currentEvent.getHour() + ":" + currentEvent.getMinute());


        return listItem;
    }
}