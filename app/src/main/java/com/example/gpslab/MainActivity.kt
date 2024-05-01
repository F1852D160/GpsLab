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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.UiSettings
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var txtUbicacion: TextView

    private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    lateinit var mMapView: MapView
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




        //inicializar mapa
//        Bundle mapViewBundle = null;
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mMapView = findViewById(R.id.mapView)
        mMapView.onCreate(mapViewBundle)
        mMapView.getMapAsync(this)

        //end inicializar mapa


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

    var myMap: GoogleMap? = null

    var lt = -21.5360986
    var lg = -64.7154756

    override fun onMapReady(map: GoogleMap) {
        myMap = map


        val markerOptions1 =
            MarkerOptions().position(LatLng(-21.585975, -64.700308)).title("Mi Ubicacion1")

        map.addMarker(markerOptions1)


        val markerOptions2 =
            MarkerOptions().position(LatLng(lt, lg)).title("Mi Ubicacion 2")

        map.addMarker(markerOptions2)


        val uiSettings: UiSettings = map.getUiSettings()
        uiSettings.isZoomControlsEnabled = true // Habilitar controles de zoom
        val Liberty =
            CameraPosition.builder().target(LatLng(lt, lg)).zoom(16f).bearing(0f).tilt(45f).build()
        map.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty))

    }
}