package net.matrixhome.kino.fragments

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.matrixhome.kino.R
import net.matrixhome.kino.data.*
import net.matrixhome.kino.gui.*
import net.matrixhome.kino.gui.CustomRecyclerView
import net.matrixhome.kino.gui.DataDurrationFragmnet
import net.matrixhome.kino.gui.SpinnerAdapter
import net.matrixhome.kino.gui.adapters.FilmCatalogueAdapter
import net.matrixhome.kino.model.Movies
import net.matrixhome.kino.viewmodel.FilmCatalougeModel
import java.util.ArrayList


class FilmCatalogueFragment : Fragment(){

    private val TAG: String = "FilmCatalogueFragment_log"

    val SEARCHRESPONSE: String = "searchResponse"

    private var sortBtnState = false

    private lateinit var settingsManager: SettingsManager
    private lateinit var years: Years
    private lateinit var genreSpinner: Spinner
    private lateinit var countrySpinner: Spinner

    private lateinit var yearDurationBtn: ImageButton


    private var genreSpinnerFlag: Boolean = false
    private var countrySpinnerFlag: Boolean = false

    private lateinit var allFilmsBtn: Button
    private lateinit var filmsBtn: Button
    private lateinit var animBtn: Button
    private lateinit var serialBtn: Button
    private lateinit var animSerialBtn: Button
    private lateinit var tvShowBtn: Button
    private lateinit var sortVarButton: ImageButton

    private lateinit var linearLayoutLastAdded: LinearLayout
    private lateinit var linearLayoutByPopularity: LinearLayout
    private lateinit var linearLayoutByRating: LinearLayout
    private lateinit var linearLayoutByDatePremiere: LinearLayout

    private lateinit var lastAddedRecView: CustomRecyclerView
    private lateinit var byPopularityFilmRecView: CustomRecyclerView
    private lateinit var byRatingRecView: CustomRecyclerView
    private lateinit var byDatePremiereRecView: CustomRecyclerView


    private lateinit var lastAddedFilmsDataAdapter: FilmCatalogueAdapter
    private lateinit var byPopularityFilmDataAdapter: FilmCatalogueAdapter
    private lateinit var byRatingDataAdapter: FilmCatalogueAdapter
    private lateinit var byDatePremiereDataAdapter: FilmCatalogueAdapter

    private lateinit var superLayout: ConstraintLayout
    private lateinit var searchTV: EditText
    private lateinit var datePickerFragment: DataDurrationFragmnet

    private lateinit var filmViewModel: FilmCatalougeModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filmViewModel = ViewModelProvider(this).get(FilmCatalougeModel::class.java)


    }

    private fun initVariables(view: View) {
        linearLayoutLastAdded = view.findViewById(R.id.linearLayoutLastAdded)
        linearLayoutByPopularity = view.findViewById(R.id.linearLayoutByPopularity)
        linearLayoutByRating = view.findViewById(R.id.linearLayoutByRating)
        linearLayoutByDatePremiere = view.findViewById(R.id.linearLayoutByDatePremiere)

        superLayout = view.findViewById(R.id.superLayout)
        yearDurationBtn = view.findViewById(R.id.yearDurationBtn)
        yearDurationBtn.setOnClickListener {

            val requestKey = "DATE_PERIOD"
            setFragmentResultListener(requestKey) { requestKey, bundle ->
                filmViewModel.yearFromTo.value = bundle.getString("date_period")
                filmViewModel.getNewSortedFilList()
                setLayoutsGone()
                notifyAllDataSetChanged()
            }
            datePickerFragment = DataDurrationFragmnet()
            datePickerFragment.show(requireActivity().supportFragmentManager, "datePickerFragment")
        }

        allFilmsBtn = view.findViewById(R.id.allFilmsBtn)
        filmsBtn = view.findViewById(R.id.filmsBtn)
        animBtn = view.findViewById(R.id.animBtn)
        serialBtn = view.findViewById(R.id.serialBtn)
        animSerialBtn = view.findViewById(R.id.animSerialBtn)
        tvShowBtn = view.findViewById(R.id.tvShowBtn)
        sortVarButton = view.findViewById(R.id.sort_var_button)

        years = Years()

        lastAddedRecView = view.findViewById(R.id.lastAddedRecView)
        byPopularityFilmRecView = view.findViewById(R.id.byPopularityFilmRecView)
        byRatingRecView = view.findViewById(R.id.byRatingRecView)
        byDatePremiereRecView = view.findViewById(R.id.byDatePremiereRecView)

        settingsManager = SettingsManager()

        lastAddedRecView.setHasFixedSize(false)
        byPopularityFilmRecView.setHasFixedSize(false)
        byRatingRecView.setHasFixedSize(false)
        byDatePremiereRecView.setHasFixedSize(false)

        searchTV = view.findViewById(R.id.searchTV)

        countrySpinner = view.findViewById(R.id.spinnerCountry)
        genreSpinner = view.findViewById(R.id.spinnerGenre)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_main, container, false)
        initVariables(view)
        setLayoutsGone()
        setLinearLayoutsToRecyclerView()
        setOnClickListeners()
        //setOnScrollListeners()//
        setObservers()
        initCountrySpinner()
        setSearchTvOnKeyListener()
        //setOnIterceptFocusSearch()  //for all RV
        //startParentRecyclerView()//




        return view
    }


    private fun setLayoutsGone() {
        linearLayoutLastAdded.visibility = View.GONE
        linearLayoutByPopularity.visibility = View.GONE
        linearLayoutByRating.visibility = View.GONE
        linearLayoutByDatePremiere.visibility = View.GONE
    }

    private fun setSearchTvOnKeyListener() {
        searchTV.setOnKeyListener(View.OnKeyListener { view: View, i: Int, keyEvent: KeyEvent ->
            when (i) {
                KeyEvent.KEYCODE_ENTER -> {
                    if (searchTV.text.toString().length > 2) {
                        startSearchFragment()
                        //startSearchActivity(searchTV.text.toString())
                        return@OnKeyListener true
                    }
                }
                KeyEvent.KEYCODE_DPAD_CENTER -> {
                    val imm: InputMethodManager =
                        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(searchTV, InputMethodManager.SHOW_IMPLICIT)
                }
            }
            false
        })
    }


    private fun startSearchActivity(response: String) {
        val intent = Intent(requireContext(), SearchActivity::class.java)
        intent.putExtra(SEARCHRESPONSE, response)
        startActivity(intent)
    }


    private fun startSearchFragment() {
        val searchFragmnet: SearchCatalogueFragment = SearchCatalogueFragment()
        var bundle = Bundle()
        bundle.putString("query", searchTV.text.toString())
        searchFragmnet.arguments = bundle
        //searchFragmnet.arguments?.putString("query", searchTV.text.toString())
        requireActivity().supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.container, searchFragmnet)
            .commit()
    }


    fun startDescriptionFragment(id: String?, serial_id: String?){
        val descripFragment: DescripFilmFragment = DescripFilmFragment()
        val bundle = Bundle()
        bundle.putString("id", id)
        bundle.putString("serial_id", serial_id)
        descripFragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .add(R.id.container, descripFragment)
            .hide(this)
            //.replace(R.id.other_container, descripFragment)
            .commit()
    }


    private fun setObservers() {
        filmViewModel.getByPopList().observe(viewLifecycleOwner, Observer {
            if (filmViewModel.updateListState2) {
                byPopularityFilmDataAdapter =
                    FilmCatalogueAdapter(
                        requireContext(),
                        it,
                        filmViewModel,
                        filmViewModel.VIEWS_MONTH
                    )
                byPopularityFilmRecView.adapter = byPopularityFilmDataAdapter
                byPopularityFilmDataAdapter.setClickListener(FilmCatalogueAdapter.ItemClickListener { view, position ->
                    //startDescriptionActivity(filmViewModel.getByPopList().value!!, position)
                    startDescriptionFragment(filmViewModel.getByPopList().value?.get(position)?.id, filmViewModel.getByPopList().value?.get(position)?.serial_id)
                })
                setOnIterceptFocusSearchRecyclerView(
                    byPopularityFilmRecView,
                    byPopularityFilmDataAdapter
                )
                filmViewModel.updateListState2 = false
                linearLayoutByPopularity.visibility = View.VISIBLE
                byPopularityFilmRecView.requestFocus()
            }
            byPopularityFilmDataAdapter.notifyItemRangeChanged(
                filmViewModel.byPopularityItemsCount,
                filmViewModel.getByPopList().value!!.size
            )
        })
        /////////////////////////////////////////////////////////////////////////////////////////
        filmViewModel.getLastAddedList().observe(viewLifecycleOwner, Observer {
            if (filmViewModel.updateListState1) {
                lastAddedFilmsDataAdapter =
                    FilmCatalogueAdapter(
                        requireContext(),
                        it,
                        filmViewModel,
                        filmViewModel.ADDED
                    )
                lastAddedRecView.adapter = lastAddedFilmsDataAdapter
                lastAddedFilmsDataAdapter.setClickListener(FilmCatalogueAdapter.ItemClickListener { view, position ->
                    //startDescriptionActivity(filmViewModel.getLastAddedList().value!!, position)
                    startDescriptionFragment(filmViewModel.getLastAddedList().value?.get(position)?.id, filmViewModel.getLastAddedList().value?.get(position)?.serial_id)

                })
                setOnIterceptFocusSearchRecyclerView(lastAddedRecView, lastAddedFilmsDataAdapter)
                linearLayoutLastAdded.visibility = View.VISIBLE
            }
            //TODO lastAddedFilmsDataAdapter.notifyItemRangeChanged(filmViewModel.lastAddedFilmList.value!!.size - filmViewModel.lastAddedItemsCount, filmViewModel.lastAddedFilmList.value!!.size)
            //lastAddedFilmsDataAdapter.notifyItemRangeChanged(filmViewModel.lastAddedFilmList.value!!.size - filmViewModel.lastAddedItemsCount, filmViewModel.lastAddedFilmList.value!!.size)
            lastAddedFilmsDataAdapter.notifyItemRangeChanged(
                filmViewModel.lastAddedItemsCount,
                filmViewModel.getLastAddedList().value!!.size - filmViewModel.lastAddedItemsCount
            )
            filmViewModel.updateListState1 = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                searchTV.focusable = View.FOCUSABLE
            }
        })
        //////////////////////////////////////////////////////////////////////////////////////////
        filmViewModel.getByRatingList().observe(viewLifecycleOwner, Observer {
            linearLayoutByRating.visibility = View.VISIBLE
            if (filmViewModel.updateListState4) {
                byRatingDataAdapter =
                    FilmCatalogueAdapter(
                        requireContext(),
                        filmViewModel.getByRatingList().value!!,
                        filmViewModel,
                        filmViewModel.RATING
                    )
                byRatingRecView.adapter = byRatingDataAdapter
                byRatingDataAdapter.setClickListener(FilmCatalogueAdapter.ItemClickListener { view, position ->
                    //startDescriptionActivity(filmViewModel.getByRatingList().value!!, position)
                    startDescriptionFragment(filmViewModel.getByRatingList().value?.get(position)?.id, filmViewModel.getByRatingList().value?.get(position)?.serial_id)

                })
                setOnIterceptFocusSearchRecyclerView(byRatingRecView, byRatingDataAdapter)
            }
            byRatingDataAdapter.notifyItemRangeChanged(
                filmViewModel.byRatingItemsCount,
                filmViewModel.getByRatingList().value!!.size
            )
            filmViewModel.updateListState4 = false
        })
////////////////////////////////////////////////////////////////////////////////////////////////
        filmViewModel.getByDatePremiereList().observe(viewLifecycleOwner, Observer {
            linearLayoutByDatePremiere.visibility = View.VISIBLE
            if (filmViewModel.updateListState6) {
                byDatePremiereDataAdapter =
                    FilmCatalogueAdapter(
                        requireContext(),
                        filmViewModel.getByDatePremiereList().value!!,
                        filmViewModel,
                        filmViewModel.DATE_PREMIERE
                    )
                byDatePremiereRecView.adapter = byDatePremiereDataAdapter
                byDatePremiereDataAdapter.setClickListener(FilmCatalogueAdapter.ItemClickListener { view, position ->
                    //startDescriptionActivity(filmViewModel.getByDatePremiereList().value!!, position)
                    startDescriptionFragment(filmViewModel.getByDatePremiereList().value?.get(position)?.id, filmViewModel.getByDatePremiereList().value?.get(position)?.serial_id)

                })
                setOnIterceptFocusSearchRecyclerView(
                    byDatePremiereRecView,
                    byDatePremiereDataAdapter
                )

            }
            byDatePremiereDataAdapter.notifyItemRangeChanged(
                filmViewModel.byDateItemsCount,
                filmViewModel.getByDatePremiereList().value!!.size
            )
            filmViewModel.updateListState6 = false
        })

        filmViewModel.genreArrayList.observe(requireActivity(), Observer {
            initGenreSpinner()
        })
    }

    private fun setLinearLayoutsToRecyclerView() {
        lastAddedRecView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        byPopularityFilmRecView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        byRatingRecView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        byDatePremiereRecView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }


    private fun notifyAllDataSetChanged() {
        lastAddedFilmsDataAdapter.notifyDataSetChanged()
        byPopularityFilmDataAdapter.notifyDataSetChanged()
        byRatingDataAdapter.notifyDataSetChanged()
        byDatePremiereDataAdapter.notifyDataSetChanged()
    }


    private fun setOnClickListeners() {

        allFilmsBtn.setOnClickListener {
            filmViewModel.genreID.value = listOf()
            filmViewModel.countryID.value = ""
            filmViewModel.yearFromTo.value = ""
            filmViewModel.category.value = listOf()
            //need to reset film list
            filmViewModel.getNewSortedFilList()
            setLayoutsGone()
            notifyAllDataSetChanged()
        }
        filmsBtn.setOnClickListener {
            filmViewModel.genreID.value = listOf()
            filmViewModel.countryID.value = ""
            filmViewModel.yearFromTo.value = ""
            filmViewModel.category.value = listOf()
            filmViewModel.category.value = listOf("our-film", "world-film")
            //need to reset film list
            filmViewModel.getNewSortedFilList()
            setLayoutsGone()
            notifyAllDataSetChanged()
        }

        animBtn.setOnClickListener {
            filmViewModel.genreID.value = listOf()
            filmViewModel.countryID.value = ""
            filmViewModel.yearFromTo.value = ""
            filmViewModel.category.value = listOf()
            filmViewModel.category.value = listOf("animation")
            //need to reset film list
            filmViewModel.getNewSortedFilList()
            setLayoutsGone()
            notifyAllDataSetChanged()
        }

        serialBtn.setOnClickListener {
            filmViewModel.genreID.value = listOf()
            filmViewModel.countryID.value = ""
            filmViewModel.yearFromTo.value = ""
            filmViewModel.category.value = listOf()
            filmViewModel.category.value = listOf("world-serial", "owr-serial")
            //need to reset film list
            filmViewModel.getNewSortedFilList()
            setLayoutsGone()
            notifyAllDataSetChanged()
        }

        animSerialBtn.setOnClickListener {
            filmViewModel.genreID.value = listOf()
            filmViewModel.countryID.value = ""
            filmViewModel.yearFromTo.value = ""
            filmViewModel.category.value = listOf()
            filmViewModel.category.value = listOf("animation-serial")
            //TODO need to reset film list
            filmViewModel.getNewSortedFilList()
            setLayoutsGone()
            notifyAllDataSetChanged()
        }

        tvShowBtn.setOnClickListener {
            filmViewModel.genreID.value = listOf()
            filmViewModel.countryID.value = ""
            filmViewModel.yearFromTo.value = ""
            filmViewModel.category.value = listOf()
            filmViewModel.category.value = listOf("tv-shows")
            //TODO need to reset film list
            filmViewModel.getNewSortedFilList()
            setLayoutsGone()
            notifyAllDataSetChanged()
        }

        sortVarButton.setOnClickListener {
            if (sortBtnState) {
                sortVarButton.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.sort_desc_variant_button_selector
                )
                //TODO сортивка по возрастанию
                filmViewModel.getNewSortedFilList()
                setLayoutsGone()
                notifyAllDataSetChanged()
                sortBtnState = false
            } else {
                sortVarButton.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.sort_variant_button_selector
                )
                //TODO сортировка по убыванию
                filmViewModel.getNewSortedFilList()
                setLayoutsGone()
                notifyAllDataSetChanged()
                sortBtnState = true
            }
        }
    }

    private fun startDescriptionActivity(filmArray: ArrayList<Movies>, position: Int) {
        val intent = Intent(requireContext(), DescriptionActivity::class.java)
        intent.putExtra("description", filmArray[position].description)
        intent.putExtra("name", filmArray[position].name)
        intent.putExtra("cover", filmArray[position].cover)
        intent.putExtra("cover_200", filmArray[position].cover_200)
        intent.putExtra("year", filmArray[position].year)
        intent.putExtra("id", filmArray[position].id)
        Log.d(TAG, "startDescriptionActivity: " + filmArray[position].id)
        intent.putExtra("country", filmArray[position].country)
        intent.putExtra("director", filmArray[position].director)
        intent.putExtra("actors", filmArray[position].actors)
        intent.putExtra("genres", filmArray[position].genres)
        intent.putExtra("original_name", filmArray[position].original_name)
        //TODO array with trailers
        //needs to get ARRAY with trailers
        //intent.putExtra("trailer_urls", filmArray[position].trailer_urls)
        intent.putExtra("serial_id", filmArray[position].serial_id)
        if (filmArray[position].serial_id.equals("null", true)) {
            intent.putExtra("isSerial", false)
        } else {
            intent.putExtra("isSerial", true)
        }
        intent.putExtra("serial_count_seasons", filmArray[position].serial_count_seasons)
        intent.putExtra("season_number", filmArray[position].season_number)
        intent.putExtra("serial_name", filmArray[position].serial_name)
        intent.putExtra("serial_o_name", filmArray[position].serial_o_name)
        intent.putExtra("translate", filmArray[position].translate)
        val array: Movies
        array = filmArray[position]
        val bundle = Bundle()
        bundle.putSerializable("array", array)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        filmViewModel.updateListState1 = true
        filmViewModel.updateListState2 = true
        filmViewModel.updateListState4 = true
        filmViewModel.updateListState6 = true
    }

    private fun setOnIterceptFocusSearchRecyclerView(
        recyclerView: RecyclerView,
        adapter: FilmCatalogueAdapter
    ) {
        recyclerView.layoutManager =
            object : LinearLayoutManager(requireContext(), HORIZONTAL, false) {
                override fun onInterceptFocusSearch(focused: View, direction: Int): View? {
                    if (direction == View.FOCUS_RIGHT
                        || direction == View.FOCUS_LEFT
                        || direction == View.FOCUS_DOWN
                        || direction == View.FOCUS_UP
                    ) {
                        val pos = adapter.position
                        if (pos == adapter.itemCount - 1)
                            return focused
                    }
                    return super.onInterceptFocusSearch(focused, direction)
                }
            }
    }

    private fun initGenreSpinner() {
        val genre = resources.getStringArray(R.array.genres)
        val adapter =
            SpinnerAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, genre)
        genreSpinner.adapter = adapter
        genreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val line: String
                line = genre[p2].toString()
                var arrayList = ArrayList<String>()
                for (j in 0..(filmViewModel.genreArrayList.value!!.size - 1)) {
                    if (filmViewModel.genreArrayList.value!!.get(j).title.equals(line)) {
                        arrayList.add(filmViewModel.genreArrayList.value!!.get(j).id)
                    }
                }
                filmViewModel.genreID.value = listOf()
                filmViewModel.genreID.value = arrayList.toList()
                if (genreSpinnerFlag) {
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

    private fun initCountrySpinner() {
        val countries = resources.getStringArray(R.array.countries)
        val spinnerAdapter = SpinnerAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item, countries
        )
        countrySpinner.adapter = spinnerAdapter
        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (p2) {
                    0 -> filmViewModel.countryID.value = ""
                    1 -> filmViewModel.countryID.value = "our"
                    2 -> filmViewModel.countryID.value = "foreign"
                }
                if (countrySpinnerFlag) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        filmViewModel.updateListState1 = true
        filmViewModel.updateListState2 = true
        filmViewModel.updateListState4 = true
        filmViewModel.updateListState6 = true
    }

    private fun onSortMovies() {

    }


    /*private fun setOnScrollListeners() {
      lastAddedRecView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
          override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
              super.onScrollStateChanged(recyclerView, newState)
              Log.d(TAG, "onScrollStateChanged: " + newState)
              if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                  filmViewModel.lastAddedItemsCount = filmViewModel.lastAddedFilmList.value!!.size
                  addNewItemToRecyclerView(lastAddedFilmsDataAdapter,
                          filmViewModel.ACTION_VIDEO
                                  + filmViewModel.sort_variant_added
                                  + filmViewModel.category.value.toString()
                                  + filmViewModel.genreID.value.toString()
                                  + filmViewModel.countryID.value.toString()
                                  + filmViewModel.yearFromTo.value.toString()
                                  + filmViewModel.LIMIT, filmViewModel.LASTADDED_ID, newState)
              }
          }
      })
      byPopularityFilmRecView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
          override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
              super.onScrollStateChanged(recyclerView, newState)
              Log.d(TAG, "onScrollStateChanged: " + newState)

              if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                  filmViewModel.byPopularityItemsCount = filmViewModel.byPopularityFilmFilmLists.value!!.size
                  addNewItemToRecyclerView(byPopularityFilmDataAdapter,
                          filmViewModel.ACTION_VIDEO
                                  + filmViewModel.sort_variant_views_month
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
                          + filmViewModel.sort_variant_views
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
                          + filmViewModel.sort_variant_rating
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
                          + filmViewModel.sort_variant_name
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
                          + filmViewModel.sort_variant_date_premiere
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
                          + filmViewModel.sort_variant_rating_vote
                          + filmViewModel.category.value.toString()
                          + filmViewModel.genreID.value.toString()
                          + filmViewModel.countryID.value.toString()
                          + filmViewModel.yearFromTo.value.toString()
                          + filmViewModel.LIMIT, filmViewModel.ESTIMATED_ID, newState)
              }
          }
      })
  }

   */


    /*fun createRoundedCorners(view: View) {
       val shapeAppearanceModel = ShapeAppearanceModel()
           .toBuilder()
           .setAllCorners(CornerFamily.ROUNDED, android.R.attr.radius.toFloat())
           .build()
       val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
       shapeDrawable.setFillColor(
           ContextCompat.getColorStateList(
               this,
               R.color.colorGrey
           )
       )
       shapeDrawable.setStroke(
           2.0f,
           ContextCompat.getColor(this, R.color.colorGrey)
       )
       ViewCompat.setBackground(view, shapeDrawable)
   }*/

    /*private fun setOnScrollListeners() {
      lastAddedRecView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
          override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
              super.onScrollStateChanged(recyclerView, newState)
              Log.d(TAG, "onScrollStateChanged: " + newState)
              if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                  filmViewModel.lastAddedItemsCount = filmViewModel.lastAddedFilmList.value!!.size
                  addNewItemToRecyclerView(lastAddedFilmsDataAdapter,
                          filmViewModel.ACTION_VIDEO
                                  + filmViewModel.sort_variant_added
                                  + filmViewModel.category.value.toString()
                                  + filmViewModel.genreID.value.toString()
                                  + filmViewModel.countryID.value.toString()
                                  + filmViewModel.yearFromTo.value.toString()
                                  + filmViewModel.LIMIT, filmViewModel.LASTADDED_ID, newState)
              }
          }
      })
      byPopularityFilmRecView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
          override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
              super.onScrollStateChanged(recyclerView, newState)
              Log.d(TAG, "onScrollStateChanged: " + newState)

              if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                  filmViewModel.byPopularityItemsCount = filmViewModel.byPopularityFilmFilmLists.value!!.size
                  addNewItemToRecyclerView(byPopularityFilmDataAdapter,
                          filmViewModel.ACTION_VIDEO
                                  + filmViewModel.sort_variant_views_month
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
                          + filmViewModel.sort_variant_views
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
                          + filmViewModel.sort_variant_rating
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
                          + filmViewModel.sort_variant_name
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
                          + filmViewModel.sort_variant_date_premiere
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
                          + filmViewModel.sort_variant_rating_vote
                          + filmViewModel.category.value.toString()
                          + filmViewModel.genreID.value.toString()
                          + filmViewModel.countryID.value.toString()
                          + filmViewModel.yearFromTo.value.toString()
                          + filmViewModel.LIMIT, filmViewModel.ESTIMATED_ID, newState)
              }
          }
      })
  }

   */
}