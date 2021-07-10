package net.matrixhome.kino.gui.adapters

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.matrixhome.kino.R
import net.matrixhome.kino.gui.RoundedCornersTransformation
import net.matrixhome.kino.model.Movies

class SearchAdapter(private val list: ArrayList<Movies>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private val TAG = "SearchAdapter_log"


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "onCreateViewHolder: ")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.listview, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get()
            .load(list.get(position).cover_200)
            .transform(RoundedCornersTransformation(30,0))
            .resize(350, 500)
            .centerCrop()
            .into(holder.filmCover)
        holder.filmName?.setText(list.get(position).name)
        holder.ageTV?.setText(list.get(position).age)
        holder.ratingTV?.setText(list.get(position).rating)
        holder.yearCountry?.setText(list.get(position).year + ", " + list.get(position).country)
        holder.itemView?.focusable = View.FOCUSABLE
        holder.itemView.setOnFocusChangeListener { view, b ->
            if (b){
                view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start()
                view.elevation = 20f
            }else{
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                view.elevation = 0f
            }

        }
    }

    override fun getItemCount(): Int {
        return list?.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var filmName: TextView? = null
        var filmCover: ImageView? = null
        var ageTV: TextView? = null
        var ratingTV: TextView? = null
        var yearCountry: TextView? = null

        init {
            filmName = itemView.findViewById(R.id.filmName)
            filmCover = itemView.findViewById(R.id.cover)
            ageTV = itemView.findViewById(R.id.ageTV)
            ratingTV = itemView.findViewById(R.id.ratingTV)
            yearCountry = itemView.findViewById(R.id.yearCountryTV)
        }



    }
}