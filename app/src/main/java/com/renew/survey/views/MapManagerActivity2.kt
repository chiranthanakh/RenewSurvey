package com.renew.survey.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.PolygonOptions
import com.mapbox.mapboxsdk.annotations.Polyline
import com.mapbox.mapboxsdk.annotations.PolylineOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.offline.OfflineManager
import com.mapbox.mapboxsdk.offline.OfflineRegion
import com.mapbox.mapboxsdk.offline.OfflineRegionError
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.renew.survey.R
import com.renew.survey.utilities.LatLagWrapper
import com.renew.survey.utilities.LocationUpdateService
import org.json.JSONObject

/**
 * Download, view, navigate to, and delete an offline region.
 */
class MapManagerActivity2 : AppCompatActivity(), MapboxMap.OnMapClickListener,
    PermissionsListener {
    private var mapView: MapView? = null
    private var map: MapboxMap? = null
    private var progressBar: ProgressBar? = null
    private var downloadButton: Button? = null
    private var listButton: Button? = null
    private var savebutton: Button? = null
    private var isEndNotified = false
    private var regionSelected = 0
    private var offlineManager: OfflineManager? = null
    private var offlineRegion: OfflineRegion? = null
    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private var own_location: Point? = null
    private var points: MutableList<LatLng> = mutableListOf()
    private lateinit var locationEngine: LocationEngine
    private lateinit var locationManager: LocationManager
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var geoJsonSource: GeoJsonSource
    private lateinit var lineLayer: LineLayer
    private var polyline: Polyline? = null
    private val lineCoordinates = mutableListOf<LatLng>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, "sk.eyJ1IjoiY2hpcnVoZW1hIiwiYSI6ImNsd3NtcjkzYzAxZ2EybXNjaTAwcWN3eWYifQ.m1qVAeCqngttwSX6hr_K3A")
        setContentView(R.layout.activity_map_manager)

        val intent = intent
        val place = intent.getStringExtra("place")
        mapView = findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mapView?.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(mapboxMap: MapboxMap) {
                map = mapboxMap
                mapboxMap?.setStyle(Style.SATELLITE_STREETS, object : Style.OnStyleLoaded {
                    override fun onStyleLoaded(style: Style) {
                        progressBar = findViewById(R.id.progress_bar)
                        offlineManager = OfflineManager.getInstance(this@MapManagerActivity2)
                        downloadButton = findViewById(R.id.download_button)
                        savebutton = findViewById(R.id.save_button)
                        if(place.equals("1")){
                            savebutton?.visibility = View.GONE
                        } else {
                            downloadButton?.visibility = View.GONE
                        }
                        downloadButton?.setOnClickListener {
                            downloadRegionDialog()
                        }
                        savebutton?.setOnClickListener{
                            val wrappedPoints = points.map { LatLagWrapper(it) }
                            val resultIntent = Intent()
                            resultIntent.putParcelableArrayListExtra("key", ArrayList(wrappedPoints))
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        }

                        val polylineOptions = PolylineOptions()
                            .color(Color.RED)
                            .width(5f)

                        // Add polyline to the map and store the reference
                        polyline = map?.addPolyline(polylineOptions)
                        initializeLocationEngine()
                        startLocationUpdates()

                        listButton = findViewById(R.id.list_button)
                        listButton?.setOnClickListener { downloadedRegionList() }
                        mapboxMap.addOnMapClickListener(this@MapManagerActivity2)
                        enableLocationComponent(style)

                    }
                })

            }
        })

        mapView?.getMapAsync { mapboxMap ->
            // initializeLocationEngine()
            //startLocationUpdates()
        }
    }

    override fun onMapClick(p0: LatLng): Boolean {
        Log.d("latlogpoints",p0.toString())
        //points.add(p0)
        if (points.size >= 2) {
            // drawRectangle()
        }
        updatePolyline(p0)
        val destination = Point.fromLngLat(p0.getLongitude(), p0.getLatitude())
        Log.d("RectangleCoordinates", destination.toString())
        return true
    }

    private fun drawRectangle() {
        map?.addPolygon(
            PolygonOptions()
                .addAll(points)
                .fillColor(resources.getColor(R.color.mapbox_blue_opacity))
        )
        Log.d("Rectangle Coordinates", points.toString())
    }

    // Override Activity lifecycle methods
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    protected override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    protected override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    private fun downloadRegionDialog() {
        val builder = AlertDialog.Builder(this@MapManagerActivity2)
        val regionNameEdit: EditText = EditText(this@MapManagerActivity2)
        regionNameEdit.setHint("Enter name")
        builder.setTitle("Name new region")
            .setView(regionNameEdit)
            .setMessage("Looks like something went wrong with the log in process! Please try clearing your browser\\'s cache/cookies and then sign back in.")
            .setPositiveButton(
                "Save",
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val regionName: String = regionNameEdit.getText().toString()
                        // Require a region name to begin the download.
                        // If the user-provided string is empty, display
                        // a toast message and do not begin download.
                        if (regionName.length == 0) {
                            Toast.makeText(
                                this@MapManagerActivity2,
                                "Region name cannot be empty.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Begin download process
                            downloadRegion(regionName)
                        }
                    }
                })
            .setNegativeButton(
                "Cancel",
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        dialog.cancel()
                    }
                })

        // Display the dialog
        builder.show()
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(true)
                .accuracyColor(
                    ContextCompat.getColor(this,
                        R.color.mapboxGreen
                    ))
                .build()

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(this, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()
            // Get an instance of the LocationComponent and then adjust its settings
            map?.locationComponent?.apply {
                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)
                // Enable to make the LocationComponent visible
                isLocationComponentEnabled = true
                // Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING
                // Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
                //  own_location = Point.fromLngLat(map!!.locationComponent.lastKnownLocation!!.longitude, map!!.locationComponent.lastKnownLocation!!.latitude)
                own_location = Point.fromLngLat(79.6608, 9.5530)
            }
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    private fun downloadRegion(regionName: String) {
        startProgress()
        map?.getStyle(object : Style.OnStyleLoaded {
            override fun onStyleLoaded(style: Style) {
                val styleUrl = style.uri
                val bounds: LatLngBounds = map!!.getProjection().getVisibleRegion().latLngBounds
                val minZoom: Double = map!!.getCameraPosition().zoom
                val maxZoom: Double = map!!.getMaxZoomLevel()
                val pixelRatio: Float =
                    this@MapManagerActivity2.getResources().getDisplayMetrics().density
                val definition: OfflineTilePyramidRegionDefinition =
                    OfflineTilePyramidRegionDefinition(
                        styleUrl, bounds, minZoom, maxZoom, pixelRatio
                    )

                var metadata: ByteArray?
                try {
                    val jsonObject: JSONObject = JSONObject()
                    jsonObject.put(JSON_FIELD_REGION_NAME, regionName)
                    val json: String = jsonObject.toString()
                    metadata = json.toByteArray(charset(JSON_CHARSET))
                } catch (exception: Exception) {
                    metadata = null
                }
                offlineManager?.createOfflineRegion(
                    definition,
                    metadata!!,
                    object : OfflineManager.CreateOfflineRegionCallback {
                        override fun onCreate(offlineRegion: OfflineRegion) {
                            //Timber.d("Offline region created: %s", regionName)
                            this@MapManagerActivity2.offlineRegion = offlineRegion
                            launchDownload()
                        }

                        override fun onError(error: String) {
                            //Timber.e("Error: %s", error)
                        }
                    })
            }
        })
    }
    private fun launchDownload() {
        offlineRegion?.setObserver(object : OfflineRegion.OfflineRegionObserver {
            override fun onStatusChanged(status: OfflineRegionStatus) {
                // Compute a percentage
                val percentage = if (status.getRequiredResourceCount() >= 0
                ) (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) else 0.0

                if (status.isComplete()) {
                    // Download complete
                    endProgress("Region downloaded successfully.")
                    return
                } else if (status.isRequiredResourceCountPrecise()) {
                    // Switch to determinate state
                    setPercentage(Math.round(percentage).toInt())
                }
            }

            override fun onError(error: OfflineRegionError) {
                /*Timber.e("onError reason: %s", error.getReason())
                Timber.e("onError message: %s", error.getMessage())*/
            }

            override fun mapboxTileCountLimitExceeded(limit: Long) {
                //Timber.e("Mapbox tile count limit exceeded: %s", limit)
            }
        })

        // Change the region state
        offlineRegion?.setDownloadState(OfflineRegion.STATE_ACTIVE)
    }
    private fun downloadedRegionList() {
        regionSelected = 0
        // Query the DB asynchronously
        offlineManager?.listOfflineRegions(object : OfflineManager.ListOfflineRegionsCallback {
            override fun onList(offlineRegions: Array<OfflineRegion>) {
                // Check result. If no regions have been
                // downloaded yet, notify user and return
                if (offlineRegions == null || offlineRegions.size == 0) {
                    Toast.makeText(
                        getApplicationContext(),
                        "You have no regions yet.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                // Add all of the region names to a list
                val offlineRegionsNames = ArrayList<String>()
                for (offlineRegion in offlineRegions) {
                    offlineRegionsNames.add(getRegionName(offlineRegion))
                }
                val items = offlineRegionsNames.toTypedArray<CharSequence>()

                // Build a dialog containing the list of regions
                val dialog: AlertDialog = AlertDialog.Builder(this@MapManagerActivity2)
                    .setTitle("List")
                    .setSingleChoiceItems(items, 0, object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            // Track which region the user selects
                            regionSelected = which
                        }
                    })
                    .setPositiveButton(
                        "Navigate to",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, id: Int) {
                                Toast.makeText(
                                    this@MapManagerActivity2,
                                    items[regionSelected],
                                    Toast.LENGTH_LONG
                                ).show()

                                // Get the region bounds and zoom
                                val bounds: LatLngBounds =
                                    offlineRegions[regionSelected].getDefinition().getBounds()
                                val regionZoom: Double =
                                    offlineRegions[regionSelected].getDefinition().getMinZoom()

                                // Create new camera position
                                val cameraPosition: CameraPosition = CameraPosition.Builder()
                                    .target(bounds.getCenter())
                                    .zoom(regionZoom)
                                    .build()

                                // Move camera to new position
                                map?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                            }
                        })
                    .setNeutralButton(
                        "Delete",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, id: Int) {
                                progressBar?.setIndeterminate(true)
                                progressBar?.setVisibility(View.VISIBLE)

                                // Begin the deletion process
                                offlineRegions[regionSelected].delete(object :
                                    OfflineRegion.OfflineRegionDeleteCallback {
                                    override fun onDelete() {
                                        // Once the region is deleted, remove the
                                        // progressBar and display a toast
                                        progressBar?.setVisibility(View.INVISIBLE)
                                        progressBar?.setIndeterminate(false)
                                        Toast.makeText(
                                            getApplicationContext(),
                                            "Region deleted",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                    override fun onError(error: String) {
                                        progressBar?.setVisibility(View.INVISIBLE)
                                        progressBar?.setIndeterminate(false)
                                        //Timber.e("Error: %s", error)
                                    }
                                })
                            }
                        })
                    .setNegativeButton(
                        "Cancel",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, id: Int) {
                                // When the user cancels, don't do anything.
                                // The dialog will automatically close
                            }
                        }).create()
                dialog.show()
            }

            override fun onError(error: String) {
                //Timber.e("Error: %s", error)
            }
        })
    }
    private fun getRegionName(offlineRegion: OfflineRegion): String {
        // Get the region name from the offline region metadata
        var regionName: String

        try {
            val metadata: ByteArray = offlineRegion.getMetadata()
            val json = String(metadata, charset(JSON_CHARSET))
            val jsonObject: JSONObject = JSONObject(json)
            regionName = jsonObject.getString(JSON_FIELD_REGION_NAME)
        } catch (exception: Exception) {
            // Timber.e("Failed to decode metadata: %s", exception.message)
            regionName = String.format("Region %1", offlineRegion.getID())
        }
        return regionName
    }
    // Progress bar methods
    private fun startProgress() {
        // Disable buttons
        downloadButton!!.isEnabled = false
        listButton!!.isEnabled = false

        // Start and show the progress bar
        isEndNotified = false
        progressBar?.setIndeterminate(true)
        progressBar?.setVisibility(View.VISIBLE)
    }

    private fun setPercentage(percentage: Int) {
        progressBar?.setIndeterminate(false)
        progressBar?.setProgress(percentage)
    }

    private fun endProgress(message: String) {
        // Don't notify more than once
        if (isEndNotified) {
            return
        }
        downloadButton!!.isEnabled = true
        listButton!!.isEnabled = true
        isEndNotified = true
        progressBar?.setIndeterminate(false)
        progressBar?.setVisibility(View.GONE)
        Toast.makeText(this@MapManagerActivity2, message, Toast.LENGTH_LONG).show()
    }


    companion object {
        private const val TAG = "OffManActivity"

        // JSON encoding/decoding
        const val JSON_CHARSET: String = "UTF-8"
        const val JSON_FIELD_REGION_NAME: String = "FIELD_REGION_NAME"
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        TODO("Not yet implemented")
    }

    override fun onPermissionResult(p0: Boolean) {
        if (p0) {
            enableLocationComponent(map?.style!!)
        } else {
            Toast.makeText(this, "User did not grant permission!", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun initializeLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this)
        val request = LocationEngineRequest.Builder(1000)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(5000)
            .build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationEngine.requestLocationUpdates(request, callback, Looper.getMainLooper())
    }

    private fun startLocationUpdates() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                if (ActivityCompat.checkSelfPermission(
                        this@MapManagerActivity2,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this@MapManagerActivity2,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                locationEngine.getLastLocation(callback)
                handler.postDelayed(this, 30000) // 30 seconds
            }
        }
        handler.post(runnable)
    }

    private val callback = object : LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(result: LocationEngineResult?) {
            result?.lastLocation?.let { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                //updatePolyline(latLng)
                Log.d("printlocationcoorinates",location.longitude.toString())
                map?.setCameraPosition(
                    CameraPosition.Builder()
                        .target(LatLng(location.latitude, location.longitude))
                        .zoom(15.0)
                        .build()
                )
            }
        }

        override fun onFailure(exception: Exception) {
            // Handle the failure
            Log.d("printlocationcoorinates",exception.toString())

        }
    }

    private fun updatePolyline(newLatLng: LatLng) {
        points.add(newLatLng)
        Log.d("printlocationcoorinates",points.toString())
        polyline?.setPoints(points)
    }

}
