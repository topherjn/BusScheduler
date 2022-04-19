package com.example.busschedule

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import com.google.android.gms.location.LocationRequest
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LocationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LocationFragment : Fragment() {


    private var gpsButton: Button? = null
    private var arrondissementTextView: TextView? = null
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var isTracking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = 1000
        locationRequest!!.fastestInterval = 500
        locationRequest!!.isWaitForAccurateLocation = true
        locationRequest!!.smallestDisplacement = 0f

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    updateLocationTextBox(location)
                }
            }
        }

        gpsButton = requireView().findViewById(R.id.getLocationButton)
        arrondissementTextView = requireView().findViewById(R.id.arrondissementTextView)

        gpsButton!!.setOnClickListener {  view: View? -> startLocationUpdates()  }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), PERMISSION_REQUEST_CODE
            )
            Toast.makeText(requireContext(), "Need to grant location permissions", Toast.LENGTH_LONG).show()
            return
        }
        gpsButton!!.setText(R.string.stop)
        gpsButton!!.setOnClickListener { view: View? -> stopLocationUpdates() }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest!!,
            locationCallback!!,
            Looper.getMainLooper()
        )
        isTracking = true
    }

    private fun stopLocationUpdates() {
        gpsButton!!.setText(R.string.start)
        gpsButton!!.setOnClickListener { view: View? -> startLocationUpdates() }
        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback!!)
        isTracking = false
    }

    private fun updateLocationTextBox(lastLocation: Location) {
        val geocoder = Geocoder(activity)
        try {
            val addresses =
                geocoder.getFromLocation(lastLocation.latitude, lastLocation.longitude, 1)
            var postalCode = addresses[0].postalCode
            if (postalCode.length > 1) {
                postalCode = postalCode.substring(postalCode.length - 2)
                if (postalCode[0] == '0') postalCode = postalCode.substring(postalCode.length - 1)
            }
            arrondissementTextView!!.text = postalCode
            arrondissementTextView!!.isEnabled = true
            fusedLocationProviderClient!!.removeLocationUpdates(locationCallback!!)
        } catch (e: Exception) {
            arrondissementTextView!!.text = "XX"
        }
    }

    override fun onResume() {
        super.onResume()
        if (isTracking) {
            startLocationUpdates()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LocationFragment.
         */
        // TODO: Rename and change types and number of parameters
        private const val PERMISSION_REQUEST_CODE = 200

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LocationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}