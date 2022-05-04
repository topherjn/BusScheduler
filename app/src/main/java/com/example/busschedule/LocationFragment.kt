package com.example.busschedule

import android.Manifest
import android.annotation.SuppressLint

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.location.*


class LocationFragment : Fragment() {

    private var client: FusedLocationProviderClient?= null
    private var locationRequest: LocationRequest?= null
    private var locationCallback: LocationCallback?= null

    private var arrondissementTextView: TextView?= null

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

        arrondissementTextView!!.isEnabled = false

        if (hasLocationPermission()) {
            Toast.makeText(requireContext(),"Tracking",Toast.LENGTH_LONG).show()
            trackLocation()
        }
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

    private fun getArrondissement(location: Location) : Int {
        val geocoder = Geocoder(requireContext())
        var arrondissement = -1

        try {
            val addresses =
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
            var postalCode = addresses[0].postalCode
            Toast.makeText(requireContext(), postalCode.toString(), Toast.LENGTH_LONG).show()
            if (postalCode.length > 1) {
                postalCode = postalCode.substring(postalCode.length - 2)
                if (postalCode[0] == '0') postalCode = postalCode.substring(postalCode.length - 1)
            }
            arrondissement = postalCode.toInt()
            if(arrondissement > 20) arrondissement = arrondissement.mod(20) + 1

            return arrondissement

        } catch (e: Exception) {
            return arrondissement
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            trackLocation()
        }
    }

    private fun trackLocation() {

        locationRequest = LocationRequest.create()
            .setInterval(5000)
            .setFastestInterval(3000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                for(location in locationResult.locations) {
                    val arrondissement = getArrondissement(location).toString()
                    arrondissementTextView!!.text = arrondissement
                    val action = LocationFragmentDirections.actionLocationFragmentToFullScheduleFragment(arrondissement.toInt())
                    arrondissementTextView!!.setOnClickListener { view -> view.findNavController().navigate(action) }
                    arrondissementTextView!!.isEnabled = true
                }
            }
        }

        client = LocationServices.getFusedLocationProviderClient(requireActivity())

        requestLocation()

    }

    override fun onPause() {
        super.onPause()
        client?.removeLocationUpdates(locationCallback!!)
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        if (hasLocationPermission()) {
            client?.requestLocationUpdates(
                locationRequest!!, locationCallback!!, Looper.getMainLooper())
        }
    }

    override fun onResume() {
        super.onResume()

        requestLocation()
    }
}