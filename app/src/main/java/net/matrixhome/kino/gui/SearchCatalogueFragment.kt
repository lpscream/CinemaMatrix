package net.matrixhome.kino.gui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.matrixhome.kino.R
import net.matrixhome.kino.gui.adapters.*
import net.matrixhome.kino.model.Movies
import net.matrixhome.kino.viewmodel.SearchViewModel
import net.matrixhome.kino.viewmodel.SearchViewModleFactory

class SearchCatalogueFragment : Fragment() {

    private lateinit var searchLine: String
    private lateinit var searchViewModel: SearchViewModel
    private val TAG = "SearchCatalogueFragment_log"

    private lateinit var searchList: CustomRecyclerView
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchViewModel = ViewModelProvider(requireActivity(), SearchViewModleFactory()).get(SearchViewModel::class.java)
        searchLine = arguments?.getString("query").toString()
        searchViewModel.getSearchResult(searchLine)
        Log.d(TAG, "onCreate: searchLine is " + searchLine)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_catalogue, container, false)
        ///////
        searchList = view. findViewById(R.id.searchRV)
        searchList.setHasFixedSize(false)
        searchList.layoutManager = GridLayoutManager(requireContext()
            , 3
            , GridLayoutManager.VERTICAL
            , false)

        searchViewModel.getSearchResult().observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "onCreateView: result count "  + searchViewModel.getSearchResult().value?.size)
            Log.d(TAG, "onCreateView: " + searchViewModel.getSearchResult().value.toString())
            searchAdapter = SearchAdapter(searchViewModel.getSearchResult().value as ArrayList<Movies>)
            searchList.adapter = searchAdapter
            searchAdapter.notifyDataSetChanged()
            Log.d(TAG, "onCreateView: list is created")
        })


        return view
    }


}