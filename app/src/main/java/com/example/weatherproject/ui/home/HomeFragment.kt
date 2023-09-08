package com.example.weatherproject.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherproject.R
import com.example.weatherproject.databinding.FragmentHomeBinding
import com.example.weatherproject.utils.MyResponse
import com.example.weatherproject.utils.formatDate
import com.example.weatherproject.utils.setupRecyclerView
import com.example.weatherproject.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    //Binding
    private lateinit var binding: FragmentHomeBinding

    //injection

    @Inject
    lateinit var adapter: HourlyWeatherAdapter

    //Other
    private val viewModel: HomeViewModel by viewModels()
    private var isMainLoading = true
    private var isNextWeatherLoading = true
    private lateinit var locationManager: LocationManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat", "DiscouragedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {


            var lat = 0.0
            var lon = 0.0

            var timeZone: Int = 0
            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    lat = location.latitude
                    lon = location.longitude
                    viewModel.loadCurrentWeatherById(lat.toString(), lon.toString())

                    locationManager.removeUpdates(this)

                }

            }

            swipeRefreshLayout.setOnRefreshListener {
                mainView.visibility = View.GONE
                requestLocationUpdates(locationListener)
            }

            requestLocationUpdates(locationListener)


            //Fetch Data
            viewModel.currentWeatherData.observe(viewLifecycleOwner) {
                when (it.status) {
                    MyResponse.Status.LOADING -> {
                        loading.visibility = View.VISIBLE
                        mainView.visibility = View.GONE
                    }

                    MyResponse.Status.SUCCESS -> {
                        timeZone = it.data?.timezone!!

                        val weatherData = it.data

                        locationTxt.text = weatherData.name
                        dateTxt.text = weatherData.dt?.formatDate("E MMM dd | hh:mm a", timeZone)
                        descTxt.text = weatherData.weather?.getOrNull(0)?.description.toString()
                        tempTxt.text = weatherData.main?.temp?.toInt().toString() + "°"
                        val high = weatherData.main?.tempMax?.toInt()
                        val low = weatherData.main?.tempMin?.toInt()
                        HLTempTxt.text = "H: $high° L: $low°"
                        val icon = weatherData.weather?.getOrNull(0)?.icon ?: "02"
                        val iconID = "image$icon"
                        val resourceId = resources.getIdentifier(
                            iconID,
                            "drawable",
                            "com.example.weatherproject"
                        )
                        val imageResource = if (resourceId != 0) resourceId else R.drawable.image02n
                        descImg.setImageResource(imageResource)
                        sunSetVal.text = weatherData.sys?.sunset!!.formatDate("hh : mm a", timeZone)
                        sunRiseVal.text =
                            weatherData.sys.sunrise!!.formatDate("hh : mm a", timeZone)
                        rainValTxt.text = (weatherData.rain?.h ?: 0.0).toString() + "%"
                        windValTxt.text = weatherData.wind?.speed.toString() + "Km/h"
                        humidityValTxt.text = weatherData.main?.humidity.toString() + "%"
                        lat = weatherData.coord?.lat ?: 0.0
                        lon = weatherData.coord?.lon ?: 0.0

                        viewModel.loadNextWeather(lat.toString(), lon.toString())
                        isMainLoading = false
                        updateLoadingState()

                    }

                    MyResponse.Status.ERROR -> {
                        isMainLoading = false
                        updateLoadingState()
                        Toast.makeText(requireContext(), "error1", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            viewModel.nextWeatherListData.observe(viewLifecycleOwner) {
                when (it.status) {
                    MyResponse.Status.LOADING -> {

                    }

                    MyResponse.Status.SUCCESS -> {
                        adapter.setData(it.data!!, timeZone)
                        list.setupRecyclerView(
                            LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            ),
                            adapter
                        )
                        isNextWeatherLoading = false
                        updateLoadingState()

                    }

                    MyResponse.Status.ERROR -> {
                        isNextWeatherLoading = false
                        updateLoadingState()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        Log.e("TAGMessage", "onViewCreated: ${it.message}")
                    }
                }
            }
            NexDaysBtn.setOnClickListener {
                val direction = HomeFragmentDirections.actionHomeFragmentToFutureFragment(
                    lat.toString(),
                    lon.toString()
                )
                findNavController().navigate(direction)
            }

        }


    }

    private fun updateLoadingState() {
        binding.apply {
            if (!isMainLoading && !isNextWeatherLoading) {

                swipeRefreshLayout.isRefreshing = false
                loading.visibility = View.GONE
                mainView.visibility = View.VISIBLE
            } else {
                loading.visibility = View.VISIBLE
                mainView.visibility = View.GONE
            }
        }
    }

    private fun requestLocationUpdates(locationListener: LocationListener) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager =
                requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }
}


