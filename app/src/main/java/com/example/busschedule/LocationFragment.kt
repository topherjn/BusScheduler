package com.example.busschedule

import android.Manifest
import android.annotation.SuppressLint

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.google.android.gms.location.*


class LocationFragment : Fragment() {

    private var arrondissementTextView: TextView? = null

    private var client: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var isTracking = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (hasLocationPermission()) {
            startLocationUpdates()
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

        (activity as AppCompatActivity).supportActionBar?.title = "Arrondissement"

        arrondissementTextView = requireView().findViewById(R.id.arrondissementTextView)
        arrondissementTextView!!.text = null

        startLocationUpdates()

    }

    private fun startLocationUpdates() {

        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = 5000
        locationRequest!!.fastestInterval = 1000

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {

                    //updateLocationTextBox(location)
                    Log.d(TAG,"Location: $location")
                }
            }
        }
        client = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private fun stopLocationUpdates() {
        if(isTracking) {
            client!!.removeLocationUpdates(locationCallback!!)
            client = null
            isTracking = false
        }
    }

    private fun updateLocationTextBox(lastLocation: Location) {
        val geocoder = Geocoder(getContext())

        try {
            val addresses =
                geocoder.getFromLocation(lastLocation.latitude, lastLocation.longitude, 1)
            var postalCode = addresses[0].postalCode
            Toast.makeText(requireContext(), postalCode.toString(), Toast.LENGTH_LONG).show()
            if (postalCode.length > 1) {
                postalCode = postalCode.substring(postalCode.length - 2)
                if (postalCode[0] == '0') postalCode = postalCode.substring(postalCode.length - 1)
            }
            var arrondissement = postalCode.toInt()
            if(arrondissement > 20) arrondissement = arrondissement.mod(20) + 1

            arrondissementTextView!!.text = arrondissement.toString()
            arrondissementTextView!!.isEnabled = true

            val action = LocationFragmentDirections.actionLocationFragmentToFullScheduleFragment(arrondissement)
            arrondissementTextView!!.setOnClickListener { view -> view.findNavController().navigate(action)}

            val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
            ft.setReorderingAllowed(false)
            ft.detach(this).attach(this).commit()

        } catch (e: Exception) {
            arrondissementTextView!!.text = getString(R.string.location_unavailable)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        if (hasLocationPermission()) {
            client?.requestLocationUpdates(
                locationRequest!!, locationCallback!!, Looper.getMainLooper())
        }
    }

    override fun onPause() {
        super.onPause()

        stopLocationUpdates()
    }

    private fun hasLocationPermission(): Boolean {

        // Request fine location permission if not already granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return false
        }
        return true
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            stopLocationUpdates()
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 200
        const val TAG = "LOCATION"
    }
}