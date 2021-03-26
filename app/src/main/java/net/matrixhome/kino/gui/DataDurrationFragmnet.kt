package net.matrixhome.kino.gui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import net.matrixhome.kino.R
import net.matrixhome.kino.data.Years

class DataDurrationFragmnet : DialogFragment() {

    private var year: Years = Years()
    private var yearIndexFrom: Int = 0
    private var yearIndexTo: Int = year.years.size - 1
    private val TAG = "DataDurrationFragmnet_log"
    var choosenDuration: String = String()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_date_picker, container)
        return inflater.inflate(R.layout.fragment_date_picker, container)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val yearFrom: TextView = view.findViewById(R.id.currentYearFrom)
        val yearTo: TextView = view.findViewById(R.id.currentYearTo)

        val btnOK: Button = view.findViewById(R.id.btnOk)
        btnOK.setOnClickListener(View.OnClickListener {
            val durationString: String = getString()
            val listener = activity as OnSelectedDurationListener
            listener.onDurationSelected(durationString)
            dismiss()
        })

        val btnYearFromDown: ImageButton = view.findViewById(R.id.btnDownYearFrom)
        btnYearFromDown.setOnClickListener(View.OnClickListener {
            if (yearIndexFrom > 0 && yearIndexFrom <= year.years.size) {
                yearIndexFrom = yearIndexFrom - 1
                yearFrom.text = year.years.get(yearIndexFrom)
            }
        })

        val btnYearFromUp: ImageButton = view.findViewById(R.id.btnUpYearFrom)
        btnYearFromUp.setOnClickListener(View.OnClickListener {
            if (yearIndexFrom >= 0 && yearIndexFrom < year.years.size - 1) {
                yearIndexFrom = yearIndexFrom + 1
                yearFrom.text = year.years.get(yearIndexFrom)
            }
        })

        val btnYearToDown: ImageButton = view.findViewById(R.id.btnDownYearTo)
        btnYearToDown.setOnClickListener(View.OnClickListener {
            if (yearIndexTo > 0 && yearIndexTo <= year.years.size) {
                yearIndexTo = yearIndexTo - 1
                yearTo.text = year.years.get(yearIndexTo)
            }
        })

        val btnYearToUp: ImageButton = view.findViewById(R.id.btnUpYearTo)
        btnYearToUp.setOnClickListener(View.OnClickListener {
            if (yearIndexTo >= 0 && yearIndexTo < year.years.size - 1) {
                yearIndexTo = yearIndexTo + 1
                yearTo.text = year.years.get(yearIndexTo)
            }
        })
    }


    fun getString(): String {
        val durationString: String = "&year[]=" + year.years.get(yearIndexFrom) + "-" + year.years.get(yearIndexTo)
        return durationString
    }

    interface OnSelectedDurationListener {
        fun onDurationSelected(durationString: String)
    }
}