package net.matrixhome.kino.gui;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import net.matrixhome.kino.BuildConfig;
import net.matrixhome.kino.R;
import net.matrixhome.kino.data.CheckService;
import net.matrixhome.kino.data.Constants;
import net.matrixhome.kino.data.DataLoaderXML;
import net.matrixhome.kino.data.FilmList;
import net.matrixhome.kino.data.Genre;
import net.matrixhome.kino.data.SettingsManager;
import net.matrixhome.kino.data.Updater;
import net.matrixhome.kino.data.Years;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MainActivity extends AppCompatActivity implements DataDurrationFragmnet.OnSelectedDurationListener {

    public static String SEARCHRESPONSE = "searchResponse";
    private final String TAG = "MainActivity_log";
    private final ArrayList<FilmList> backup = new ArrayList<>();
    private long downloadID;

    private SettingsManager settingsManager;
    private CheckService checkService;
    private String category = "";
    private ArrayList<Genre> genreArrayList;
    private Years years;
    private Spinner genreSpinner;
    private Spinner counrtySpinner;
    private Spinner yearFromSpinner;
    private Spinner yearToSpinner;
    private ImageButton yearDurationBtn;
    private boolean genreSpinnerFlag = false;
    private boolean countrySpinnerFlag = false;
    private boolean yearFromSpinnerFlag = false;
    private boolean yearToSpinnerFlag = false;
    private String genreID;
    private String countryID;
    private String yearFrom;
    private String yearTo;
    private String yearFromTo;
    private int position;
    private Button filmsBtn;
    private Button animBtn;
    private Button serialBtn;
    private Button animSerialBtn;
    private Button tvShowBtn;
    private DataLoaderXML getAllDataList;

    private ArrayList<FilmList> lastAddedFilmList;
    private ArrayList<FilmList> byPopularityFilmFilmLists;
    private ArrayList<FilmList> byViewsFilmLists;
    private ArrayList<FilmList> byRatingFilmLists;
    private ArrayList<FilmList> byNameFilmLists;
    private ArrayList<FilmList> byDatePremiereFilmLists;
    private ArrayList<FilmList> estimatedFilmLists;

    private ArrayList<FilmList> bundleLastAddedFilmList;
    private ArrayList<FilmList> bundleByPopularityFilmFilmLists;
    private ArrayList<FilmList> bundleByViewsFilmLists;
    private ArrayList<FilmList> bundleByRatingFilmLists;
    private ArrayList<FilmList> bundleByNameFilmLists;
    private ArrayList<FilmList> bundleByDatePremiereFilmLists;
    private ArrayList<FilmList> bundleEstimatedFilmLists;

    private ArrayList<FilmList> updateLastAddedFilmList;
    private ArrayList<FilmList> updateByPopularityFilmFilmLists;
    private ArrayList<FilmList> updateByViewsFilmFilmLists;
    private ArrayList<FilmList> updateByRatingFilmLists;
    private ArrayList<FilmList> updateByNameFilmLists;
    private ArrayList<FilmList> updateByDatePremiereFilmLists;
    private ArrayList<FilmList> updateEstimatedFilmLists;

    private RecyclerView lastAddedRecView;
    private RecyclerView byPopularityFilmRecView;
    private RecyclerView byViewsFilmRecView;
    private RecyclerView byRatingRecView;
    private RecyclerView byNameRecView;
    private RecyclerView byDatePremiereRecView;
    private RecyclerView estimatedRecView;

    private LinearLayoutManager lastAddedRecLayManager;
    private LinearLayoutManager byPopularityFilmRecLayManager;
    private LinearLayoutManager byViewsFilmRecLayManager;
    private LinearLayoutManager byRatingRecLayManager;
    private LinearLayoutManager byNameRecLayManager;
    private LinearLayoutManager byDatePremiereRecLayManager;
    private LinearLayoutManager estimatedRecLayManager;

    private DataAdapter lastAddedFilmsDataAdapter;
    private DataAdapter byPopularityFilmDataAdapter;
    private DataAdapter byViewsFilmDataAdapter;
    private DataAdapter byRatingDataAdapter;
    private DataAdapter byNameDataAdapter;
    private DataAdapter byDatePremiereDataAdapter;
    private DataAdapter estimatedDataAdapter;

    private ConstraintLayout superLayout;
    private EditText searchTV;
    private DataDurrationFragmnet datePickerFragment;
    private boolean restoreState = false;

    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        return wifiInfo != null && wifiInfo.isConnected();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        if (!restoreState) {
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("bundleLastAddedFilmList", bundleLastAddedFilmList);
        outState.putSerializable("bundleByDatePremiereFilmLists", bundleByDatePremiereFilmLists);
        outState.putSerializable("bundleByNameFilmLists", bundleByNameFilmLists);
        outState.putSerializable("bundleByPopularityFilmFilmLists", bundleByPopularityFilmFilmLists);
        outState.putSerializable("bundleByRatingFilmLists", bundleByRatingFilmLists);
        outState.putSerializable("bundleByViewsFilmLists", bundleByViewsFilmLists);
        outState.putSerializable("bundleEstimatedFilmLists", bundleEstimatedFilmLists);
        outState.putSerializable("genreArrayList", genreArrayList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: started activity ");
        restoreState = savedInstanceState != null;
        Log.d(TAG, "onCreate: savedInstanceState is " + restoreState);
        //проверка соединения с интернет
        if (!hasConnection(this)) {
            Intent intent = new Intent(this, ConnectionActivity.class);
            startActivity(intent);
        }
        //вкл./выкл обнровления
        new Updater().execute(this);
        checkService = new CheckService();
        if (!checkService.check()) {
            finish();
        }

        initVariables();


        /*if (restoreState) {
            Log.d(TAG, "onCreate: restore data");
            genreArrayList = new ArrayList<Genre>();
            genreArrayList.addAll((ArrayList<Genre>) savedInstanceState.getSerializable("genreArrayList"));

            bundleLastAddedFilmList = (ArrayList<FilmList>) savedInstanceState
                    .getSerializable("bundleLastAddedFilmList");
            Log.d(TAG, "onCreate: backup size is " + bundleLastAddedFilmList.size());
            restoreView(bundleLastAddedFilmList,
                    lastAddedFilmList, lastAddedRecView, lastAddedRecLayManager, lastAddedFilmsDataAdapter);


            bundleByPopularityFilmFilmLists.addAll((ArrayList<FilmList>) savedInstanceState
                    .getSerializable("bundleByPopularityFilmFilmLists"));
            Log.d(TAG, "onCreate: backup size is " + bundleByPopularityFilmFilmLists.size());
            restoreView(bundleByPopularityFilmFilmLists
                    , byPopularityFilmFilmLists, byPopularityFilmRecView, byPopularityFilmRecLayManager
                    , byPopularityFilmDataAdapter);


            bundleByViewsFilmLists.addAll((ArrayList<FilmList>) savedInstanceState
                    .getSerializable("bundleByViewsFilmLists"));
            Log.d(TAG, "onCreate: backup size is " + bundleByViewsFilmLists.size());
            restoreView(bundleByViewsFilmLists,
                    byViewsFilmLists, byViewsFilmRecView, byViewsFilmRecLayManager, byViewsFilmDataAdapter);


            bundleByRatingFilmLists.addAll((ArrayList<FilmList>) savedInstanceState
                    .getSerializable("bundleByRatingFilmLists"));
            Log.d(TAG, "onCreate: backup size is " + backup.size());
            restoreView(bundleByRatingFilmLists,
                    byRatingFilmLists, byRatingRecView, byRatingRecLayManager, byRatingDataAdapter);


            bundleByNameFilmLists.addAll((ArrayList<FilmList>) savedInstanceState
                    .getSerializable("bundleByNameFilmLists"));
            Log.d(TAG, "onCreate: backup size is " + backup.size());
            restoreView(bundleByNameFilmLists
                    , byNameFilmLists, byNameRecView, byNameRecLayManager, byNameDataAdapter);


            bundleByDatePremiereFilmLists.addAll((ArrayList<FilmList>) savedInstanceState
                    .getSerializable("bundleByDatePremiereFilmLists"));
            Log.d(TAG, "onCreate: backup size is " + backup.size());
            restoreView(bundleByDatePremiereFilmLists
                    , byDatePremiereFilmLists, byDatePremiereRecView, byDatePremiereRecLayManager, byDatePremiereDataAdapter);


            bundleEstimatedFilmLists.addAll((ArrayList<FilmList>) savedInstanceState
                    .getSerializable("bundleEstimatedFilmLists"));
            Log.d(TAG, "onCreate: backup size is " + backup.size());
            restoreView(bundleEstimatedFilmLists
                    , estimatedFilmLists, estimatedRecView, estimatedRecLayManager, estimatedDataAdapter);
        } else {*/

        //initMultiThread();

        initWithoutMultuThread();






        //getGenres("?action=genre");
        //}

        getGenres("?action=genre");
        initGenreSpinner();
        initCountrySpinner();
        //initSpinnerYears();
    }

    private void initWithoutMultuThread() {
        //lastAddedFilmList
        getWorldFilms("?action=video&sort_desc=added" + "&limit=" + Constants.COUNT, getAllDataList
                , lastAddedFilmList
                , lastAddedRecView, lastAddedRecLayManager
                , lastAddedFilmsDataAdapter, bundleLastAddedFilmList);
        lastAddedFilmsDataAdapter.notifyDataSetChanged();
        Log.d(TAG, "onCreate: ///////////////////lastAddedFilmList");
        //byPopularityFilmFilmLists
        getWorldFilms("?action=video&sort_desc=views_month" + "&limit=" + Constants.COUNT
                , getAllDataList
                , byPopularityFilmFilmLists, byPopularityFilmRecView, byPopularityFilmRecLayManager,
                byPopularityFilmDataAdapter, bundleByPopularityFilmFilmLists);
        byPopularityFilmDataAdapter.notifyDataSetChanged();
        Log.d(TAG, "onCreate: ///////////////////byPopularityFilmFilmLists");
        //byViewsFilmLists
        getWorldFilms("?action=video&sort_desc=views" + "&limit=" + Constants.COUNT
                , getAllDataList
                , byViewsFilmLists, byViewsFilmRecView, byViewsFilmRecLayManager
                , byViewsFilmDataAdapter, bundleByViewsFilmLists);
        byViewsFilmDataAdapter.notifyDataSetChanged();
        Log.d(TAG, "onCreate: ///////////////////byViewsFilmLists");
        //byRatingFilmLists
        getWorldFilms("?action=video&sort_desc=rating" + "&limit=" + Constants.COUNT
                , getAllDataList
                , byRatingFilmLists, byRatingRecView, byRatingRecLayManager
                , byRatingDataAdapter, bundleByRatingFilmLists);
        byRatingDataAdapter.notifyDataSetChanged();
        Log.d(TAG, "onCreate: ///////////////////byRatingFilmLists");
        //byNameFilmLists
        getWorldFilms("?action=video&sort_desc=name" + "&limit=" + Constants.COUNT
                , getAllDataList
                , byNameFilmLists, byNameRecView, byNameRecLayManager
                , byNameDataAdapter, bundleByNameFilmLists);
        byNameDataAdapter.notifyDataSetChanged();
        Log.d(TAG, "onCreate: ///////////////////byNameFilmLists");
        //byDatePremiereFilmLists
        getWorldFilms("?action=video&sort_desc=date_premiere" + "&limit=" + Constants.COUNT
                , getAllDataList
                , byDatePremiereFilmLists, byDatePremiereRecView, byDatePremiereRecLayManager
                , byDatePremiereDataAdapter, bundleByDatePremiereFilmLists);
        byDatePremiereDataAdapter.notifyDataSetChanged();
        Log.d(TAG, "onCreate: ///////////////////byDatePremiereFilmLists");

        getWorldFilms("?action=video&sort_desc=rating_vote" + "&limit=" + Constants.COUNT
                , getAllDataList
                , estimatedFilmLists, estimatedRecView, estimatedRecLayManager
                , estimatedDataAdapter, bundleEstimatedFilmLists);
        estimatedDataAdapter.notifyDataSetChanged();
        Log.d(TAG, "onCreate: ///////////////////estimatedFilmLists");
    }

    private void initMultiThread() {
        ArrayList<ArrayList<FilmList>> collection = new ArrayList<>();
        ArrayList<ArrayList<FilmList>> collection2 = new ArrayList<>();
        ArrayList<String> urlList = new ArrayList<>();

        collection2.add(lastAddedFilmList);
        collection2.add(byPopularityFilmFilmLists);
        collection2.add(byViewsFilmLists);
        collection2.add(byRatingFilmLists);
        collection2.add(byNameFilmLists);
        collection2.add(byDatePremiereFilmLists);
        collection2.add(estimatedFilmLists);

        urlList.add("?action=video&sort_desc=added" + "&limit=" + Constants.COUNT);
        urlList.add("?action=video&sort_desc=views_month" + "&limit=" + Constants.COUNT);
        urlList.add("?action=video&sort_desc=views" + "&limit=" + Constants.COUNT);
        urlList.add("?action=video&sort_desc=rating" + "&limit=" + Constants.COUNT);
        urlList.add("?action=video&sort_desc=name" + "&limit=" + Constants.COUNT);
        urlList.add("?action=video&sort_desc=date_premiere" + "&limit=" + Constants.COUNT);
        urlList.add("?action=video&sort_desc=rating_vote" + "&limit=" + Constants.COUNT);

        for (int i = 0; i < urlList.size(); i++) {
             collection.add(getAllDataList.getAllDataCallable(urlList.get(i)));
            Log.d(TAG, "initMultiThread: thread created");
        }




        /*lastAddedFilmList = getAllDataList.getAllDataCallable("?action=video&sort_desc=added" + "&limit=" + Constants.COUNT);
        Log.d(TAG, "onCreate: " + lastAddedFilmList.size());
        byPopularityFilmFilmLists = getAllDataList.getAllDataCallable("?action=video&sort_desc=views_month" + "&limit=" + Constants.COUNT);
        Log.d(TAG, "onCreate: " + byPopularityFilmFilmLists.size());
        byViewsFilmLists = getAllDataList.getAllDataCallable("?action=video&sort_desc=views" + "&limit=" + Constants.COUNT);
        Log.d(TAG, "onCreate: " + byViewsFilmLists.size());
        byRatingFilmLists = getAllDataList.getAllDataCallable("?action=video&sort_desc=rating" + "&limit=" + Constants.COUNT);
        Log.d(TAG, "onCreate: " + byRatingFilmLists.size());
        byNameFilmLists = getAllDataList.getAllDataCallable("?action=video&sort_desc=name" + "&limit=" + Constants.COUNT);
        Log.d(TAG, "onCreate: " + byNameFilmLists.size());
        byDatePremiereFilmLists = getAllDataList.getAllDataCallable("?action=video&sort_desc=date_premiere" + "&limit=" + Constants.COUNT);
        Log.d(TAG, "onCreate: " + byDatePremiereFilmLists.size());
        estimatedFilmLists = getAllDataList.getAllDataCallable("?action=video&sort_desc=rating_vote" + "&limit=" + Constants.COUNT);
        Log.d(TAG, "onCreate: "+ estimatedFilmLists.size());

         */

        fillRecyclerView(lastAddedFilmList
                , lastAddedRecView, lastAddedRecLayManager
                , lastAddedFilmsDataAdapter, bundleLastAddedFilmList);
        lastAddedFilmsDataAdapter.notifyDataSetChanged();
        fillRecyclerView(byPopularityFilmFilmLists, byPopularityFilmRecView, byPopularityFilmRecLayManager,
                byPopularityFilmDataAdapter, bundleByPopularityFilmFilmLists);
        byPopularityFilmDataAdapter.notifyDataSetChanged();
        fillRecyclerView(byViewsFilmLists, byViewsFilmRecView, byViewsFilmRecLayManager
                , byViewsFilmDataAdapter, bundleByViewsFilmLists);
        byViewsFilmDataAdapter.notifyDataSetChanged();
        fillRecyclerView(byRatingFilmLists, byRatingRecView, byRatingRecLayManager
                , byRatingDataAdapter, bundleByRatingFilmLists);
        byRatingDataAdapter.notifyDataSetChanged();
        fillRecyclerView(byNameFilmLists, byNameRecView, byNameRecLayManager
                , byNameDataAdapter, bundleByNameFilmLists);
        byNameDataAdapter.notifyDataSetChanged();
        fillRecyclerView(byDatePremiereFilmLists, byDatePremiereRecView, byDatePremiereRecLayManager
                , byDatePremiereDataAdapter, bundleByDatePremiereFilmLists);
        byDatePremiereDataAdapter.notifyDataSetChanged();
        fillRecyclerView(estimatedFilmLists, estimatedRecView, estimatedRecLayManager
                , estimatedDataAdapter, bundleEstimatedFilmLists);
        estimatedDataAdapter.notifyDataSetChanged();
    }

    private void newCategorySort() {
        getWorldFilms("?action=video&sort_desc=added" + category
                        + genreID + countryID + yearFromTo
                        + "&limit=" + Constants.COUNT, getAllDataList
                , lastAddedFilmList
                , lastAddedRecView, lastAddedRecLayManager
                , lastAddedFilmsDataAdapter, bundleLastAddedFilmList);
        lastAddedFilmsDataAdapter.offset = 0;

        getWorldFilms("?action=video&sort_desc=views_month" + category
                        + genreID + countryID + yearFromTo
                        + "&limit=" + Constants.COUNT
                , getAllDataList
                , byPopularityFilmFilmLists, byPopularityFilmRecView, byPopularityFilmRecLayManager,
                byPopularityFilmDataAdapter, bundleByPopularityFilmFilmLists);
        byPopularityFilmDataAdapter.offset = 0;

        getWorldFilms("?action=video&sort_desc=views" + category
                        + genreID + countryID + yearFromTo
                        + "&limit=" + Constants.COUNT
                , getAllDataList
                , byViewsFilmLists, byViewsFilmRecView, byViewsFilmRecLayManager
                , byViewsFilmDataAdapter, bundleByViewsFilmLists);
        byViewsFilmDataAdapter.offset = 0;

        getWorldFilms("?action=video&sort_desc=rating" + category
                        + genreID + countryID + yearFromTo
                        + "&limit=" + Constants.COUNT
                , getAllDataList
                , byRatingFilmLists, byRatingRecView, byRatingRecLayManager
                , byRatingDataAdapter, bundleByRatingFilmLists);
        byRatingDataAdapter.offset = 0;

        getWorldFilms("?action=video&sort_desc=name" + category
                        + genreID + countryID + yearFromTo
                        + "&limit=" + Constants.COUNT
                , getAllDataList
                , byNameFilmLists, byNameRecView, byNameRecLayManager
                , byNameDataAdapter, bundleByNameFilmLists);
        byNameDataAdapter.offset = 0;
        getWorldFilms("?action=video&sort_desc=date_premiere" + category
                        + genreID + countryID + yearFromTo
                        + "&limit=" + Constants.COUNT
                , getAllDataList
                , byDatePremiereFilmLists, byDatePremiereRecView, byDatePremiereRecLayManager
                , byDatePremiereDataAdapter, bundleByDatePremiereFilmLists);
        byPopularityFilmDataAdapter.offset = 0;

        getWorldFilms("?action=video&sort_desc=rating_vote" + category
                        + genreID + countryID + yearFromTo
                        + "&limit=" + Constants.COUNT
                , getAllDataList
                , estimatedFilmLists, estimatedRecView, estimatedRecLayManager
                , estimatedDataAdapter, bundleEstimatedFilmLists);
        estimatedDataAdapter.offset = 0;
    }

    @SuppressLint("WrongViewCast")
    private void initVariables() {
        getAllDataList = new DataLoaderXML(10);
        settingsManager = new SettingsManager();
        genreID = "";
        countryID = "";
        yearFrom = "";
        yearTo = "";
        yearFromTo = "";
        superLayout = findViewById(R.id.superLayout);
        //TODO need to sign with button
        yearDurationBtn = findViewById(R.id.yearDurationBtn);
        yearDurationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerFragment = new DataDurrationFragmnet();
                datePickerFragment.show(getSupportFragmentManager(), "datePickerFragment");
            }
        });


        filmsBtn = findViewById(R.id.filmsBtn);
        animBtn = findViewById(R.id.animBtn);
        serialBtn = findViewById(R.id.serialBtn);
        animSerialBtn = findViewById(R.id.animSerialBtn);
        tvShowBtn = findViewById(R.id.tvShowBtn);
        years = new Years();

        lastAddedRecLayManager = new LinearLayoutManager(this
                , LinearLayoutManager.HORIZONTAL
                , false);
        byPopularityFilmRecLayManager = new LinearLayoutManager(this
                , LinearLayoutManager.HORIZONTAL
                , false);
        byViewsFilmRecLayManager = new LinearLayoutManager(this
                , LinearLayoutManager.HORIZONTAL
                , false);
        byRatingRecLayManager = new LinearLayoutManager(this
                , LinearLayoutManager.HORIZONTAL
                , false);
        byNameRecLayManager = new LinearLayoutManager(this
                , LinearLayoutManager.HORIZONTAL
                , false);
        byDatePremiereRecLayManager = new LinearLayoutManager(this
                , LinearLayoutManager.HORIZONTAL
                , false);
        estimatedRecLayManager = new LinearLayoutManager(this
                , LinearLayoutManager.HORIZONTAL
                , false);



        lastAddedFilmList = new ArrayList<>();
        byPopularityFilmFilmLists = new ArrayList<>();
        byViewsFilmLists = new ArrayList<>();
        byRatingFilmLists = new ArrayList<>();
        byNameFilmLists = new ArrayList<>();
        byDatePremiereFilmLists = new ArrayList<>();
        estimatedFilmLists = new ArrayList<>();

        bundleLastAddedFilmList = new ArrayList<>();
        bundleByPopularityFilmFilmLists = new ArrayList<>();
        bundleByViewsFilmLists = new ArrayList<>();
        bundleByRatingFilmLists = new ArrayList<>();
        bundleByNameFilmLists = new ArrayList<>();
        bundleByDatePremiereFilmLists = new ArrayList<>();
        bundleEstimatedFilmLists = new ArrayList<>();

        updateLastAddedFilmList = new ArrayList<>();
        updateByPopularityFilmFilmLists = new ArrayList<>();
        updateByViewsFilmFilmLists = new ArrayList<>();
        updateByRatingFilmLists = new ArrayList<>();
        updateByNameFilmLists = new ArrayList<>();
        updateByDatePremiereFilmLists = new ArrayList<>();
        updateEstimatedFilmLists = new ArrayList<>();

        lastAddedRecView = findViewById(R.id.lastAddedRecView);
        byPopularityFilmRecView = findViewById(R.id.worldfilmRecView);
        byViewsFilmRecView = findViewById(R.id.ourfilmRecView);
        byRatingRecView = findViewById(R.id.animationRecView);
        byNameRecView = findViewById(R.id.animationSerialRecView);
        byDatePremiereRecView = findViewById(R.id.worldSerialRecView);
        estimatedRecView = findViewById(R.id.owrSerialRecView);

        ////////////////////////////////////
        lastAddedFilmsDataAdapter = new DataAdapter(this.getBaseContext().getApplicationContext()
                , lastAddedFilmList);

        lastAddedFilmsDataAdapter.setClickListener(new DataAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startDescriptionActivity(lastAddedFilmList, position);
            }
        });
        lastAddedRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                updateList(lastAddedFilmsDataAdapter, updateLastAddedFilmList
                        , lastAddedFilmList, getAllDataList
                        , "?action=video&sort_desc=added" + category
                                + genreID + countryID + yearFromTo
                                + "&limit=", bundleLastAddedFilmList);
            }
        });
        /////////////////////////////////////
        byPopularityFilmDataAdapter = new DataAdapter(this.getBaseContext().getApplicationContext(), byPopularityFilmFilmLists);
        byPopularityFilmDataAdapter.setClickListener(new DataAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startDescriptionActivity(byPopularityFilmFilmLists, position);
            }
        });
        byPopularityFilmRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                updateList(byPopularityFilmDataAdapter, updateByPopularityFilmFilmLists
                        , byPopularityFilmFilmLists, getAllDataList
                        , "?action=video&sort_desc=views_month" + category
                                + genreID + countryID + yearFromTo
                                + "&limit=", bundleByPopularityFilmFilmLists);
            }
        });
        /////////////////////////////////////
        byViewsFilmDataAdapter = new DataAdapter(this.getBaseContext().getApplicationContext(), byViewsFilmLists);
        byViewsFilmDataAdapter.setClickListener(new DataAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startDescriptionActivity(byViewsFilmLists, position);
            }
        });
        byViewsFilmRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                updateList(byViewsFilmDataAdapter, updateByViewsFilmFilmLists
                        , byViewsFilmLists, getAllDataList
                        , "?action=video&sort_desc=views" + category
                                + genreID + countryID + yearFromTo
                                + "&limit=", bundleByViewsFilmLists);
            }
        });
        /////////////////////////////////////
        byRatingDataAdapter = new DataAdapter(this.getBaseContext().getApplicationContext(), byRatingFilmLists);
        byRatingDataAdapter.setClickListener(new DataAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startDescriptionActivity(byRatingFilmLists, position);
            }
        });
        byRatingRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                updateList(byRatingDataAdapter, updateByRatingFilmLists
                        , byRatingFilmLists, getAllDataList
                        , "?action=video&sort_desc=rating" + category
                                + genreID + countryID + yearFromTo
                                + "&limit=", bundleByRatingFilmLists);
            }
        });

        byNameDataAdapter = new DataAdapter(this.getBaseContext().getApplicationContext(), byNameFilmLists);
        byNameDataAdapter.setClickListener(new DataAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startDescriptionActivity(byNameFilmLists, position);
                Log.d(TAG, "onItemClick: animation serials listener  " + position);
            }
        });
        byNameRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                updateList(byNameDataAdapter, updateByNameFilmLists
                        , byNameFilmLists, getAllDataList
                        , "?action=video&sort_desc=name" + category
                                + genreID + countryID + yearFromTo
                                + "&limit=", bundleByNameFilmLists);
            }
        });

        byDatePremiereDataAdapter = new DataAdapter(this.getBaseContext().getApplicationContext(), byDatePremiereFilmLists);
        byDatePremiereDataAdapter.setClickListener(new DataAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startDescriptionActivity(byDatePremiereFilmLists, position);
                Log.d(TAG, "onItemClick: world serials listener  " + position);
            }
        });
        byDatePremiereRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                updateList(byDatePremiereDataAdapter, updateByDatePremiereFilmLists, byDatePremiereFilmLists
                        , getAllDataList
                        , "?action=video&sort_desc=date_premiere" + category
                                + genreID + countryID + yearFromTo
                                + "&limit=", bundleByDatePremiereFilmLists);
            }
        });
        estimatedDataAdapter = new DataAdapter(this.getBaseContext().getApplicationContext(), estimatedFilmLists);
        estimatedDataAdapter.setClickListener(new DataAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startDescriptionActivity(estimatedFilmLists, position);
                Log.d(TAG, "onItemClick: our serials " + position);
            }
        });

        estimatedRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                updateList(estimatedDataAdapter, updateEstimatedFilmLists
                        , estimatedFilmLists, getAllDataList
                        , "?action=video&sort_desc=rating_vote" + category
                                + genreID + countryID + yearFromTo
                                + "&limit=", bundleEstimatedFilmLists);
            }
        });


        searchTV = findViewById(R.id.searchTV);

        searchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchTV.getText().toString().contains("Поиск"))
                    searchTV.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchTV, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        searchTV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (searchTV.getText().toString().contains("Поиск"))
                        searchTV.setText("");
                    Log.d(TAG, "onFocusChange: focus works");
                }
            }
        });

        searchTV.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(TAG, "onKey: " + keyCode);
                Log.d(TAG, "onKey: " + event);
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        if (searchTV.getText().toString().length() > 2) {
                            //берем строку введеную и отправляем в активити с поиском
                            startSearchActivity(searchTV.getText().toString());
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(searchTV, InputMethodManager.SHOW_IMPLICIT);
                        break;
                    case KeyEvent.KEYCODE_BACK:
                        //onBackPressed
                        break;
                }
                return false;
            }
        });

        filmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genreID = "";
                countryID = "";
                yearFrom = "";
                yearTo = "";
                yearFromTo = "";
                category = "&category[]=our-film&category[]=world-film";
                newCategorySort();
            }
        });

        animBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genreID = "";
                countryID = "";
                yearFrom = "";
                yearTo = "";
                yearFromTo = "";
                category = "&category=animation";
                newCategorySort();
            }
        });

        serialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genreID = "";
                countryID = "";
                yearFrom = "";
                yearTo = "";
                yearFromTo = "";
                category = "&category[]=world-serial&category[]=owr-serial";
                newCategorySort();
            }
        });

        animSerialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genreID = "";
                countryID = "";
                yearFrom = "";
                yearTo = "";
                yearFromTo = "";
                category = "&category=animation-serial";
                newCategorySort();
            }
        });

        tvShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genreID = "";
                countryID = "";
                yearFrom = "";
                yearTo = "";
                yearFromTo = "";
                category = "&category=tv-shows";
                newCategorySort();
            }
        });

        lastAddedRecView.requestFocus();
    }

    private void startSearchActivity(String response) {
        //start activity for search result
        Intent searchActivity = new Intent(this, SearchActivity.class);
        searchActivity.putExtra(SEARCHRESPONSE, response);
        startActivity(searchActivity);
    }

    private void startDescriptionActivity(ArrayList<FilmList> filmArray, int position) {
        Log.d(TAG, "onItemClick: world films listener  " + position);
        Intent intent = new Intent(getApplicationContext(), DescriptionActivity.class);
        intent.putExtra("description", filmArray.get(position).description);
        intent.putExtra("name", filmArray.get(position).name);
        intent.putExtra("cover", filmArray.get(position).cover);
        intent.putExtra("cover_200", filmArray.get(position).cover_200);
        intent.putExtra("year", filmArray.get(position).year);
        intent.putExtra("id", filmArray.get(position).id);
        intent.putExtra("country", filmArray.get(position).country);
        intent.putExtra("director", filmArray.get(position).director);
        intent.putExtra("actors", filmArray.get(position).actors);
        intent.putExtra("genres", filmArray.get(position).genres);
        intent.putExtra("original_name", filmArray.get(position).original_name);
        //TODO array with trailers
        //needs to get ARRAY with trailers
        //intent.putExtra("trailer_urls", filmArray.get(position).trailer_urls);
        intent.putExtra("serial_id", filmArray.get(position).serial_id);
        if (!filmArray.get(position).serial_id.equalsIgnoreCase("null")) {
            intent.putExtra("isSerial", true);
            Log.d("videoActivity_log", "startDescriptionActivity: true");
        } else {
            intent.putExtra("isSerial", false);
            Log.d("videoActivity_log", "startDescriptionActivity: false");
        }

        intent.putExtra("serial_count_seasons", filmArray.get(position).serial_count_seasons);
        intent.putExtra("season_number", filmArray.get(position).season_number);
        intent.putExtra("serial_name", filmArray.get(position).serial_name);
        intent.putExtra("serial_o_name", filmArray.get(position).serial_o_name);
        intent.putExtra("translate", filmArray.get(position).translate);

        ///////////////////////////////////////////////////////////////////////
        //put one film description to a bundle
        FilmList array = new FilmList();
        array = filmArray.get(position);
        Log.d(TAG, "startDescriptionActivity: " + array.name);
        Bundle bundle = new Bundle();
        bundle.putSerializable("array", array);
        intent.putExtras(bundle);

        //intent.putStringArrayListExtra("filmarray", filmArray.get(position));

        startActivity(intent);
    }

    private void getWorldFilms(String url, DataLoaderXML loaderXML, ArrayList<FilmList> filmListArrayList
            , RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager, DataAdapter adapter
            , ArrayList<FilmList> backupFilmListArrayList) {
        filmListArrayList.clear();
        filmListArrayList.addAll(loaderXML.getAllDataCallable(url));
        backupFilmListArrayList.addAll(filmListArrayList);
        Log.d(TAG, "getWorldFilms: size is " + backupFilmListArrayList.size());
        //dowloadCount.set(num, Integer.valueOf(dowloadCount.get(num) + Constants.COUNT));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);
    }

    private void fillRecyclerView(ArrayList<FilmList> filmListArrayList
            , RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager, DataAdapter adapter
            , ArrayList<FilmList> backupFilmListArrayList) {
        backupFilmListArrayList.addAll(filmListArrayList);
        Log.d(TAG, "getWorldFilms: size is " + backupFilmListArrayList.size());
        //dowloadCount.set(num, Integer.valueOf(dowloadCount.get(num) + Constants.COUNT));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);
    }

    private void restoreView(ArrayList<FilmList> backupedFilmList, ArrayList<FilmList> filmList
            , RecyclerView recyclerView
            , RecyclerView.LayoutManager layoutManager, DataAdapter adapter) {
        Log.d(TAG, "restoreView: restoring data");
        filmList = new ArrayList<>();
        filmList.addAll(backupedFilmList);
        adapter = new DataAdapter(this.getBaseContext().getApplicationContext(), filmList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private ArrayList<FilmList> getNewFilmList(String url, DataLoaderXML loaderXML) {
        ArrayList<FilmList> arrayList = loaderXML.getAllDataCallable(url);
        return arrayList;
    }

    private void updateList(DataAdapter adapter, ArrayList<FilmList> update
            , ArrayList<FilmList> filmList, DataLoaderXML loaderXML, String url
            , ArrayList<FilmList> backupList /*, RecyclerView recyclerView, int i*/) {
        if (adapter.getPosition() == adapter.getItemCount() - 1) {
            if (update != null) {
                Log.d(TAG, "updateList: adapter is null");
                update = null;
                update = new ArrayList<>();
            }
            adapter.offset = adapter.offset + Integer.parseInt(Constants.UPDATE_COUNT);
            update.addAll(getNewFilmList(url + Constants.COUNT + "&offset="
                    + adapter.offset, loaderXML));
            backupList.addAll(update);
            filmList.addAll(update);
            position = adapter.getItemCount() - 1;
            adapter.setItems(adapter.getItemCount() - 1);
        }
    }

    private void getGenres(String genreLink) {
        String str;
        //DataLoaderXML loaderXML = new DataLoaderXML();
        Log.d(TAG, "getGenres: " + getAllDataList.toString());
        str = getAllDataList.getList(genreLink);
        genreArrayList = getAllDataList.parseGenres(str);
    }

    private void initGenreSpinner() {
        genreID = "";
        StringBuilder stringBuilder = new StringBuilder();
        genreSpinner = findViewById(R.id.spinnerGenre);
        String[] genre = getResources().getStringArray(R.array.genres);
        SpinnerAdapter adapter = new SpinnerAdapter(this
                , R.layout.support_simple_spinner_dropdown_item, genre);
        genreSpinner.setAdapter(adapter);
        //genreSpinner.setSelection(0);
        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String line;
                line = genre[i].trim();
                for (int j = 0; j < genreArrayList.size(); j++) {
                    if (genreArrayList.get(j).title.equals(line)) {
                        stringBuilder.append("&genre_id[]=").append(genreArrayList.get(j).id);
                    }
                }
                genreID = stringBuilder.toString();
                if (stringBuilder.length() > 0) {
                    stringBuilder.setLength(0);
                }
                if (genreSpinnerFlag) {
                    newCategorySort();
                }
                genreSpinnerFlag = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    private void initCountrySpinner() {
        counrtySpinner = findViewById(R.id.spinnerCountry);
        String[] countriesID = getResources().getStringArray(R.array.countriesid);
        String[] countries = getResources().getStringArray(R.array.countries);
        SpinnerAdapter arrayAdapter = new SpinnerAdapter(this
                , R.layout.support_simple_spinner_dropdown_item, countries);
        counrtySpinner.setAdapter(arrayAdapter);
        //counrtySpinner.setSelection(0);
        counrtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        countryID = "";
                        break;
                    case 1:
                        countryID = "&made=our";
                        break;
                    case 2:
                        countryID = "&made=foreign";
                        break;
                }
                if (countrySpinnerFlag) {
                    newCategorySort();
                }
                countrySpinnerFlag = true;
                //SpinnerAdapter.flag = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void newCategorySort(String str) {
        if (str != null) {
            if (str.contains("&year[]=")) {
                yearFromTo = str;
                newCategorySort();
            }
        }
    }

    private void initSpinnerYears() {
        yearFromSpinner = findViewById(R.id.spinnerFromYear);
        yearToSpinner = findViewById(R.id.spinnerToYear);
        String[] yearFromTo = new String[years.years.size()];
        for (int i = 0; i < yearFromTo.length; i++) {
            yearFromTo[i] = years.years.get(i);
        }
        //String[] yearToArray = new String[yearFromTo.length];
        SpinnerAdapter spinnerAdapterFrom = new SpinnerAdapter(this
                , R.layout.support_simple_spinner_dropdown_item, yearFromTo);
        SpinnerAdapter spinnerAdapterTo = new SpinnerAdapter(this
                , R.layout.support_simple_spinner_dropdown_item, yearFromTo);
        yearToSpinner.setAdapter(spinnerAdapterTo);
        yearToSpinner.setSelection(yearFromTo.length - 1);
        yearFromSpinner.setAdapter(spinnerAdapterFrom);
        //yearFromSpinner.setSelection(0);
        yearFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemSelected: yearFromSpinner position is " + i);
                Log.d(TAG, "onItemSelected: yearFromTo size is " + yearFromTo.length);
                Log.d(TAG, "onItemSelected: yearFromTo in index " + i + " is " + yearFromTo[i]);
                Log.d(TAG, "onItemSelected: yearFromSpinner position is " + i);
                Log.d(TAG, "onItemSelected: yearFromSpinnerFlag is " + yearFromSpinnerFlag);
                //TODO здесь кроется говно мира и надо с ним разобраться
                if (yearFromSpinnerFlag != false) {
                    yearFrom = "&year[]=" + yearFromTo[i];
                    newCategorySort();
                }
                yearFromSpinnerFlag = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        yearToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemSelected: yearToSpinner position is " + i);
                Log.d(TAG, "onItemSelected: yearToSpinner position is " + i);
                Log.d(TAG, "onItemSelected: yearToSpinnerFlag is " + yearToSpinnerFlag);
                //TODO здесь кроется говно мира и надо с ним разобраться
                if (yearToSpinnerFlag != false) {
                    yearTo = "-" + yearFromTo[i];
                    newCategorySort();
                }
                yearToSpinnerFlag = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    public void UpdateApplication(final Integer lastAppVersion) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.alertForUpdate)
                        .setCancelable(true)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                /*Intent intent = new Intent(Intent.ACTION_VIEW);
                                String apkUrl = "https://drive.google.com/uc?export=download&id=1ae2_zIRsPc3-k4jAYnUp4oamhLpwBHp5";
                                //intent.setDataAndType(Uri.parse(apkUrl), "application/vnd.android.package-archive");
                                intent.setData(Uri.parse(apkUrl));

                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);*/


                                //file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "app-armeabi-v7a-release.apk");
                                //Uri apk = FileProvider.getUriForFile(getApplicationContext(),BuildConfig.APPLICATION_ID, file);
                                File directory = getApplication().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                                File file = new File(directory, "app-armeabi-v7a-release.apk");
                                file.setReadable(true, false);
                                if (file.exists()) {
                                    file.delete();
                                }
                                Uri fileUri = Uri.fromFile(file);
                                if (Build.VERSION.SDK_INT >= 24) {
                                    fileUri = FileProvider.getUriForFile(getApplication().getApplicationContext(),
                                            BuildConfig.APPLICATION_ID + ".fileprovider",
                                            file);
                                }
                                DownloadManager.Request request =
                                        new DownloadManager.Request(Uri.parse("https://drive.google.com/uc?export=download&id=1_yaXDy0huSFoKIddQMRVHhWMDwCr-yqZ"))
                                                .setTitle("Обновление Кинозал")
                                                .setDescription("Загрузка")
                                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                                .setDestinationUri(Uri.fromFile(file))
                                                .setAllowedOverMetered(true)
                                                .setAllowedOverRoaming(true);
                                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                downloadID = downloadManager.enqueue(request);
                                registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!getScreenOrientation()) {
                                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                                }
                                SettingsManager.put(MainActivity.this, "LastIgnoredUpdateVersion", lastAppVersion.toString());
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                try {
                    File directory = getApplication().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                    File file = new File(directory, "app-armeabi-v7a-release.apk");
                    file.setReadable(true, false);
                    if (file.exists()) {

                        if (Build.VERSION.SDK_INT > 22) {
                            Uri fileUri = Uri.fromFile(file);
                            if (Build.VERSION.SDK_INT >= 24) {
                                fileUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider",
                                        file);
                            }
                            Intent installer = new Intent(Intent.ACTION_VIEW, fileUri);
                            installer.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                            installer.setDataAndType(fileUri, "application/vnd.android" + ".package-archive");
                            installer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            getApplication().getApplicationContext().startActivity(installer);
                        } else {
                            Intent install = new Intent(Intent.ACTION_VIEW);
                            install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplication().getApplicationContext().startActivity(install);
                        }
                    } else {
                    }

                } catch (Exception e) {
                    Log.d("updater_message:", e.getMessage());

                }
            }
              /*File directory = getApplication().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(directory, "app-armeabi-v7a-release.apk");
                file.setReadable(true, false);
                Uri fileUri = Uri.fromFile(file);
                if (Build.VERSION.SDK_INT >= 24){
                    fileUri = FileProvider.getUriForFile(getApplication().getApplicationContext(),
                            BuildConfig.APPLICATION_ID + ".fileprovider",
                            file);
                }
                Intent installer = new Intent();
                installer.setAction(Intent.ACTION_INSTALL_PACKAGE);
                installer.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                installer.setDataAndType(fileUri, "application/vnd.android.package-archive");
                installer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                installer.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                getApplicationContext().startActivity(installer);*/
        }
    };

    private boolean getScreenOrientation() {
        boolean screenState = false;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            screenState = true;
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenState = false;
        }
        return screenState;
    }

    @Override
    public void onDurationSelected(@NotNull String durationString) {
        newCategorySort(durationString);
    }
}

