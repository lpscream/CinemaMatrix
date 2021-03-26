package net.matrixhome.kino.gui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class SpinnerAdapter extends ArrayAdapter {
    public static boolean flag = false;
    private final Context context;
    private final int resource;
    private final String[] textViewResourceId;


    //constructor
    public SpinnerAdapter(@NonNull Context context, int resource, String[] textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(context, resource, null);
        if (flag) {
            TextView tv = (TextView) convertView;
            tv.setText(textViewResourceId[position]);
        }
        return convertView;
    }
}
