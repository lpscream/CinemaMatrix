package net.matrixhome.kino.gui.adapters;

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
import net.matrixhome.kino.gui.RoundedCornersTransformation;
import net.matrixhome.kino.model.Movies;
import net.matrixhome.kino.viewmodel.FilmCatalougeModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FilmCatalogueAdapter extends RecyclerView.Adapter<FilmCatalogueAdapter.ViewHolder> {
    private final String TAG = "FilmCatalogueAdapter_lo";
    private final Context cntx;
    ArrayList<Movies> filmLists;
    LayoutInflater inflater;
    private FilmCatalogueAdapter.ItemClickListener mClickListener;
    private View.OnScrollChangeListener onMyScrollListener;
    private int pos;
    private FilmCatalougeModel filmViewModel;
    private String id;


    public FilmCatalogueAdapter(Context context, ArrayList<Movies> films, FilmCatalougeModel filmViewModel, String id) {
        this.filmLists = films;//список фильмов
        this.inflater = LayoutInflater.from(context);
        this.cntx = context;
        this.filmViewModel = filmViewModel;//вьюмодель
        this.id = id;//id ряда фильмов
    }

    @Override
    public FilmCatalogueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.listview, parent, false);
        return new FilmCatalogueAdapter.ViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull FilmCatalogueAdapter.ViewHolder holder, int position) {
        if (position == getItemCount() - 15) {
            filmViewModel.updateListByID(id);
            Log.d(TAG, "onBindViewHolder: " + "id " + id + " current update position " + position);
        }
        if (filmLists.get(position).getSerial_name() != null) {
            holder.filmName.setText(filmLists.get(position).getSerial_name());
        } else {
            holder.filmName.setText(filmLists.get(position).getName());
        }
        holder.ageTV.setText(filmLists.get(position).getAge());
        holder.ageTV.setBackgroundColor(R.color.colorGrey);
        holder.ratingTV.setText(filmLists.get(position).getRating());
        holder.ratingTV.setBackgroundColor(R.color.colorGrey);
        holder.yearCountry.setText(filmLists.get(position).getYear() + ", " + filmLists.get(position).getCountry());
        Picasso.get().load(filmLists.get(position).getCover_200()).transform(new RoundedCornersTransformation(30, 0)).resize(350, 500).centerCrop().into(holder.filmCover);
        holder.itemView.setFocusable(true);
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.setElevation(20);
                    }
                } else {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.setElevation(0);
                    }
                }
            }
        });
        pos = position;
    }


    @Override
    public int getItemCount() {
        if (filmLists != null)
            return filmLists.size();
        else return 0;
    }


    public int getPosition() {
        return pos;
    }

    //click listener
    public void setClickListener(FilmCatalogueAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    void setOnScrollChangeListener(View.OnScrollChangeListener onScrollChangeListener) {
        this.onMyScrollListener = onScrollChangeListener;
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
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