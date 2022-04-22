package com.example.busschedule

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.google.android.gms.location.*

class LocationFragment : Fragment() {

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

        (activity as AppCompatActivity).supportActionBar?.title = "Arrondissement"

        arrondissementTextView = requireView().findViewById(R.id.arrondissementTextView)
        arrondissementTextView!!.text = null

        startLocationUpdates()


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

        isTracking = true
    }

    private fun stopLocationUpdates() {
        if(isTracking) {
            fusedLocationProviderClient!!.removeLocationUpdates(locationCallback!!)
            fusedLocationProviderClient = null
            isTracking = false
        }
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
            var arrondissement = postalCode.toInt()
            if(arrondissement > 20) arrondissement = arrondissement.mod(20) + 1

            Toast.makeText(requireContext(), postalCode.toString(), Toast.LENGTH_LONG).show()

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

    override fun onResume() {
        super.onResume()

        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()

        stopLocationUpdates()

    }

    override fun onStop() {
        super.onStop()

        stopLocationUpdates()
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 200
    }
}