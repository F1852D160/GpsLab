package com.example.gpslab

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener

class MainActivity : AppCompatActivity() {
    lateinit var txtUbicacion: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        txtUbicacion = findViewById(R.id.txtLocation)


        if (checkPermisosDeUbicacion()) {
            obtenerUbicacion()
        } else {
            solicitarPermisosDeUbicacion()
        }


    }

    lateinit var fusedLocationClient: FusedLocationProviderClient


    private var REQUEST_LOCATION_CODE = 1001


    private fun checkPermisosDeUbicacion(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun solicitarPermisosDeUbicacion() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            AlertDialog.Builder(this)
                .setTitle("Permiso para obtener la ubicacion")
                .setMessage("Esta app necesita el permiso para obtener la ubicacion .")
                .setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_LOCATION_CODE
                    )
                }
                .create()
                .show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_CODE
            )
        }


    }

    /**
     * Este metodo se ejecuta cuando le otorgamos el permiso de obtener la ubicacion
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_LOCATION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                obtenerUbicacion()
            }
        }
    }


    @SuppressLint("MissingPermission")
    fun obtenerUbicacion(){
        val addOnSuccessListener = fusedLocationClient.getLastLocation()
            .addOnSuccessListener(OnSuccessListener<Location?> { location: Location? ->
                if (location != null) {
                    txtUbicacion.text = "${location.latitude} - ${location.longitude}"

                }else{
                    txtUbicacion.text = "No se puede obtener la ubicacion"
                }
            })
    }
}