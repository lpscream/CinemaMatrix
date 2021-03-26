package net.matrixhome.kino.gui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.matrixhome.kino.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SearchResultAdapter extends ArrayAdapter<String> {

    private final Context cntx;
    private final String TAG = "SearchActivity_log";
    private String[] filmList;


    public SearchResultAdapter(@NonNull Context context, int resource, String[] arrayList) {
        super(context, resource);
        cntx = context;
        this.filmList = new String[arrayList.length];
        this.filmList = arrayList;
        Log.d(TAG, "SearchResultAdapter: created");
    }

    @Override
    public int getCount() {
        return filmList.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d(TAG, "getView: " + position);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.search_list, parent, false);
        TextView filmName = (TextView) row.findViewById(R.id.filmNameTV);
        filmName.setText(filmList[position]);
        Log.d(TAG, "getView: " + filmList[position]);
        Log.d(TAG, "getView: " + position);
        return row;
    }
}
