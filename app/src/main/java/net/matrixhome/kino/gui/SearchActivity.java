package net.matrixhome.kino.gui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.matrixhome.kino.R;
import net.matrixhome.kino.data.Constants;
import net.matrixhome.kino.data.DataLoaderXML;
import net.matrixhome.kino.data.FilmList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

    private final String TAG = "SearchActivity_log";

    private final String COUNT = "100";
    private EditText searchTV;
    private DataLoaderXML dataLoaderXML;
    private ArrayList<FilmList> searchResult;
    private ListView filmListView;
    private SearchResultAdapter adapter;
    private TextView filmName;
    private ImageView cover;
    private String coverUrl;
    private TextView yearTV, genreTV, actorsTV, directorTV, countryTV;
    private Button playBtn;
    private int current;
    //private DataLoaderXML loader;
    private LinearLayout contentLayout, noResultLayout;
    private String[] list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchTV = findViewById(R.id.searchTVField);
        yearTV = findViewById(R.id.yearSearchActivity);
        genreTV = findViewById(R.id.genreSearchActivity);
        actorsTV = findViewById(R.id.actorsSearchActivity);
        directorTV = findViewById(R.id.directorSearchActivity);
        countryTV = findViewById(R.id.countrySearchActivity);
        playBtn = findViewById(R.id.playBtnSearchActivity);
        contentLayout = findViewById(R.id.contentLayout);
        noResultLayout = findViewById(R.id.noResultLayout);
        //loader = new DataLoaderXML(3);
        current = 0;
        Intent searchResponse = getIntent();
        searchTV.setText(searchResponse.getStringExtra(Constants.SEARCHRESPONSE));
        dataLoaderXML = new DataLoaderXML(3);
        searchResult = new ArrayList();
        filmName = findViewById(R.id.filmNameSearch);
        cover = findViewById(R.id.coverSearchDiscription);
        getWorldFilms(searchResponse.getStringExtra(Constants.SEARCHRESPONSE), searchResult);

        Log.d(TAG, "onCreate: ends");

        setListeners();
    }

    private void getWorldFilms(String search, ArrayList<FilmList> arrayList) {
        String host = "?action=video&sortby=added&limit=" + COUNT + "&q=";
        Log.d(TAG, "getWorldFilms: " + host.concat(search) + Constants.API_KEY);
        String str = dataLoaderXML.searchFilm(host.concat(search) + Constants.API_KEY);
        if (filmListView != null) {
            filmListView = null;
            adapter = null;
        }
        if (arrayList != null) {
            arrayList.clear();
            Log.d(TAG, "getWorldFilms: size is " + arrayList.size());
        }

        //TODO put this parser to a new thread
        //TODO need something to do with serials, some how play choosen episode
        Log.d(TAG, "run: started");
        JSONObject jsonRoot;
        try {
            jsonRoot = new JSONObject(str);
            JSONArray jsonArray = jsonRoot.getJSONArray("results");
            String[] array = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                array[i] = jsonArray.getString(i);
            }
            for (int i = 0; i < array.length; i++) {
                FilmList filmList = new FilmList();
                JSONObject jsonObject = new JSONObject(array[i]);
                filmList.id = jsonObject.getString("id");
                filmList.name = jsonObject.getString("name");
                filmList.description = jsonObject.getString("description");
                filmList.time = jsonObject.getString("time");
                filmList.cover = jsonObject.getString("cover");
                filmList.cover_200 = jsonObject.getString("cover_200");
                filmList.year = jsonObject.getString("year");
                filmList.country = jsonObject.getString("country");
                filmList.director = jsonObject.getString("director");
                filmList.actors = jsonObject.getString("actors");
                filmList.genres = jsonObject.getString("genres");
                filmList.original_name = jsonObject.getString("original_name");
                //TODO array with trailers for activity
                //create an array for film's trailers in filmList
                //filmList.trailer_urls = jsonObject.getString("trailer_urls");
                filmList.serial_id = jsonObject.getString("serial_id");
                filmList.serial_count_seasons = jsonObject.getString("serial_count_seasons");
                filmList.season_number = jsonObject.getString("season_number");
                filmList.serial_name = jsonObject.getString("serial_name");
                filmList.serial_o_name = jsonObject.getString("serial_o_name");
                filmList.translate = jsonObject.getString("translate");
                filmList.series = new ArrayList<>();
                for (int k = 0; k < jsonObject.getJSONArray("series").length(); k++) {
                    filmList.series.add(jsonObject.getJSONArray("series").get(k).toString());
                    //Log.d(TAG, "getWorldFilms: " + k);
                }
                Log.d(TAG, "getWorldFilms: size " + filmList.series.size());
                arrayList.add(filmList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getWorldFilms: " + arrayList.size());
        if (list != null) {
            list = null;
            list = new String[arrayList.size()];
        } else {
            list = new String[arrayList.size()];
        }

        for (int i = 0; i < arrayList.size(); i++) {
            list[i] = arrayList.get(i).name;
        }
        //listview create

        filmListView = findViewById(R.id.filmList);
        if (adapter != null)
            adapter.clear();
        adapter = new SearchResultAdapter(this, R.layout.search_list, list);
        filmListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        filmListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        filmListView.setSmoothScrollbarEnabled(true);
        filmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                coverUrl = searchResult.get(position).name;
                Picasso.get().load(searchResult.get(position).cover_200).transform(new RoundedCornersTransformation(30, 0)).resize(350, 500).centerCrop().into(cover);
                filmName.setText(searchResult.get(position).name);
                yearTV.setText(searchResult.get(position).year);
                genreTV.setText(searchResult.get(position).genres);
                actorsTV.setText(searchResult.get(position).actors);
                countryTV.setText(searchResult.get(position).country);
                directorTV.setText(searchResult.get(position).director);
                current = position;
                Log.d(TAG, "onItemClick: works");
            }
        });
        filmListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                coverUrl = searchResult.get(position).name;
                Picasso.get().load(searchResult.get(position).cover_200).transform(new RoundedCornersTransformation(30, 0)).resize(350, 500).centerCrop().into(cover);
                filmName.setText(searchResult.get(position).name);
                yearTV.setText(searchResult.get(position).year);
                genreTV.setText(searchResult.get(position).genres);
                actorsTV.setText(searchResult.get(position).actors);
                countryTV.setText(searchResult.get(position).country);
                directorTV.setText(searchResult.get(position).director);
                current = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        filmListView.setSelection(0);
        filmListView.requestFocus();

        if (arrayList.size() > 0) {
            contentLayout.setVisibility(View.VISIBLE);
            noResultLayout.setVisibility(View.GONE);
            Picasso.get().load(arrayList.get(0).cover).transform(new RoundedCornersTransformation(30, 0)).resize(700, 1000).into(cover);
            filmName.setText(arrayList.get(current).name);
            yearTV.setText(arrayList.get(current).year);
            genreTV.setText(arrayList.get(current).genres);
            actorsTV.setText(arrayList.get(current).actors);
            countryTV.setText(arrayList.get(current).country);
            directorTV.setText(arrayList.get(current).director);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchTV.getWindowToken(), 0);
        } else {
            filmName.setText(getResources().getString(R.string.noresult));
            contentLayout.setVisibility(View.GONE);
            noResultLayout.setVisibility(View.VISIBLE);
            searchTV.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchTV, InputMethodManager.SHOW_IMPLICIT);
        }
        Log.d(TAG, "getWorldFilms: method is end");
    }

    private void setListeners() {

        filmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                coverUrl = searchResult.get(position).name;
                Picasso.get().load(searchResult.get(position).cover_200).transform(new RoundedCornersTransformation(30, 0)).resize(350, 500).centerCrop().into(cover);
                filmName.setText(searchResult.get(position).name);
                yearTV.setText(searchResult.get(position).year);
                genreTV.setText(searchResult.get(position).genres);
                actorsTV.setText(searchResult.get(position).actors);
                countryTV.setText(searchResult.get(position).country);
                directorTV.setText(searchResult.get(position).director);
                current = position;
                Log.d(TAG, "onItemClick: works");
            }
        });

        filmListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                coverUrl = searchResult.get(position).name;
                Picasso.get().load(searchResult.get(position).cover_200).transform(new RoundedCornersTransformation(30, 0)).resize(350, 500).centerCrop().into(cover);
                filmName.setText(searchResult.get(position).name);
                yearTV.setText(searchResult.get(position).year);
                genreTV.setText(searchResult.get(position).genres);
                actorsTV.setText(searchResult.get(position).actors);
                countryTV.setText(searchResult.get(position).country);
                directorTV.setText(searchResult.get(position).director);
                current = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        searchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        searchTV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(searchTV, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        searchTV.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        if (searchTV.getText().toString().length() > 2) {
                            getWorldFilms(searchTV.getText().toString(), searchResult);
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(searchTV, InputMethodManager.SHOW_IMPLICIT);
                        break;
                }
                return false;
            }
        });


        //запускаем видео для просмотра
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchResult.get(current).serial_id.equalsIgnoreCase("null")) {
                    Intent intent = new Intent(getApplicationContext(), VideoPlayer.class);
                    intent.putExtra("link", dataLoaderXML.getSerialLinkByID(searchResult.get(current).id, "1"));
                    intent.putExtra("currentSeasonNumber", searchResult.get(current).id);
                    intent.putExtra("currentEpisodeNumber", "1");
                    int i = searchResult.get(current).series.size();
                    intent.putExtra("seriesCount", String.valueOf(i));
                    Log.d(TAG, "onClick: " + searchResult.get(current).series.size());
                    intent.putExtra("isSerial", true);
                    intent.putExtra("name", filmName.getText().toString());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), VideoPlayer.class);
                    intent.putExtra("link", dataLoaderXML.getLinkByID(searchResult.get(current).id));
                    intent.putExtra("name", filmName.getText().toString());
                    intent.putExtra("isSerial", false);

                    startActivity(intent);
                    Log.d(TAG, "onClick: trying start video activity");
                }
                //Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
                //intent.putExtra("link", dataLoaderXML.getLinkByID(searchResult.get(current).id));
                //startActivity(intent);
            }
        });


    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}