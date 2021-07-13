package net.matrixhome.kino.gui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import net.matrixhome.kino.R
import net.matrixhome.kino.databinding.SearchCatalogueFragmentBinding
import net.matrixhome.kino.fragments.DescripFilmFragment
import net.matrixhome.kino.fragments.FilmCatalogueFragment
import net.matrixhome.kino.gui.adapters.*
import net.matrixhome.kino.viewmodel.SearchViewModel
import net.matrixhome.kino.viewmodel.SearchViewModleFactory

class SearchCatalogueFragment : Fragment() {

    private var _binding: SearchCatalogueFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchLine: String
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchAdapter: FilmCatalogueAdapter

    private val TAG = "SearchCatalogueFragment_log"


    companion object {
        fun newInstance() = SearchCatalogueFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        searchLine = arguments?.getString("query").toString()
        Log.d(TAG, "onCreate: searchLine is " + searchLine)
        setOnBackPressed()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")
        _binding = SearchCatalogueFragmentBinding.inflate(inflater, container, false)
        val view: View = binding.root
        searchViewModel?.getSearchResult(searchLine)
        return view
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated: ")
        initViews()
    }

    private fun startDescriptionFragment(id: String?, serial_id: String?) {
        val descripFragment: DescripFilmFragment = DescripFilmFragment()
        val bundle = Bundle()
        bundle.putString("id", id)
        bundle.putString("serial_id", serial_id)
        descripFragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .add(R.id.container, descripFragment)
            .hide(this)
            .commit()
    }

    private fun initViews(){
        binding.srchTVsrch.setText(searchLine)
        setSearchTvOnKeyListener()
        binding.searchRV.setHasFixedSize(false)
        if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.searchRV.layoutManager = androidx.recyclerview.widget.GridLayoutManager(
                requireContext(),
                6,
                androidx.recyclerview.widget.GridLayoutManager.VERTICAL,
                false
            )

        } else {
            binding.searchRV.layoutManager = GridLayoutManager(
                requireContext(), 3, GridLayoutManager.VERTICAL, false
            )
        }
        searchViewModel?.getSearchResult()?.observe(viewLifecycleOwner, Observer {
            searchAdapter =
                FilmCatalogueAdapter(requireContext(), searchViewModel?.getSearchResult()?.value)
            binding.searchRV.adapter = searchAdapter
            searchAdapter.notifyDataSetChanged()
            searchAdapter.setClickListener(FilmCatalogueAdapter.ItemClickListener { view, position ->
                startDescriptionFragment(
                    searchViewModel?.getSearchResult()?.value?.get(position)?.id,
                    searchViewModel?.getSearchResult()?.value!![position]?.serial_id
                )
            })
        })
    }

    private fun setOnBackPressed() {
        //requireActivity().onBackPressedDispatcher.addCallback(this) {
        //val fragment = FilmCatalogueFragment()
        //requireActivity().onBackPressed()
        //requireActivity().supportFragmentManager.beginTransaction().remove(this@SearchCatalogueFragment).show(fragment).commit()
        //requireActivity().supportFragmentManager.popBackStack()
        //remove()
        //requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        // }
    }

    private fun setSearchTvOnKeyListener() {
        binding.srchTVsrch.setOnKeyListener(View.OnKeyListener { view: View, i: Int, keyEvent: KeyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_UP){
                when (i) {
                    KeyEvent.KEYCODE_ENTER -> {
                        if (binding.srchTVsrch.text.toString().length > 2) {
                            searchViewModel.getSearchResult(binding.srchTVsrch.text.toString())

                            return@OnKeyListener true
                        }
                    }
                    KeyEvent.KEYCODE_DPAD_CENTER -> {
                        val imm: InputMethodManager =
                            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(binding.srchTVsrch, InputMethodManager.SHOW_IMPLICIT)
                    }
                }
            }
            false
        })
    }

}