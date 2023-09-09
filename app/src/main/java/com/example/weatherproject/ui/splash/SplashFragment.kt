package com.example.weatherproject.ui.splash

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.example.weatherproject.R
import com.example.weatherproject.databinding.FragmentSplashBinding
import com.example.weatherproject.utils.StoreLocationData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
@AndroidEntryPoint
class SplashFragment : Fragment() {

    //Binding
    private lateinit var binding : FragmentSplashBinding

    @Inject
    lateinit var storeLocationData: StoreLocationData

    //other
    private var latValue = 0.0
    private var lonValue = 0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycle.coroutineScope.launch(Dispatchers.IO){

        }


        lifecycle.coroutineScope.launch {
            storeLocationData.getLocation().collect{(lat , lon) ->

                Log.d("TAG check lat lon", "onViewCreated: $lat , $lon")
                if (lat != 0.0 && lon != 0.0){
                    latValue = lat!!
                    lonValue = lon!!

                }else {

                    latValue = 0.0
                    lonValue = 0.0
                }
                delay(2000)
                val direction = SplashFragmentDirections.actionSplashFragmentToHomeFragment(latValue.toString() , lonValue.toString())
                findNavController().navigate(direction)
            }


        }

    }
}