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
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherproject.R
import com.example.weatherproject.databinding.FragmentHomeBinding
import com.example.weatherproject.ui.future.FutureFragmentArgs
import com.example.weatherproject.utils.MyResponse
import com.example.weatherproject.utils.StoreLocationData
import com.example.weatherproject.utils.formatDate
import com.example.weatherproject.utils.setupRecyclerView
import com.example.weatherproject.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    //Binding
    private lateinit var binding: FragmentHomeBinding

    //injection
    @Inject
    lateinit var adapter: HourlyWeatherAdapter

    @Inject
    lateinit var storeLocationData: StoreLocationData

    //Other
    private val viewModel: HomeViewModel by viewModels()
    private var isMainLoading = true
    private var isTodayWeatherLoading = true
    private val args: HomeFragmentArgs by navArgs()


    //location
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

        var lat = args.lat.toDouble()
        var lon = args.lon.toDouble()
        Toast.makeText(requireContext(), "${args.lat} $lon", Toast.LENGTH_SHORT).show()
        //init view
        binding.apply {

            var timeZone: Int = 0
            //check lat and lon
            if (lat == 0.0 && lon == 0.0) {
                homeDisLay.visibility = View.VISIBLE
                mainView.visibility = View.GONE
            } else {
                viewModel.loadCurrentWeatherById(lat.toString(), lon.toString())
                
            }

            //init online lat and lon
            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    lat = location.latitude
                    lon = location.longitude

                    lifecycle.coroutineScope.launchWhenCreated {
                        if (lat != 0.0 && lon != 0.0)
                            storeLocationData.saveLocation(lat, lon)
                    }


                    viewModel.loadCurrentWeatherById(lat.toString(), lon.toString())

                    locationManager.removeUpdates(this)

                }

            }
            //update lat and lon
            swipeRefreshLayout.setOnRefreshListener {
                mainView.visibility = View.GONE
                requestLocationUpdates(locationListener)

            }

            //Fetch Data
            viewModel.currentWeatherData.observe(viewLifecycleOwner) {
                when (it.status) {
                    MyResponse.Status.LOADING -> {
                        homeDisLay.visibility = View.GONE
                        loading.visibility = View.VISIBLE
                        mainView.visibility = View.GONE
                    }

                    MyResponse.Status.SUCCESS -> {
                        homeDisLay.visibility = View.GONE
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
                        homeDisLay.visibility = View.GONE
                        isMainLoading = false
                        updateLoadingState()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
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
                        isTodayWeatherLoading = false
                        updateLoadingState()

                    }

                    MyResponse.Status.ERROR -> {
                        isTodayWeatherLoading = false
                        updateLoadingState()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
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
            if (!isMainLoading && !isTodayWeatherLoading) {

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


