package net.matrixhome.kino.gui

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.matrixhome.kino.BuildConfig
import net.matrixhome.kino.R
import net.matrixhome.kino.data.*
import net.matrixhome.kino.services.BroadcastRecieverOnDowloadComplete
import net.matrixhome.kino.viewmodel.FilmViewModel
import net.matrixhome.kino.viewmodel.MainViewModelFactory
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URL
import java.util.*


class FilmCatalogueActivity : AppCompatActivity(), DataDurrationFragmnet.OnSelectedDurationListener {

    private val SEARCHRESPONSE: String = "searchResponse"
    private val TAG: String = "FilmCatalogueActivity_log"
    private var downloadID: Long = 0

    private var updateStatus = true

    private lateinit var settingsManager: SettingsManager
    private lateinit var checkService: CheckService

    private lateinit var genreArrayList: ArrayList<Genre>
    private lateinit var years: Years
    private lateinit var genreSpinner: Spinner
    private lateinit var countrySpinner: Spinner
    private lateinit var yearFromSpinner: Spinner
    private lateinit var yearToSpinner: Spinner
    private lateinit var yearDurationBtn: ImageButton

    private var genreSpinnerFlag: Boolean = false
    private var countrySpinnerFlag: Boolean = false
    private var yearFromSpinnerFlag: Boolean = false
    private var yearToSpinnerFlag: Boolean = false

    private var genreID: String = ""
    private var countryID: String = ""
    private var yearFrom: String = ""
    private var yearTo: String = ""
    private var yearFromTo: String = ""

    private lateinit var filmsBtn: Button
    private lateinit var animBtn: Button
    private lateinit var serialBtn: Button
    private lateinit var animSerialBtn: Button
    private lateinit var tvShowBtn: Button

    private lateinit var progressBar: ProgressBar

    private lateinit var linearLayoutLastAdded: LinearLayout
    private lateinit var linearLayoutByPopularity: LinearLayout
    private lateinit var linearLayoutByViews: LinearLayout
    private lateinit var linearLayoutByRating: LinearLayout
    private lateinit var linearLayoutByName: LinearLayout
    private lateinit var linearLayoutByDatePremiere: LinearLayout
    private lateinit var linearLayoutEstimated: LinearLayout

    private lateinit var lastAddedRecView: RecyclerView
    private lateinit var byPopularityFilmRecView: RecyclerView
    private lateinit var byViewsFilmRecView: RecyclerView
    private lateinit var byRatingRecView: RecyclerView
    private lateinit var byNameRecView: RecyclerView
    private lateinit var byDatePremiereRecView: RecyclerView
    private lateinit var estimatedRecView: RecyclerView

    private lateinit var lastAddedFilmsDataAdapter: DataAdapter
    private lateinit var byPopularityFilmDataAdapter: DataAdapter
    private lateinit var byViewsFilmDataAdapter: DataAdapter
    private lateinit var byRatingDataAdapter: DataAdapter
    private lateinit var byNameDataAdapter: DataAdapter
    private lateinit var byDatePremiereDataAdapter: DataAdapter
    private lateinit var estimatedDataAdapter: DataAdapter

    private lateinit var superLayout: ConstraintLayout
    private lateinit var searchTV: EditText
    private lateinit var datePickerFragment: DataDurrationFragmnet

    private lateinit var filmViewModel:FilmViewModel




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!Connection.hasConnection(applicationContext)){
            var intent = Intent(application, ConnectionActivity::class.java)
            startActivity(intent)
        }

        checkService = CheckService()
        if (!checkService.check()){
            finish()
        }

        filmViewModel = ViewModelProvider(this, MainViewModelFactory(application)).get(FilmViewModel::class.java)




        initVariables()
        setLayoutsGone()
        setLinearLayoutsToRecyclerView()
        setOnClickListeners()
        setOnScrollListeners()
        setObservers()
        initCountrySpinner()
        setSearchTvOnKeyListener()
        setOnIterceptFocusSearch()
    }

    private fun setOnIterceptFocusSearch() {
        lastAddedRecView.layoutManager = object: LinearLayoutManager(this
                , LinearLayoutManager.HORIZONTAL, false){
            override fun onInterceptFocusSearch(focused: View, direction: Int): View? {
                if (direction == View.FOCUS_RIGHT) {
                    val pos = getPosition(focused)
                    if (pos == itemCount - 1)
                        return focused
                }
                return super.onInterceptFocusSearch(focused, direction)
            }
        }

        byPopularityFilmRecView.layoutManager = object: LinearLayoutManager(this
                , LinearLayoutManager.HORIZONTAL, false){
            override fun onInterceptFocusSearch(focused: View, direction: Int): View? {
                if (direction == View.FOCUS_RIGHT) {
                    val pos = getPosition(focused)
                    if (pos == itemCount - 1)
                        return focused
                }
                return super.onInterceptFocusSearch(focused, direction)
            }
        }
        byViewsFilmRecView.layoutManager = object: LinearLayoutManager(this
                , LinearLayoutManager.HORIZONTAL, false){
            override fun onInterceptFocusSearch(focused: View, direction: Int): View? {
                if (direction == View.FOCUS_RIGHT) {
                    val pos = getPosition(focused)
                    if (pos == itemCount - 1)
                        return focused
                }
                return super.onInterceptFocusSearch(focused, direction)
            }
        }
        byRatingRecView.layoutManager = object: LinearLayoutManager(this
                , LinearLayoutManager.HORIZONTAL, false){
            override fun onInterceptFocusSearch(focused: View, direction: Int): View? {
                if (direction == View.FOCUS_RIGHT) {
                    val pos = getPosition(focused)
                    if (pos == itemCount - 1)
                        return focused
                }
                return super.onInterceptFocusSearch(focused, direction)
            }
        }
        byNameRecView.layoutManager = object: LinearLayoutManager(this
                , LinearLayoutManager.HORIZONTAL, false){
            override fun onInterceptFocusSearch(focused: View, direction: Int): View? {
                if (direction == View.FOCUS_RIGHT) {
                    val pos = getPosition(focused)
                    if (pos == itemCount - 1)
                        return focused
                }
                return super.onInterceptFocusSearch(focused, direction)
            }
        }
        byDatePremiereRecView.layoutManager = object: LinearLayoutManager(this
                , LinearLayoutManager.HORIZONTAL, false){
            override fun onInterceptFocusSearch(focused: View, direction: Int): View? {
                if (direction == View.FOCUS_RIGHT) {
                    val pos = getPosition(focused)
                    if (pos == itemCount - 1)
                        return focused
                }
                return super.onInterceptFocusSearch(focused, direction)
            }
        }
        estimatedRecView.layoutManager = object: LinearLayoutManager(this
                , LinearLayoutManager.HORIZONTAL, false){
            override fun onInterceptFocusSearch(focused: View, direction: Int): View? {
                if (direction == View.FOCUS_RIGHT) {
                    val pos = getPosition(focused)
                    if (pos == itemCount - 1)
                        return focused
                }
                return super.onInterceptFocusSearch(focused, direction)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //TODO updater needs to make in coroutines cause of asynctask is depricated
        //вкл/выкл обновление
        checkUpdates()
    }

    private fun initGenreSpinner() {
        var stringBuilder: StringBuilder = kotlin.text.StringBuilder()
        genreSpinner = findViewById(R.id.spinnerGenre)
        var genre = resources.getStringArray(R.array.genres)
        var adapter: SpinnerAdapter = SpinnerAdapter(this, R.layout.support_simple_spinner_dropdown_item, genre)
        genreSpinner.adapter = adapter
        genreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var line: String
                line = genre[p2].toString()
                for (j in 0..(filmViewModel.genreArrayList.value!!.size - 1)){
                    if (filmViewModel.genreArrayList.value!!.get(j).title.equals(line)){
                        stringBuilder.append("&genre_id[]=").append(filmViewModel.genreArrayList.value!!.get(j).id)
                    }
                }
                filmViewModel.genreID.value = stringBuilder.toString()
                if (stringBuilder.length > 0){
                    stringBuilder.setLength(0)
                }
                if (genreSpinnerFlag){
                    //update new list of films
                    filmViewModel.getNewSortedFilList()
                    setLayoutsGone()
                    notifyAllDataSetChanged()
                }
                //set flag to set listener work
                genreSpinnerFlag = true
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun initCountrySpinner(){
        countrySpinner = findViewById(R.id.spinnerCountry)
        var countries = resources.getStringArray(R.array.countries)
        var spinnerAdapter: SpinnerAdapter = SpinnerAdapter(this,
                R.layout.support_simple_spinner_dropdown_item, countries)
        countrySpinner.adapter = spinnerAdapter
        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when(p2){
                    0 -> filmViewModel.countryID.value = ""
                    1 -> filmViewModel.countryID.value = "&made=our"
                    2 -> filmViewModel.countryID.value = "&made=foreign"
                }
                if (countrySpinnerFlag){
                    //update film list
                    filmViewModel.getNewSortedFilList()
                    setLayoutsGone()
                    notifyAllDataSetChanged()
                }
                countrySpinnerFlag = true
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun initVariables() {
        linearLayoutLastAdded = findViewById(R.id.linearLayoutLastAdded)
        linearLayoutByPopularity = findViewById(R.id.linearLayoutByPopularity)
        linearLayoutByViews = findViewById(R.id.linearLayoutByViews)
        linearLayoutByRating = findViewById(R.id.linearLayoutByRating)
        linearLayoutByName = findViewById(R.id.linearLayoutByName)
        linearLayoutByDatePremiere = findViewById(R.id.linearLayoutByDatePremiere)
        linearLayoutEstimated = findViewById(R.id.linearLayoutEstimated)

        superLayout = findViewById(R.id.superLayout)
        yearDurationBtn = findViewById(R.id.yearDurationBtn)
        yearDurationBtn.setOnClickListener(View.OnClickListener {
            datePickerFragment = DataDurrationFragmnet()
            datePickerFragment.show(supportFragmentManager, "datePickerFragment")
        })

        filmsBtn = findViewById(R.id.filmsBtn)
        animBtn = findViewById(R.id.animBtn)
        serialBtn = findViewById(R.id.serialBtn)
        animSerialBtn = findViewById(R.id.animSerialBtn)
        tvShowBtn = findViewById(R.id.tvShowBtn)

        years = Years()

        lastAddedRecView = findViewById(R.id.lastAddedRecView)
        byPopularityFilmRecView = findViewById(R.id.byPopularityFilmRecView)
        byViewsFilmRecView = findViewById(R.id.byViewsFilmRecView)
        byRatingRecView = findViewById(R.id.byRatingRecView)
        byNameRecView = findViewById(R.id.byNameRecView)
        byDatePremiereRecView = findViewById(R.id.byDatePremiereRecView)
        estimatedRecView = findViewById(R.id.estimatedRecView)

        settingsManager = SettingsManager()


        lastAddedRecView.setHasFixedSize(false)
        byPopularityFilmRecView.setHasFixedSize(false)
        byViewsFilmRecView.setHasFixedSize(false)
        byRatingRecView.setHasFixedSize(false)
        byNameRecView.setHasFixedSize(false)
        byDatePremiereRecView.setHasFixedSize(false)
        estimatedRecView.setHasFixedSize(false)

        searchTV = findViewById(R.id.searchTV)

    }

    private fun setLayoutsGone(){
        linearLayoutLastAdded.visibility = View.GONE
        linearLayoutByPopularity.visibility = View.GONE
        linearLayoutByViews.visibility = View.GONE
        linearLayoutByRating.visibility = View.GONE
        linearLayoutByName.visibility = View.GONE
        linearLayoutByDatePremiere.visibility = View.GONE
        linearLayoutEstimated.visibility = View.GONE
    }

    private fun setSearchTvOnKeyListener(){
        searchTV.setOnKeyListener(View.OnKeyListener{ view: View, i: Int, keyEvent: KeyEvent ->
            when(i){
                KeyEvent.KEYCODE_ENTER -> {
                    if (searchTV.text.toString().length > 2){
                        startSearchActivity(searchTV.text.toString())
                        return@OnKeyListener true
                    }
                }
                KeyEvent.KEYCODE_DPAD_CENTER -> {
                    var imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(searchTV, InputMethodManager.SHOW_IMPLICIT)
                }
            }

            false
        })
    }

    private fun startSearchActivity(response: String) {
        var intent: Intent = Intent(application, SearchActivity::class.java)
        intent.putExtra(SEARCHRESPONSE, response)
        startActivity(intent)
    }

    private fun setObservers(){
        filmViewModel.lastAddedFilmList.observe(this, Observer {
            linearLayoutLastAdded.visibility = View.VISIBLE
            if (filmViewModel.updateListState1) {
                lastAddedFilmsDataAdapter = DataAdapter(this, filmViewModel.lastAddedFilmList.value!!)
                lastAddedRecView.adapter = lastAddedFilmsDataAdapter
                lastAddedFilmsDataAdapter.setClickListener(DataAdapter.ItemClickListener { view, position ->
                    startDescriptionActivity(filmViewModel.lastAddedFilmList.value!!, position)
                })
            }
            lastAddedFilmsDataAdapter.notifyItemRangeInserted(filmViewModel.lastAddedItemsCount, filmViewModel.lastAddedFilmList.value!!.size)
            filmViewModel.updateListState1 = false
        })
        filmViewModel.byPopularityFilmFilmLists.observe(this, Observer {
            linearLayoutByPopularity.visibility = View.VISIBLE
            if (filmViewModel.updateListState2) {
                byPopularityFilmDataAdapter = DataAdapter(this, filmViewModel.byPopularityFilmFilmLists.value!!)
                byPopularityFilmRecView.adapter = byPopularityFilmDataAdapter
                byPopularityFilmDataAdapter.setClickListener(DataAdapter.ItemClickListener { view, position ->
                    startDescriptionActivity(filmViewModel.byPopularityFilmFilmLists.value!!, position)
                })
            }
            byPopularityFilmDataAdapter.notifyItemRangeInserted(filmViewModel.byPopularityItemsCount, filmViewModel.byPopularityFilmFilmLists.value!!.size)
            filmViewModel.updateListState2 = false
        })
        filmViewModel.byViewsFilmLists.observe(this, Observer {
            linearLayoutByViews.visibility = View.VISIBLE
            if (filmViewModel.updateListState3) {
                byViewsFilmDataAdapter = DataAdapter(this, filmViewModel.byViewsFilmLists.value!!)
                byViewsFilmRecView.adapter = byViewsFilmDataAdapter
                byViewsFilmDataAdapter.setClickListener(DataAdapter.ItemClickListener { view, position ->
                    startDescriptionActivity(filmViewModel.byViewsFilmLists.value!!, position)
                })
            }
            byViewsFilmDataAdapter.notifyItemRangeInserted(filmViewModel.byViewsItemsCount, filmViewModel.byViewsFilmLists.value!!.size)
            filmViewModel.updateListState3 = false
        })
        filmViewModel.byRatingFilmLists.observe(this, Observer {
            linearLayoutByRating.visibility = View.VISIBLE
            if (filmViewModel.updateListState4) {
                byRatingDataAdapter = DataAdapter(this, filmViewModel.byRatingFilmLists.value!!)
                byRatingRecView.adapter = byRatingDataAdapter
                byRatingDataAdapter.setClickListener(DataAdapter.ItemClickListener { view, position ->
                    startDescriptionActivity(filmViewModel.byRatingFilmLists.value!!, position)
                })
            }
            byRatingDataAdapter.notifyItemRangeInserted(filmViewModel.byRatingItemsCount, filmViewModel.byRatingFilmLists.value!!.size)
            filmViewModel.updateListState4 = false
        })
        filmViewModel.byNameFilmLists.observe(this, Observer {
            linearLayoutByName.visibility = View.VISIBLE
            if (filmViewModel.updateListState5) {
                byNameDataAdapter = DataAdapter(this, filmViewModel.byNameFilmLists.value!!)
                byNameRecView.adapter = byNameDataAdapter
                byNameDataAdapter.setClickListener(DataAdapter.ItemClickListener { view, position ->
                    startDescriptionActivity(filmViewModel.byNameFilmLists.value!!, position)
                })
            }
            byNameDataAdapter.notifyItemRangeInserted(filmViewModel.byNameItemsCount, filmViewModel.byNameFilmLists.value!!.size)
            filmViewModel.updateListState5 = false
        })
        filmViewModel.byDatePremiereFilmLists.observe(this, Observer {
            linearLayoutByDatePremiere.visibility = View.VISIBLE
            if (filmViewModel.updateListState6) {
                byDatePremiereDataAdapter = DataAdapter(this, filmViewModel.byDatePremiereFilmLists.value!!)
                byDatePremiereRecView.adapter = byDatePremiereDataAdapter
                byDatePremiereDataAdapter.setClickListener(DataAdapter.ItemClickListener { view, position ->
                    startDescriptionActivity(filmViewModel.byDatePremiereFilmLists.value!!, position)
                })
            }
            byDatePremiereDataAdapter.notifyItemRangeInserted(filmViewModel.byDateItemsCount, filmViewModel.byDatePremiereFilmLists.value!!.size)
            filmViewModel.updateListState6 = false
        })
        filmViewModel.estimatedFilmLists.observe(this, Observer {
            linearLayoutEstimated.visibility = View.VISIBLE
            if (filmViewModel.updateListState7) {
                estimatedDataAdapter = DataAdapter(this, filmViewModel.estimatedFilmLists.value!!)
                estimatedRecView.adapter = estimatedDataAdapter
                estimatedDataAdapter.setClickListener(DataAdapter.ItemClickListener { view, position ->
                    startDescriptionActivity(filmViewModel.estimatedFilmLists.value!!, position)
                })
            }
            estimatedDataAdapter.notifyItemRangeInserted(filmViewModel.estimatedItemsCount, filmViewModel.estimatedFilmLists.value!!.size)
            filmViewModel.updateListState7 = false
        })

        filmViewModel.genreArrayList.observe(this, Observer {
            initGenreSpinner()
        })
    }

    private fun setLinearLayoutsToRecyclerView(){
        lastAddedRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        byPopularityFilmRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        byViewsFilmRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        byRatingRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        byNameRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        byDatePremiereRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        estimatedRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun notifyAllDataSetChanged(){
        lastAddedFilmsDataAdapter.notifyDataSetChanged()
        byPopularityFilmDataAdapter.notifyDataSetChanged()
        byViewsFilmDataAdapter.notifyDataSetChanged()
        byRatingDataAdapter.notifyDataSetChanged()
        byNameDataAdapter.notifyDataSetChanged()
        byDatePremiereDataAdapter.notifyDataSetChanged()
        estimatedDataAdapter.notifyDataSetChanged()
    }

    private fun setOnClickListeners(){
        filmsBtn.setOnClickListener {
            filmViewModel.genreID.value = ""
            filmViewModel.countryID.value = ""
            filmViewModel.yearFromTo.value = ""
            filmViewModel.category.value = "&category[]=our-film&category[]=world-film"
            //need to reset film list
            filmViewModel.getNewSortedFilList()
            setLayoutsGone()
            notifyAllDataSetChanged()
        }

        animBtn.setOnClickListener {
            filmViewModel.genreID.value = ""
            filmViewModel.countryID.value = ""
            filmViewModel.yearFromTo.value = ""
            filmViewModel.category.value = "&category=animation"
            //need to reset film list
            filmViewModel.getNewSortedFilList()
            setLayoutsGone()
            notifyAllDataSetChanged()
        }

        serialBtn.setOnClickListener {
            filmViewModel.genreID.value = ""
            filmViewModel.countryID.value = ""
            filmViewModel.yearFromTo.value = ""
            filmViewModel.category.value = "&category[]=world-serial&category[]=owr-serial"
            //need to reset film list
            filmViewModel.getNewSortedFilList()
            setLayoutsGone()
            notifyAllDataSetChanged()
        }

        animSerialBtn.setOnClickListener {
            filmViewModel.genreID.value = ""
            filmViewModel.countryID.value = ""
            filmViewModel.yearFromTo.value = ""
            filmViewModel.category.value = "&category=animation-serial"
            //need to reset film list
            filmViewModel.getNewSortedFilList()
            setLayoutsGone()
            notifyAllDataSetChanged()
        }

        tvShowBtn.setOnClickListener {
            filmViewModel.genreID.value = ""
            filmViewModel.countryID.value = ""
            filmViewModel.yearFromTo.value = ""
            filmViewModel.category.value = "&category=tv-shows"
            //need to reset film list
            filmViewModel.getNewSortedFilList()
            setLayoutsGone()
            notifyAllDataSetChanged()
        }
    }

    private fun setOnScrollListeners(){
        lastAddedRecView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.d(TAG, "onScrollStateChanged: " + newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    filmViewModel.lastAddedItemsCount = filmViewModel.lastAddedFilmList.value!!.size
                    addNewItemToRecyclerView(lastAddedFilmsDataAdapter,
                            filmViewModel.ACTION_VIDEO
                                    + filmViewModel.SORT_DESC_ADDED
                                    + filmViewModel.category.value.toString()
                                    + filmViewModel.genreID.value.toString()
                                    + filmViewModel.countryID.value.toString()
                                    + filmViewModel.yearFromTo.value.toString()
                                    + filmViewModel.LIMIT, filmViewModel.LASTADDED_ID, newState)
                }
            }
        })

        lastAddedRecView.layoutManager = object: LinearLayoutManager(this
                , LinearLayoutManager.HORIZONTAL, false){
            override fun onInterceptFocusSearch(focused: View, direction: Int): View? {
                if (direction == View.FOCUS_RIGHT) {
                    val pos = getPosition(focused)
                    if (pos == itemCount - 1)
                        return focused
                }
                return super.onInterceptFocusSearch(focused, direction)
            }
        }
        byPopularityFilmRecView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.d(TAG, "onScrollStateChanged: " + newState)

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    filmViewModel.byPopularityItemsCount = filmViewModel.byPopularityFilmFilmLists.value!!.size
                    addNewItemToRecyclerView(byPopularityFilmDataAdapter,
                            filmViewModel.ACTION_VIDEO
                                    + filmViewModel.SORT_DESC_VIEWS_MONTH
                                    + filmViewModel.category.value.toString()
                                    + filmViewModel.genreID.value.toString()
                                    + filmViewModel.countryID.value.toString()
                                    + filmViewModel.yearFromTo.value.toString()
                                    + filmViewModel.LIMIT, filmViewModel.BYPOPULARITY_ID, newState)
                }
            }
        })
        byViewsFilmRecView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.d(TAG, "onScrollStateChanged: " + newState)

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    filmViewModel.byViewsItemsCount = filmViewModel.byViewsFilmLists.value!!.size
                    addNewItemToRecyclerView(byViewsFilmDataAdapter, filmViewModel.ACTION_VIDEO
                            + filmViewModel.SORT_DESC_VIEWS
                            + filmViewModel.category.value.toString()
                            + filmViewModel.genreID.value.toString()
                            + filmViewModel.countryID.value.toString()
                            + filmViewModel.yearFromTo.value.toString()
                            + filmViewModel.LIMIT, filmViewModel.BYVIEW_ID, newState)
                }
            }
        })
        byRatingRecView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    filmViewModel.byRatingItemsCount = filmViewModel.byRatingFilmLists.value!!.size
                    addNewItemToRecyclerView(byRatingDataAdapter, filmViewModel.ACTION_VIDEO
                            + filmViewModel.SORT_DESC_RATING
                            + filmViewModel.category.value.toString()
                            + filmViewModel.genreID.value.toString()
                            + filmViewModel.countryID.value.toString()
                            + filmViewModel.yearFromTo.value.toString()
                            + filmViewModel.LIMIT, filmViewModel.BYRATING_ID, newState)
                }
            }
        })
        byNameRecView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.d(TAG, "onScrollStateChanged: " + newState)

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    filmViewModel.byNameItemsCount = filmViewModel.byNameFilmLists.value!!.size
                    addNewItemToRecyclerView(byNameDataAdapter, filmViewModel.ACTION_VIDEO
                            + filmViewModel.SORT_DESC_NAME
                            + filmViewModel.category.value.toString()
                            + filmViewModel.genreID.value.toString()
                            + filmViewModel.countryID.value.toString()
                            + filmViewModel.yearFromTo.value.toString()
                            + filmViewModel.LIMIT, filmViewModel.BYNAME_ID, newState)
                }
            }
        })
        byDatePremiereRecView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.d(TAG, "onScrollStateChanged: " + newState)

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    filmViewModel.byDateItemsCount = filmViewModel.byDatePremiereFilmLists.value!!.size
                    addNewItemToRecyclerView(byDatePremiereDataAdapter, filmViewModel.ACTION_VIDEO
                            + filmViewModel.SORT_DESC_DATE_PREMIERE
                            + filmViewModel.category.value.toString()
                            + filmViewModel.genreID.value.toString()
                            + filmViewModel.countryID.value.toString()
                            + filmViewModel.yearFromTo.value.toString()
                            + filmViewModel.LIMIT, filmViewModel.BYDATEPREMIER_ID, newState)
                }
            }
        })
        estimatedRecView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.d(TAG, "onScrollStateChanged: " + newState)

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    filmViewModel.estimatedItemsCount = filmViewModel.estimatedFilmLists.value!!.size
                    addNewItemToRecyclerView(estimatedDataAdapter, filmViewModel.ACTION_VIDEO
                            + filmViewModel.SORT_DESC_RATING_VOTE
                            + filmViewModel.category.value.toString()
                            + filmViewModel.genreID.value.toString()
                            + filmViewModel.countryID.value.toString()
                            + filmViewModel.yearFromTo.value.toString()
                            + filmViewModel.LIMIT, filmViewModel.ESTIMATED_ID, newState)
                }
            }
        })
    }

    private fun addNewItemToRecyclerView(adapter: DataAdapter, url: String, id: Int, newState: Int) {
        Log.d(TAG, "////////////////////////////////////////////////////////////")
        Log.d(TAG, "addNewItemToRecyclerView: newState " + newState)
        Log.d(TAG, "addNewItemToRecyclerView: position " + adapter.position)
        Log.d(TAG, "addNewItemToRecyclerView: count " + adapter.itemCount)
        if (adapter.position == (adapter.itemCount - 1)){
            filmViewModel.updateFilmList(url, id)
        }
    }

    private fun startDescriptionActivity(filmArray: ArrayList<FilmList>, position: Int) {
        var intent: Intent = Intent(application, DescriptionActivity::class.java)
        intent.putExtra("description", filmArray[position].description)
        intent.putExtra("name", filmArray[position].name)
        intent.putExtra("cover", filmArray[position].cover)
        intent.putExtra("cover_200", filmArray[position].cover_200)
        intent.putExtra("year", filmArray[position].year)
        intent.putExtra("id", filmArray[position].id)
        intent.putExtra("country", filmArray[position].country)
        intent.putExtra("director", filmArray[position].director)
        intent.putExtra("actors", filmArray[position].actors)
        intent.putExtra("genres", filmArray[position].genres)
        intent.putExtra("original_name", filmArray[position].original_name)
        //TODO array with trailers
        //needs to get ARRAY with trailers
        //intent.putExtra("trailer_urls", filmArray[position].trailer_urls)
        intent.putExtra("serial_id", filmArray[position].serial_id)
        if (filmArray[position].serial_id.equals("null", true)){
            intent.putExtra("isSerial", false)
        }else{
            intent.putExtra("isSerial", true)
        }
        intent.putExtra("serial_count_seasons", filmArray[position].serial_count_seasons)
        intent.putExtra("season_number", filmArray[position].season_number)
        intent.putExtra("serial_name", filmArray[position].serial_name)
        intent.putExtra("serial_o_name", filmArray[position].serial_o_name)
        intent.putExtra("translate", filmArray[position].translate)
        var array: FilmList = FilmList()
        array = filmArray[position]
        var bundle: Bundle = Bundle()
        bundle.putSerializable("array", array)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onDurationSelected(durationString: String) {
        filmViewModel.yearFromTo.value = durationString
        filmViewModel.getNewSortedFilList()
        setLayoutsGone()
        notifyAllDataSetChanged()
    }


    fun checkUpdates(){
        getLastAppVersion()
        filmViewModel.lastAppVersion.observe(this, Observer {
            if (updateStatus){
            if (it == 0) {
                return@Observer
            }
            if (it <= BuildConfig.VERSION_CODE) {
                return@Observer
            }
            var lastVersionNumber: String? = SettingsManager.get(applicationContext, "LastIgnoredUpdateVersion")
            if (lastVersionNumber != null) {
                var liInt: Int = lastVersionNumber.toInt()
                if (liInt >= filmViewModel.lastAppVersion.value!!)
                    return@Observer
            }
            updateApplication(it)
            }
            updateStatus = false
        })

    }


    private fun getLastAppVersion(){
        var coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            try {
                var url: URL = URL(Constants.FILE_WITH_VERSION_NUMBER)
                var inStream: BufferedReader = BufferedReader(InputStreamReader(url.openStream()))
                var str: String? = ""
                inStream.forEachLine {
                    var strNum: Int = it.indexOf("releaseVersionCode")
                    str = it.substring(strNum + ("releaseVersionCode").length).trim()
                    filmViewModel.lastAppVersion.postValue(str!!.toInt())
                }
                inStream.close()
            }catch (e: Exception){
                Log.d(TAG, "getLastAppVersion error: " + e.message)
            }

        }
    }


    private fun updateApplication(lastAppVersion: Int) {
        Log.d(TAG, "updateApplication: " + lastAppVersion)
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.alertForUpdate)
                .setCancelable(true)
                .setPositiveButton("Да", DialogInterface.OnClickListener { dialogInterface, i ->
                    Log.d(TAG, "updateApplication: click yes button")
                    var directory: File? = applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    var file = File(directory, "app-armeabi-v7a-release.apk")
                    if (file.exists()) {
                        file.delete()
                    }
                    var fileUri: Uri
                    if (Build.VERSION.SDK_INT < 24) {
                        fileUri = Uri.fromFile(file)
                        Log.d(TAG, "sdk version is < 24")
                        Log.d(TAG, "fileURi: " + fileUri.path)
                    }
                    if (Build.VERSION.SDK_INT >= 24) {
                        fileUri = FileProvider.getUriForFile(applicationContext, BuildConfig.APPLICATION_ID + ".fileprovider", file)
                        Log.d(TAG, "sdk version is >= 24")
                        Log.d(TAG, "fileURi: " + fileUri.path)
                    }
                    var request: DownloadManager.Request = DownloadManager.Request(Uri.parse(Constants.FILE_WITH_APK_UPDATE))
                            .setTitle("Обновление Кинозал")
                            .setDescription("Загрузка")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                            .setDestinationUri(Uri.fromFile(file))
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true)
                    Log.d(TAG, "file: " + file.toString())
                    var downloadManager: DownloadManager = this.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    Log.d(TAG, "create download manager")
                    downloadID = downloadManager.enqueue(request)
                    Log.d(TAG, "downloadID is " + downloadID)
                    application.registerReceiver(BroadcastRecieverOnDowloadComplete(downloadID, application), IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
                    dialogInterface.dismiss()
                })
                .setNegativeButton("Нет", DialogInterface.OnClickListener { dialogInterface, i ->
                    Log.d(TAG, "updateApplication: click no button" )

                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    SettingsManager.put(applicationContext, "LastIgnoredUpdateVersion", lastAppVersion.toString())
                })
        var alert: AlertDialog =builder.create()
        alert.show()
    }



}