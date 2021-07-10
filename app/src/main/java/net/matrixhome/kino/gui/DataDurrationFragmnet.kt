package net.matrixhome.kino.gui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import com.bekawestberg.loopinglayout.library.LoopingLayoutManager
import net.matrixhome.kino.R
import net.matrixhome.kino.data.Years

class DataDurrationFragmnet : DialogFragment() {

    private var year: Years = Years()
    private var yearIndexFrom: Int = 0
    private var yearIndexTo: Int = year.years.size - 1
    private val TAG = "DataDurrationFragmnet_log"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_date_picker, container)
        return inflater.inflate(R.layout.fragment_date_picker, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val yearFrom: RecyclerView = view.findViewById(R.id.dateFromRV)
        val yearTo: RecyclerView = view.findViewById(R.id.dateToRV)


        val dateFromDescriptionAdapter = DescriptionAdapter(activity, year.years)
        val dateToDescriptionAdapter = DescriptionAdapter(activity, year.years)



        yearFrom.setHasFixedSize(false)
        yearTo.setHasFixedSize(false)



        yearFrom.layoutManager = LoopingLayoutManager(requireActivity(), LoopingLayoutManager.VERTICAL, false)
        yearTo.layoutManager = LoopingLayoutManager(requireActivity(), LoopingLayoutManager.VERTICAL,false)

        yearFrom.adapter = dateFromDescriptionAdapter
        yearTo.adapter = dateToDescriptionAdapter


        yearFrom.requestFocus()
        yearFrom.scrollToPosition(year.years.size-1)
        yearTo.scrollToPosition(year.years.size-1)


        dateFromDescriptionAdapter.setOnmClickListener { view, position ->
            yearIndexFrom = position
            yearTo.requestFocus()
        }

        dateToDescriptionAdapter.setOnmClickListener { view, position ->
            yearIndexTo = position
            val durationString: String = getString()
            setFragmentResult("DATE_PERIOD", bundleOf("date_period" to durationString))
            dismiss()
        }


    }


    fun getString(): String {
        val durationString: String = year.years.get(yearIndexFrom) + "-" + year.years.get(yearIndexTo)
        return durationString
    }
}