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
        Toast.makeText(requireContext(),"onCreateView",Toast.LENGTH_LONG).show()
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Arrondissement"

        arrondissementTextView = requireView().findViewById(R.id.arrondissementTextView)

        arrondissementTextView!!.isEnabled = false

        Toast.makeText(requireContext(),"onViewCreated",Toast.LENGTH_LONG).show()
    }

    private fun hasLocationPermission(): Boolean {

        // Request fine location permission if not already granted
        if (getContext()?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return false
        }

        return true
    }

    private fun getArrondissement(location: Location) : Int {
        val geocoder = Geocoder(getContext())
        var arrondissement : Int

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
            return -1
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

        Toast.makeText(requireContext(),"Tracking",Toast.LENGTH_LONG).show()

        locationRequest = LocationRequest.create()
            .setInterval(5000)
            .setFastestInterval(3000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)


        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Toast.makeText(requireContext(),"LocationResult",Toast.LENGTH_LONG).show()
                for(location in locationResult.locations) {
                    val arrondissement = getArrondissement(location).toString()
                    arrondissementTextView!!.text = arrondissement
                    val action = LocationFragmentDirections.actionLocationFragmentToFullScheduleFragment(arrondissement.toInt())
                    arrondissementTextView!!.setOnClickListener { view -> view.findNavController().navigate(action) }
                    arrondissementTextView!!.isEnabled = true
                }
            }
        }

        client = getContext()?.let { LocationServices.getFusedLocationProviderClient(it) }

        requestLocation()

    }

    override fun onPause() {
        super.onPause()
        client?.removeLocationUpdates(locationCallback!!)
        client = null
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        Toast.makeText(requireContext(), "request", Toast.LENGTH_LONG).show()

        if (hasLocationPermission()) {
            client?.requestLocationUpdates(
                locationRequest!!, locationCallback!!, Looper.getMainLooper())
        }
    }

    override fun onResume() {
        super.onResume()

        trackLocation()
    }
}