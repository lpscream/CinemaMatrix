package net.matrixhome.kino.gui;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
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
import android.widget.LinearLayout;
import android.widget.Spinner;

import net.matrixhome.kino.BuildConfig;
import net.matrixhome.kino.R;
import net.matrixhome.kino.data.CheckService;
import net.matrixhome.kino.data.Connection;
import net.matrixhome.kino.data.Constants;
import net.matrixhome.kino.data.DataLoaderXML;
import net.matrixhome.kino.data.FilmList;
import net.matrixhome.kino.data.Genre;
import net.matrixhome.kino.data.SettingsManager;
import net.matrixhome.kino.data.Years;
import net.matrixhome.kino.viewmodel.FilmViewModel;
import net.matrixhome.kino.viewmodel.MainViewModelFactory;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
    private Spinner countrySpinner;
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

    private LinearLayout linearLayoutLastAdded;
    private LinearLayout linearLayoutByPopularity;
    private LinearLayout linearLayoutByViews;
    private LinearLayout linearLayoutByRating;
    private LinearLayout linearLayoutByName;
    private LinearLayout linearLayoutByDatePremiere;
    private LinearLayout linearLayoutEstimated;

    private RecyclerView lastAddedRecView;
    private RecyclerView byPopularityFilmRecView;
    private RecyclerView byViewsFilmRecView;
    private RecyclerView byRatingRecView;
    private RecyclerView byNameRecView;
    private RecyclerView byDatePremiereRecView;
    private RecyclerView estimatedRecView;



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
    private FilmViewModel filmViewModel;


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
        //проверка соединения с интернет
        if (!Connection.hasConnection(this)) {
            Intent intent = new Intent(this, ConnectionActivity.class);
            startActivity(intent);
        }
        //вкл./выкл обнровления
        //new Updater().execute(this);
        checkService = new CheckService();
        if (!checkService.check()) {
            finish();
        }

        initVariables();

        initWithoutMultuThread();


        initCountrySpinner();
        //initSpinnerYears();
        setObservers();
    }

    private void initWithoutMultuThread() {
        //lastAddedFilmList
        getWorldFilms("?action=video&sort_desc=added" + "&limit=" + Constants.COUNT, getAllDataList
                , lastAddedFilmList
                , lastAddedRecView
                , lastAddedFilmsDataAdapter);
        //lastAddedFilmsDataAdapter.notifyDataSetChanged();
        //filmViewModel.add(lastAddedFilmList);
        Log.d(TAG, "onCreate: ///////////////////lastAddedFilmList");
        //byPopularityFilmFilmLists
        getWorldFilms("?action=video&sort_desc=views_month" + "&limit=" + Constants.COUNT
                , getAllDataList
                , byPopularityFilmFilmLists, byPopularityFilmRecView,
                byPopularityFilmDataAdapter);
        //byPopularityFilmDataAdapter.notifyDataSetChanged();
        Log.d(TAG, "onCreate: ///////////////////byPopularityFilmFilmLists");
        //byViewsFilmLists
        getWorldFilms("?action=video&sort_desc=views" + "&limit=" + Constants.COUNT
                , getAllDataList
                , byViewsFilmLists, byViewsFilmRecView
                , byViewsFilmDataAdapter);
        //byViewsFilmDataAdapter.notifyDataSetChanged();
        Log.d(TAG, "onCreate: ///////////////////byViewsFilmLists");
        //byRatingFilmLists
        getWorldFilms("?action=video&sort_desc=rating" + "&limit=" + Constants.COUNT
                , getAllDataList
                , byRatingFilmLists, byRatingRecView
                , byRatingDataAdapter);
        //byRatingDataAdapter.notifyDataSetChanged();
        Log.d(TAG, "onCreate: ///////////////////byRatingFilmLists");
        //byNameFilmLists
        getWorldFilms("?action=video&sort_desc=name" + "&limit=" + Constants.COUNT
                , getAllDataList
                , byNameFilmLists, byNameRecView
                , byNameDataAdapter);
        //byNameDataAdapter.notifyDataSetChanged();
        Log.d(TAG, "onCreate: ///////////////////byNameFilmLists");
        //byDatePremiereFilmLists
        getWorldFilms("?action=video&sort_desc=date_premiere" + "&limit=" + Constants.COUNT
                , getAllDataList
                , byDatePremiereFilmLists, byDatePremiereRecView
                , byDatePremiereDataAdapter);
        //byDatePremiereDataAdapter.notifyDataSetChanged();
        Log.d(TAG, "onCreate: ///////////////////byDatePremiereFilmLists");

        getWorldFilms("?action=video&sort_desc=rating_vote" + "&limit=" + Constants.COUNT
                , getAllDataList
                , estimatedFilmLists, estimatedRecView
                , estimatedDataAdapter);
        //estimatedDataAdapter.notifyDataSetChanged();
        Log.d(TAG, "onCreate: ///////////////////estimatedFilmLists");
    }

    private void newCategorySort() {
        filmViewModel.updateFilmList("?action=video&sort_desc=added" + category
                + genreID + countryID + yearFromTo
                + "&limit=" + Constants.COUNT, filmViewModel.getLASTADDED_ID());
        filmViewModel.updateFilmList("?action=video&sort_desc=views_month" + category
                + genreID + countryID + yearFromTo
                + "&limit=" + Constants.COUNT, filmViewModel.getBYPOPULARITY_ID());
        filmViewModel.updateFilmList("?action=video&sort_desc=views" + category
                + genreID + countryID + yearFromTo
                + "&limit=" + Constants.COUNT, filmViewModel.getBYVIEW_ID());
        filmViewModel.updateFilmList("?action=video&sort_desc=rating" + category
                + genreID + countryID + yearFromTo
                + "&limit=" + Constants.COUNT, filmViewModel.getBYRATING_ID());
        filmViewModel.updateFilmList("?action=video&sort_desc=name" + category
                + genreID + countryID + yearFromTo
                + "&limit=" + Constants.COUNT, filmViewModel.getBYNAME_ID());
        filmViewModel.updateFilmList("?action=video&sort_desc=date_premiere" + category
                + genreID + countryID + yearFromTo
                + "&limit=" + Constants.COUNT, filmViewModel.getBYDATEPREMIER_ID());
        filmViewModel.updateFilmList("?action=video&sort_desc=rating_vote" + category
                + genreID + countryID + yearFromTo
                + "&limit=" + Constants.COUNT, filmViewModel.getESTIMATED_ID());
    }

    private void initVariables() {
        linearLayoutLastAdded = findViewById(R.id.linearLayoutLastAdded);
        linearLayoutByPopularity = findViewById(R.id.linearLayoutByPopularity);
        linearLayoutByViews = findViewById(R.id.linearLayoutByViews);
        linearLayoutByRating = findViewById(R.id.linearLayoutByRating);
        linearLayoutByName = findViewById(R.id.linearLayoutByName);
        linearLayoutByDatePremiere = findViewById(R.id.linearLayoutByDatePremiere);
        linearLayoutEstimated = findViewById(R.id.linearLayoutEstimated);

        linearLayoutLastAdded.setVisibility(View.GONE);
        linearLayoutByPopularity.setVisibility(View.GONE);
        linearLayoutByViews.setVisibility(View.GONE);
        linearLayoutByRating.setVisibility(View.GONE);
        linearLayoutByName.setVisibility(View.GONE);
        linearLayoutByDatePremiere.setVisibility(View.GONE);
        linearLayoutEstimated.setVisibility(View.GONE);


        getAllDataList = new DataLoaderXML(10);
        settingsManager = new SettingsManager();
        filmViewModel = new ViewModelProvider(MainActivity.this, new MainViewModelFactory(getApplication())).get(FilmViewModel.class);

        genreID = "";
        countryID = "";
        yearFrom = "";
        yearTo = "";
        yearFromTo = "";
        superLayout = findViewById(R.id.superLayout);
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


        lastAddedFilmList = new ArrayList<>();
        byPopularityFilmFilmLists = new ArrayList<>();
        byViewsFilmLists = new ArrayList<>();
        byRatingFilmLists = new ArrayList<>();
        byNameFilmLists = new ArrayList<>();
        byDatePremiereFilmLists = new ArrayList<>();
        estimatedFilmLists = new ArrayList<>();


        lastAddedRecView = findViewById(R.id.lastAddedRecView);
        byPopularityFilmRecView = findViewById(R.id.byPopularityFilmRecView);
        byViewsFilmRecView = findViewById(R.id.byViewsFilmRecView);
        byRatingRecView = findViewById(R.id.byRatingRecView);
        byNameRecView = findViewById(R.id.byNameRecView);
        byDatePremiereRecView = findViewById(R.id.byDatePremiereRecView);
        estimatedRecView = findViewById(R.id.estimatedRecView);

        //TODO check list
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        lastAddedFilmsDataAdapter = new DataAdapter(this.getBaseContext().getApplicationContext()
                , filmViewModel.getLastAddedFilmList().getValue());
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
                updateList(lastAddedFilmsDataAdapter
                        , lastAddedFilmList, getAllDataList
                        , "?action=video&sort_desc=added" + category
                                + genreID + countryID + yearFromTo
                                + "&limit=", filmViewModel.getLASTADDED_ID());
            }
        });
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        byPopularityFilmDataAdapter = new DataAdapter(this.getBaseContext().getApplicationContext()
                , filmViewModel.getByPopularityFilmFilmLists().getValue());
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
                updateList(byPopularityFilmDataAdapter
                        , byPopularityFilmFilmLists, getAllDataList
                        , "?action=video&sort_desc=views_month" + category
                                + genreID + countryID + yearFromTo
                                + "&limit=", filmViewModel.getBYPOPULARITY_ID());
            }
        });
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        byViewsFilmDataAdapter = new DataAdapter(this.getBaseContext().getApplicationContext()
                , filmViewModel.getByViewsFilmLists().getValue());
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
                updateList(byViewsFilmDataAdapter
                        , byViewsFilmLists, getAllDataList
                        , "?action=video&sort_desc=views" + category
                                + genreID + countryID + yearFromTo
                                + "&limit=", filmViewModel.getBYVIEW_ID());
            }
        });
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        byRatingDataAdapter = new DataAdapter(this.getBaseContext().getApplicationContext()
                , filmViewModel.getByRatingFilmLists().getValue());
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
                updateList(byRatingDataAdapter
                        , byRatingFilmLists, getAllDataList
                        , "?action=video&sort_desc=rating" + category
                                + genreID + countryID + yearFromTo
                                + "&limit=", filmViewModel.getBYRATING_ID());
            }
        });
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        byNameDataAdapter = new DataAdapter(this.getBaseContext().getApplicationContext()
                , filmViewModel.getByNameFilmLists().getValue());
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
                updateList(byNameDataAdapter
                        , byNameFilmLists, getAllDataList
                        , "?action=video&sort_desc=name" + category
                                + genreID + countryID + yearFromTo
                                + "&limit=", filmViewModel.getBYNAME_ID());
            }
        });
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        byDatePremiereDataAdapter = new DataAdapter(this.getBaseContext().getApplicationContext()
                , filmViewModel.getByDatePremiereFilmLists().getValue());
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
                updateList(byDatePremiereDataAdapter, byDatePremiereFilmLists
                        , getAllDataList
                        , "?action=video&sort_desc=date_premiere" + category
                                + genreID + countryID + yearFromTo
                                + "&limit=", filmViewModel.getBYDATEPREMIER_ID());
            }
        });
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        estimatedDataAdapter = new DataAdapter(this.getBaseContext().getApplicationContext()
                , filmViewModel.getEstimatedFilmLists().getValue());
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
                updateList(estimatedDataAdapter
                        , estimatedFilmLists, getAllDataList
                        , "?action=video&sort_desc=rating_vote" + category
                                + genreID + countryID + yearFromTo
                                + "&limit=", filmViewModel.getESTIMATED_ID());
            }
        });
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////
        ///////////////////////////////



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
            , RecyclerView recyclerView, DataAdapter adapter
            ) {
        filmListArrayList.clear();
        //filmListArrayList.addAll(loaderXML.getAllDataCallable(url));
        //dowloadCount.set(num, Integer.valueOf(dowloadCount.get(num) + Constants.COUNT));
        recyclerView.setLayoutManager(new LinearLayoutManager(this
                , LinearLayoutManager.HORIZONTAL
                , false));
        recyclerView.setHasFixedSize(false);
        //recyclerView.setAdapter(adapter);
    }

    private void fillRecyclerView(ArrayList<FilmList> filmListArrayList
            , RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager, DataAdapter adapter
            , ArrayList<FilmList> backupFilmListArrayList) {
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);
    }

    private void updateList(DataAdapter adapter
            , ArrayList<FilmList> filmList, DataLoaderXML loaderXML, String url
            , Integer id) {
        if (adapter.getPosition() == adapter.getItemCount() - 1) {
            /*if (update != null) {
                Log.d(TAG, "updateList: adapter is null");
                update = null;
                update = new ArrayList<>();
            }
            adapter.offset = adapter.offset + Integer.parseInt(Constants.UPDATE_COUNT);
            update.addAll(getNewFilmList(url + Constants.COUNT + "&offset="
                    + adapter.offset, loaderXML));
            filmList.addAll(update);
            position = adapter.getItemCount() - 1;
            adapter.setItems(adapter.getItemCount() - 1);*/
            updateViewModelFilmList(url,id);
        }
    }


    private void updateViewModelFilmList(String url, Integer id){
        switch (id) {
            case 0:
                filmViewModel.updateFilmList(url,0);
                break;
            case 1:
                filmViewModel.updateFilmList(url,1);
                break;
            case 2:
                filmViewModel.updateFilmList(url,2);
                break;
            case 3:
                filmViewModel.updateFilmList(url,3);
                break;
            case 4:
                filmViewModel.updateFilmList(url,4);
                break;
            case 5:
                filmViewModel.updateFilmList(url,5);
                break;
            case 6:
                filmViewModel.updateFilmList(url,6);
                break;
        }
    }

    private void setObservers(){
        filmViewModel.getGenreArrayList().observe(this, new Observer<ArrayList<Genre>>() {
            @Override
            public void onChanged(ArrayList<Genre> genres) {
                genreArrayList = filmViewModel.getGenreArrayList().getValue();
                initGenreSpinner();
            }
        });

        filmViewModel.getLastAddedFilmList().observe(this, new Observer<ArrayList<FilmList>>() {
            @Override
            public void onChanged(ArrayList<FilmList> filmLists) {
                linearLayoutLastAdded.setVisibility(View.VISIBLE);
                lastAddedFilmsDataAdapter = new DataAdapter(MainActivity.this
                        , filmViewModel.getLastAddedFilmList().getValue());
                lastAddedRecView.setAdapter(lastAddedFilmsDataAdapter);
                lastAddedFilmsDataAdapter.notifyDataSetChanged();
            }
        });
        filmViewModel.getByDatePremiereFilmLists().observe(this, new Observer<ArrayList<FilmList>>() {
            @Override
            public void onChanged(ArrayList<FilmList> filmLists) {
                linearLayoutByDatePremiere.setVisibility(View.VISIBLE);
                byDatePremiereDataAdapter = new DataAdapter(MainActivity.this
                , filmViewModel.getByDatePremiereFilmLists().getValue());
                byDatePremiereRecView.setAdapter(byDatePremiereDataAdapter);
                byDatePremiereDataAdapter.notifyDataSetChanged();
            }
        });
        filmViewModel.getByNameFilmLists().observe(this, new Observer<ArrayList<FilmList>>() {
            @Override
            public void onChanged(ArrayList<FilmList> filmLists) {
                linearLayoutByName.setVisibility(View.VISIBLE);
                byNameDataAdapter = new DataAdapter(MainActivity.this
                        , filmViewModel.getByNameFilmLists().getValue());
                byNameRecView.setAdapter(byNameDataAdapter);
                byNameDataAdapter.notifyDataSetChanged();
               }
        });
        filmViewModel.getByPopularityFilmFilmLists().observe(this, new Observer<ArrayList<FilmList>>() {
            @Override
            public void onChanged(ArrayList<FilmList> filmLists) {
                linearLayoutByPopularity.setVisibility(View.VISIBLE);
                byPopularityFilmDataAdapter = new DataAdapter(MainActivity.this
                        , filmViewModel.getByPopularityFilmFilmLists().getValue());
                byPopularityFilmRecView.setAdapter(byPopularityFilmDataAdapter);
                byPopularityFilmDataAdapter.notifyDataSetChanged();
                }
        });
        filmViewModel.getByRatingFilmLists().observe(this, new Observer<ArrayList<FilmList>>() {
            @Override
            public void onChanged(ArrayList<FilmList> filmLists) {
                linearLayoutByRating.setVisibility(View.VISIBLE);
                byRatingDataAdapter = new DataAdapter(MainActivity.this
                        , filmViewModel.getByRatingFilmLists().getValue());
                byRatingRecView.setAdapter(byRatingDataAdapter);
                byRatingDataAdapter.notifyDataSetChanged();
               }
        });
        filmViewModel.getByViewsFilmLists().observe(this, new Observer<ArrayList<FilmList>>() {
            @Override
            public void onChanged(ArrayList<FilmList> filmLists) {
                linearLayoutByViews.setVisibility(View.VISIBLE);
                byViewsFilmDataAdapter = new DataAdapter(MainActivity.this
                        , filmViewModel.getByViewsFilmLists().getValue());
                byViewsFilmRecView.setAdapter(byViewsFilmDataAdapter);
                byViewsFilmDataAdapter.notifyDataSetChanged();
                 }
        });
        filmViewModel.getEstimatedFilmLists().observe(this, new Observer<ArrayList<FilmList>>() {
            @Override
            public void onChanged(ArrayList<FilmList> filmLists) {
                linearLayoutEstimated.setVisibility(View.VISIBLE);
                estimatedDataAdapter = new DataAdapter(MainActivity.this
                        , filmViewModel.getEstimatedFilmLists().getValue());
                estimatedRecView.setAdapter(estimatedDataAdapter);
                estimatedDataAdapter.notifyDataSetChanged();
            }
        });
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
        countrySpinner = findViewById(R.id.spinnerCountry);
        String[] countriesID = getResources().getStringArray(R.array.countriesid);
        String[] countries = getResources().getStringArray(R.array.countries);
        SpinnerAdapter arrayAdapter = new SpinnerAdapter(this
                , R.layout.support_simple_spinner_dropdown_item, countries);
        countrySpinner.setAdapter(arrayAdapter);
        //counrtySpinner.setSelection(0);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

