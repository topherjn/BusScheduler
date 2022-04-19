package com.example.busschedule

import android.Manifest
import android.location.Location
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import com.google.android.gms.location.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class LocationFragment : Fragment() {

    private var gpsButton: Button? = null
    private var arrondissementTextView: TextView? = null

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var isTracking = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gpsButton = requireView().findViewById(R.id.getLocationButton)
        arrondissementTextView = requireView().findViewById(R.id.arrondissementTextView)
        arrondissementTextView!!.text = null

        gpsButton!!.setOnClickListener { startLocationUpdates() }

    }

    private fun startLocationUpdates() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = 5000
        locationRequest!!.fastestInterval = 1000
        locationRequest!!.isWaitForAccurateLocation = true
        locationRequest!!.smallestDisplacement = 0f

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    updateLocationTextBox(location)
                }
            }
        }

        gpsButton!!.setText(R.string.stop)
        gpsButton!!.setOnClickListener { stopLocationUpdates() }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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

        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest!!,
            locationCallback!!,
            Looper.getMainLooper()!!
        )
        Log.d("TAG", "looper")
        isTracking = true
    }

    private fun stopLocationUpdates() {
        gpsButton!!.setText(R.string.start)
        gpsButton!!.setOnClickListener { startLocationUpdates() }
        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback!!)
        isTracking = false
    }

    private fun updateLocationTextBox(lastLocation: Location) {
        val geocoder = Geocoder(getContext())
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
            arrondissementTextView!!.text = getString(R.string.location_unavailable)
        }

        val action = LocationFragmentDirections.actionLocationFragmentToFullScheduleFragment(arrondissementTextView!!.text.toString().toInt())
        arrondissementTextView!!.setOnClickListener { view -> view.findNavController().navigate(action)}
    }

    override fun onResume() {
        super.onResume()
        if (isTracking) {
            startLocationUpdates()
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 200
    }
}