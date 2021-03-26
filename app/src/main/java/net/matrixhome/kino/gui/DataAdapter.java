package net.matrixhome.kino.gui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.matrixhome.kino.R;
import net.matrixhome.kino.data.FilmList;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private final String TAG = "DataAdapter_log";
    private final ArrayList<Integer> hidenList = new ArrayList<>();
    private final Context cntx;
    public int offset = 0;
    ArrayList<FilmList> filmLists;
    LayoutInflater inflater;
    private ItemClickListener mClickListener;
    private View.OnScrollChangeListener onMyScrollListener;
    private int pos;


    DataAdapter(Context context, ArrayList<FilmList> films) {
        this.filmLists = films;
        this.inflater = LayoutInflater.from(context);
        this.cntx = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.listview, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (filmLists.get(position).serial_name != "null") {
            holder.filmName.setText(filmLists.get(position).serial_name);
        } else {
            holder.filmName.setText(filmLists.get(position).name);
        }
        holder.ageTV.setText(filmLists.get(position).age);
        holder.ageTV.setBackgroundColor(R.color.colorGrey);
        holder.ratingTV.setText(filmLists.get(position).rating);
        holder.ratingTV.setBackgroundColor(R.color.colorGrey);
        holder.yearCountry.setText(filmLists.get(position).year + ", " + filmLists.get(position).country);
        Picasso.get().load(filmLists.get(position).cover_200).transform(new RoundedCornersTransformation(30, 0)).resize(350, 500).centerCrop().into(holder.filmCover);
        holder.itemView.setFocusable(true);
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //Log.d(TAG, "onFocusChange: " + holder.getAdapterPosition());
                    v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.setElevation(20);
                    }

                    Log.d(TAG, "onFocusChange: " + position);
                    //Log.d(TAG, "onFocusChange: bigger");
                } else {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.setElevation(0);
                    }
                    //Log.d(TAG, "onFocusChange: smaller");
                }
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.itemView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                }
            });
        }
        pos = position;
    }


    public void setItems(int num) {
        for (int i = num; i < getItemCount(); i++) {
            Log.d(TAG, "setItems: " + i);
            notifyItemInserted(i);
        }

        //notifyDataSetChanged();
    }

    public void deleteItem() {
        for (int i = 0; i < hidenList.size(); i++) {
            notifyItemRemoved(hidenList.get(i));
            notifyItemRangeChanged(hidenList.get(i), hidenList.get(hidenList.size() - 1));
        }
    }

    @Override
    public int getItemCount() {
        return filmLists.size();
    }


    public int getPosition() {
        return pos;
    }

    //click listener
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    //onMyScrollListener listener
    void setOnScrollChangeListener(View.OnScrollChangeListener onScrollChangeListener) {
        this.onMyScrollListener = onScrollChangeListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    public interface OnScrollChangeListener {
        void onScrollChange(View view, int i, int i1, int i2, int i3);
    }

    @SuppressLint("NewApi")
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnScrollChangeListener {
        private final TextView filmName;
        private final ImageView filmCover;
        private final TextView ageTV;
        private final TextView ratingTV;
        private final TextView yearCountry;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            filmName = (TextView) itemView.findViewById(R.id.filmName);
            filmCover = (ImageView) itemView.findViewById(R.id.cover);
            ageTV = itemView.findViewById(R.id.ageTV);
            ratingTV = itemView.findViewById(R.id.ratingTV);
            yearCountry = itemView.findViewById(R.id.yearCountryTV);
            itemView.setOnClickListener(this);
            itemView.setOnScrollChangeListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
        }


        @Override
        public void onScrollChange(View view, int i, int i1, int i2, int i3) {
            if (onMyScrollListener != null)
                onMyScrollListener.onScrollChange(view, i, i1, i2, i3);
        }
    }
}