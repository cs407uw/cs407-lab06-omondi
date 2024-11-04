package com.cs407.lab6

import android.content.pm.PackageManager
import android.os.Bundle
import android.location.Location
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MainActivity : AppCompatActivity() {
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private val mDestinationLatLng = LatLng(43.0753, -89.4034)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync { googleMap: GoogleMap ->
            mMap = googleMap
            setLocationMarker(
                mDestinationLatLng,
                "Bascom Hall"
            )
            checkLocationPermissionAndDrawPolyline()
        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setLocationMarker(destination: LatLng, destinationName: String) {
        mMap.addMarker(MarkerOptions().position(destination).title(destinationName))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 15f))
    }

    private fun checkLocationPermissionAndDrawPolyline() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            mMap.isMyLocationEnabled = true
            getCurrentLocationAndDrawPolyline()
        }
    }

    private fun getCurrentLocationAndDrawPolyline() {
        mFusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))

                mMap.addPolyline(
                    PolylineOptions()
                        .add(currentLatLng)
                        .add(mDestinationLatLng)
                        .color(ContextCompat.getColor(this, R.color.black))
                        .width(10f)
                )
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocationAndDrawPolyline()
        }
    }
}



