package com.example.weatherproject.ui.future

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherproject.R
import com.example.weatherproject.databinding.FragmentFutureBinding
import com.example.weatherproject.utils.MyResponse
import com.example.weatherproject.utils.formatDate
import com.example.weatherproject.utils.setupRecyclerView
import com.example.weatherproject.viewmodel.FutureViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.log

@AndroidEntryPoint
class FutureFragment : Fragment() {
    //Binding
    private lateinit var binding: FragmentFutureBinding

    @Inject
    lateinit var adapter: DailyWeatherAdapter

    //Other
    private val args: FutureFragmentArgs by navArgs()
    private val viewModel: FutureViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFutureBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {


            val lat = args.lat
            val lon = args.lon

            val timeZone : Int

            viewModel.loadTomorrowWeather(lat, lon)
            viewModel.tomorrowWeatherData.observe(viewLifecycleOwner) {
                when (it.status) {
                    MyResponse.Status.LOADING -> {
                        mainLay.visibility = View.GONE
                        loading.visibility = View.VISIBLE

                    }

                    MyResponse.Status.SUCCESS -> {


                        val data = it.data!![0]

                        tempTxt.text = data.main?.temp?.toInt().toString() + "°"
                        descTxt.text = data.weather!![0]?.description
                        val unixTimestamp = data.dt

//                        dayTxt.text = unixTimestamp?.formatDate("hh:mm E a" , 12600)
                        Log.d("TAGTimes", "onViewCreated: ${unixTimestamp?.formatDate("hh:mm E a" , 12600)}")
                        Toast.makeText(requireContext(), "${unixTimestamp?.formatDate("hh:mm E a" , 12600)}", Toast.LENGTH_SHORT).show()


                        val icon = data.weather.getOrNull(0)?.icon ?: "01"
                        val iconID = "image$icon"
                        val resourceId = resources.getIdentifier(
                            iconID,
                            "drawable",
                            "com.example.weatherproject"
                        )
                        val imageResource = if (resourceId != 0) resourceId else R.drawable.ic_launcher_background
                        descImg.setImageResource(imageResource)
                        rainValTxt.text = "0.0%"
                        windValTxt.text = data.wind?.speed.toString() + "Km/h"
                        humidityValTxt.text = data.main?.humidity.toString() + "%"


                        mainLay.visibility = View.VISIBLE
                        loading.visibility = View.GONE

                    }

                    MyResponse.Status.ERROR -> {
                        loading.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        Log.e("TAGMessage", "onViewCreated: ${it.message}")
                    }
                }

            }
            backBtn.setOnClickListener {
                val navController = findNavController()
                navController.popBackStack()
            }
            viewModel.loadNextDayWeather(lat, lon)
            viewModel.netDayWeatherData.observe(viewLifecycleOwner) {

                when (it.status) {
                    MyResponse.Status.LOADING -> {

                    }

                    MyResponse.Status.SUCCESS -> {

                        adapter.setData(it.data!!)
                        list.setupRecyclerView(
                            LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.VERTICAL,
                                false
                            ),
                            adapter
                        )

                    }


                    MyResponse.Status.ERROR -> {

                    }
                }

            }
        }
    }

}