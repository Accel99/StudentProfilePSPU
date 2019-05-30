package com.example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.profile.R;

import java.util.List;

public class MyAdapter extends ArrayAdapter {

    private List<Ordering> list;
    private Context context;

    public MyAdapter(Context context, List<Ordering> objects) {
        super(context, R.layout.list_item, objects);

        this.context = context;
        this.list = objects;
    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);

        TextView cell1 = rowView.findViewById(R.id.listTvCol1);
        TextView cell2 = rowView.findViewById(R.id.listTvCol2);
        TextView cell3 = rowView.findViewById(R.id.listTvCol3);
        TextView cell4 = rowView.findViewById(R.id.listTvCol4);

        Ordering ordering = list.get(position);

        cell1.setText(ordering.type);
        cell2.setText("ID: " + ordering.id);
        cell3.setText("Статуст: " + ordering.status);
        cell4.setText(ordering.date);

        return rowView;
    }
}
