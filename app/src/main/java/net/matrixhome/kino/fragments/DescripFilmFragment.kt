package net.matrixhome.kino.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.squareup.picasso.Picasso
import net.matrixhome.kino.R
import net.matrixhome.kino.databinding.DescripFilmFragmentBinding
import net.matrixhome.kino.gui.DescriptionAdapter
import net.matrixhome.kino.gui.RoundedCornersTransformation
import java.util.*
import kotlin.collections.ArrayList

class DescripFilmFragment : Fragment() {

    private var _binding: DescripFilmFragmentBinding? = null
    private val binding get() = _binding!!

    private val TAG = "DescriptionFilmFragment_log"

    //private lateinit var nameTextView: TextView
    private lateinit var cover: ImageView
    private lateinit var ratingDiscTextView: TextView
    private lateinit var ageDiscTextView: TextView
    private lateinit var filmYearDescTextView: TextView
    private lateinit var filmCountryDescTextView: TextView
    private lateinit var filmGenreDescTextView: TextView
    private lateinit var filmDirectorDescTextView: TextView
    private lateinit var filmActorDescTextView: TextView
    private lateinit var translateTextView: TextView
    private lateinit var isHdTextView: TextView
    private lateinit var isHdDesc: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var sesonSelectLayout: LinearLayout
    private lateinit var translateDiscTV: TextView
    private lateinit var container: ConstraintLayout
    private lateinit var playBtn: Button
    private lateinit var seasonChssrRecyclerView: RecyclerView
    private lateinit var episodeChssrRecyclerView: RecyclerView
    private lateinit var seasonAdapter: DescriptionAdapter
    private lateinit var episodeAdapter: DescriptionAdapter
    private lateinit var stringArray: ArrayList<String>
    private lateinit var arrayList: ArrayList<Int>
    private lateinit var serial_id: String
    private lateinit var id: String

    companion object {
        fun newInstance() = DescripFilmFragment()
    }

    private lateinit var viewModel: DescriptionFilmViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DescripFilmFragmentBinding.inflate(inflater, container, false)

        val view: View = binding.root
        binding.containerDesc.visibility = View.GONE
        viewModel = ViewModelProvider(this).get(DescriptionFilmViewModel::class.java)
        initReppository()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getFilmRepository().observe(viewLifecycleOwner, Observer {
            if (arguments?.getString("serial_id").toString() == "null"){
                setFilmContent()
            }
        })


        viewModel.getSerialRepository().observe(viewLifecycleOwner, Observer {
            if (arguments?.getString("serial_id").toString() != "null"){
                setSerialContent()
            }
        })
    }

    fun initReppository(){
        if (arguments?.getString("serial_id").toString() != "null"){
            viewModel.getRepositoryBySerialID(arguments?.getString("serial_id").toString())
        } else{
            viewModel.getRepositoryByID(arguments?.getString("id").toString())
        }
    }

    fun setSerialContent(){
        binding.playVideoBtn.visibility = View.GONE
        binding.seasonChooserRV.setHasFixedSize(false)
        binding.seasonChooserRV.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
        arrayList = arrayListOf()
        for (i in viewModel.getSerialRepository().value?.indices!!){
            arrayList.add(viewModel.getSerialRepository().value!![i].season_number.toInt())
        }
        Collections.sort(arrayList)
        stringArray = arrayListOf()
        for (i in arrayList.indices){
            stringArray.add(arrayList[i].toString())
        }
        seasonAdapter = DescriptionAdapter(requireContext(), stringArray)
        binding.seasonChooserRV.adapter = seasonAdapter
        binding.containerDesc.visibility = View.VISIBLE

        seasonAdapter.setOnmClickListener { view, position ->
            fillContentByPosition(position)
        }
        fillContentByPosition(0)
    }

    fun fillContentByPosition(position: Int){
        Log.d(TAG, "fillContentByPosition: " + position)
        for (i in viewModel.getSerialRepository().value!!.indices){
            if (viewModel.getSerialRepository().value!![i].season_number == stringArray[position]){
                serial_id = viewModel.getSerialRepository().value!![i].serial_id
                id = viewModel.getSerialRepository().value!![i].id
                binding.filmNameDescFragment.text = viewModel.getSerialRepository().value!![i].name + " (" + viewModel.getSerialRepository().value!![i].serial_o_name + ")"
                binding.filmYearDescFragment.text = viewModel.getSerialRepository().value!![i].year
                binding.filmCountryDescFragment.text = viewModel.getSerialRepository().value!![i].country
                binding.filmGenreDescFragment.text = viewModel.getSerialRepository().value!![i].genres
                binding.filmDirectorDescFragment.text = viewModel.getSerialRepository().value!![i].director
                binding.filmActorsDesActivity.text = viewModel.getSerialRepository().value!![i].actors
                binding.translateTV.text = viewModel.getSerialRepository().value!![i].translate
                if (viewModel.getSerialRepository().value!![i].hd == 1) {
                    binding.isHDDiscrp.visibility = View.VISIBLE
                    binding.isHD.text = "HD"
                }
                else {
                    binding.isHDDiscrp.visibility = View.GONE
                    binding.isHD.visibility = View.GONE
                }
                if (viewModel.getSerialRepository().value!![i].translate != ""){
                    binding.translateTV.visibility = View.VISIBLE
                    binding.translateDscrTV.visibility = View.VISIBLE
                    binding.translateTV.text = viewModel.getSerialRepository().value!![i].translate
                }else{
                    binding.translateTV.visibility = View.GONE
                    binding.translateDscrTV.visibility = View.GONE
                }
                binding.descriptionTV.text = viewModel.getSerialRepository().value!![i].description
                Picasso.get()
                    .load(viewModel.getSerialRepository().value!![i].cover_200)
                    .transform(RoundedCornersTransformation(30,0))
                    .fit()
                    .into(binding.imageViewDescFragmnet)
                binding.episodeChooserRV.setHasFixedSize(false)
                binding.episodeChooserRV.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
                episodeAdapter = DescriptionAdapter(requireContext()
                    , viewModel.getSerialRepository().value!![i].series)
                binding.episodeChooserRV.adapter = episodeAdapter
                episodeAdapter.notifyDataSetChanged()
                episodeAdapter.setOnmClickListener { view, position ->
                    val videoPlayerFragment: VideoPlayerFragment = VideoPlayerFragment()
                    val bundle = Bundle()
                    bundle.putString("episode_number", (position + 1).toString())
                    bundle.putString("serial_id", serial_id)
                    bundle.putString("id", id)
                    videoPlayerFragment.arguments = bundle
                    requireActivity().supportFragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .add(R.id.container, videoPlayerFragment)
                        .hide(this)
                        .commit()
                }
            }
        }
    }

    fun setFilmContent(){
        binding.filmNameDescFragment.text = viewModel.getFilmRepository().value?.name+ " (" + viewModel.getFilmRepository().value?.original_name + ")"
        binding.filmYearDescFragment.text = viewModel.getFilmRepository().value?.year
        binding.filmCountryDescFragment.text = viewModel.getFilmRepository().value?.country
        binding.filmGenreDescFragment.text = viewModel.getFilmRepository().value?.genres
        binding.filmDirectorDescFragment.text = viewModel.getFilmRepository().value?.director
        binding.filmActorsDesActivity.text = viewModel.getFilmRepository().value?.actors
        if (viewModel.getFilmRepository().value?.hd == 1){
            binding.isHD.visibility = View.VISIBLE
            binding.isHDDiscrp.visibility = View.VISIBLE
            binding.isHD.text = "HD"
        }else {
            binding.isHD.visibility = View.GONE
            binding.isHDDiscrp.visibility = View.GONE
        }

        if (viewModel.getFilmRepository().value?.translate != ""){
            binding.translateTV.visibility = View.VISIBLE
            binding.translateDscrTV.visibility = View.VISIBLE
            binding.translateTV.text = viewModel.getFilmRepository().value?.translate
        }else {
            binding.translateTV.visibility = View.GONE
            binding.translateDscrTV.visibility = View.GONE
        }
        binding.descriptionTV.text = viewModel.getFilmRepository().value?.description
        Picasso.get()
            .load(viewModel.getFilmRepository().value?.cover_200)
            .transform(RoundedCornersTransformation(30,0))
            .fit()
            .into(binding.imageViewDescFragmnet)
        binding.containerDesc.visibility = View.VISIBLE
        binding.seasonSelectLayout.visibility = View.GONE
        createRoundedCorners(binding.playVideoBtn)
    }

    fun showToast(line: String){
        Toast.makeText(requireContext(), line, Toast.LENGTH_SHORT).show()
    }

    fun createRoundedCorners(view: View) {
        val shapeAppearanceModel = ShapeAppearanceModel()
            .toBuilder()
            .setAllCorners(CornerFamily.ROUNDED, android.R.attr.radius.toFloat())
            .build()
        val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
        shapeDrawable.setFillColor(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.btn_background
            )
        )
        shapeDrawable.setStroke(
            2.0f,
            ContextCompat.getColor(requireContext(), R.color.btn_background)
        )
        ViewCompat.setBackground(view, shapeDrawable)
    }
}