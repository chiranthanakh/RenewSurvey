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
import android.widget.TextView
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
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Download, view, navigate to, and delete an offline region.
 */
class MapManagerActivity : AppCompatActivity(), MapboxMap.OnMapClickListener,
    PermissionsListener {
    private var mapView: MapView? = null
    private var map: MapboxMap? = null
    private var tv_sqft : TextView? = null
    private var progressBar: ProgressBar? = null
    private var downloadButton: Button? = null
    private var listButton: Button? = null
    private var startlocation: Button? = null
    private var stopLocation: Button? = null
    private var savebutton: Button? = null
    private var isEndNotified = false
    private var regionSelected = 0
    private var offlineManager: OfflineManager? = null
    private var offlineRegion: OfflineRegion? = null
    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private var own_location: Point? = null
    private var points: MutableList<LatLng> = mutableListOf()
    //private var points: MutableSet<LatLng> = mutableSetOf()
    private lateinit var locationEngine: LocationEngine
    private lateinit var locationManager: LocationManager
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var geoJsonSource: GeoJsonSource
    private lateinit var lineLayer: LineLayer
    var latLngArrayListPolygon = mutableListOf<LatLng>()
    val distancesFromMidPointsOfPolygonEdges = mutableListOf<Double>()
    private var polyline: Polyline? = null
    var place : String? = ""
    private val lineCoordinates = mutableListOf<LatLng>()

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, "sk.eyJ1IjoiY2hpcnVoZW1hIiwiYSI6ImNsd3NtcjkzYzAxZ2EybXNjaTAwcWN3eWYifQ.m1qVAeCqngttwSX6hr_K3A")
        setContentView(R.layout.activity_map_manager)

         val intent = intent
         place = intent.getStringExtra("place")
        mapView = findViewById(R.id.mapView)
         tv_sqft = findViewById(R.id.tv_sqft)
        mapView?.onCreate(savedInstanceState)
         locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mapView?.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(mapboxMap: MapboxMap) {
                map = mapboxMap
                mapboxMap?.setStyle(Style.SATELLITE_STREETS, object : Style.OnStyleLoaded {
                    override fun onStyleLoaded(style: Style) {
                        initializeLocationEngine()
                        //startLocationUpdates()
                        progressBar = findViewById(R.id.progress_bar)
                        offlineManager = OfflineManager.getInstance(this@MapManagerActivity)
                        downloadButton = findViewById(R.id.download_button)
                        savebutton = findViewById(R.id.save_button)
                        startlocation = findViewById(R.id.start_location_button)
                        stopLocation = findViewById(R.id.stop_location_button)
                        if(place.equals("1")){
                            savebutton?.visibility = View.GONE
                            startlocation?.visibility = View.GONE
                            stopLocation?.visibility = View.GONE
                        } else {
                            downloadButton?.visibility = View.GONE
                            // startlocation?.visibility = View.GONE
                            stopLocation?.visibility = View.GONE
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
                        startlocation?.setOnClickListener{
                            startLocationUpdates()
                        }
                        stopLocation?.setOnClickListener{
                            stopLocationUpdates()
                        }

                        val polylineOptions = PolylineOptions()
                            .color(Color.RED)
                            .width(5f)

                        // Add polyline to the map and store the reference
                         polyline = map?.addPolyline(polylineOptions)

                        listButton = findViewById(R.id.list_button)
                        listButton?.setOnClickListener { downloadedRegionList() }
                        mapboxMap.addOnMapClickListener(this@MapManagerActivity)
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

    private fun sortPoints(points: List<LatLng>): List<LatLng> {
        // Calculate the centroid of the points
        val centroid = LatLng(
            points.map { it.latitude }.average(),
            points.map { it.longitude }.average()
        )

        // Sort points by angle relative to the centroid (clockwise)
        return points.sortedWith { p1, p2 ->
            val angle1 = atan2(p1.latitude - centroid.latitude, p1.longitude - centroid.longitude)
            val angle2 = atan2(p2.latitude - centroid.latitude, p2.longitude - centroid.longitude)
            angle1.compareTo(angle2)
        }
    }

    // Function to update the polyline with the new point
    private fun updatePolyline(newPoint: LatLng) {
        points.add(newPoint)
        if (points.size >= 2) {
            val polylineOptions = PolylineOptions()
                .addAll(points)  // Add all points to the polyline
                .color(Color.BLUE)  // You can customize the color of the polyline here
                .width(5f)  // Set width of the polyline if necessary

            map?.addPolyline(polylineOptions)
        }
    }
    private fun drawPolygon() {
        map?.clear()
        val sortedPoints = sortPoints(points)
        if (sortedPoints.size >= 3) {  // Ensure there are at least 3 points to form a polygon
            val polygonOptions = PolygonOptions()
                .addAll(sortedPoints)  // Add sorted points to form the polygon
                .fillColor(Color.argb(128, 0, 0, 255))  // Set fill color (semi-transparent blue)
                .strokeColor(Color.BLUE)  // Set the border color of the polygon
                //.strokeWidth(5f)  // Set border width if necessary

            map?.addPolygon(polygonOptions)
        }
    }
    override fun onMapClick(p0: LatLng): Boolean {
        points.add(p0)
        updatePolyline(p0)
        val areaInSquareFeet = calculateAreaInSquareFeet(points)
        tv_sqft?.text = "$areaInSquareFeet Sqft"
        Log.d("latlogpoints", areaInSquareFeet.toString())
        if (points.size >= 3) {
            drawPolygon()
        }
        return true
    }

//    override fun onMapClick(p0: LatLng): Boolean {
//
//
//        Log.d("latlogpoints", p0.toString())
//        points.add(p0)
//        updatePolyline(p0)
//        if (points.size >= 2 && place != "1") {
//            drawRectangle()
//        }
//        val destination = Point.fromLngLat(p0.longitude, p0.latitude)
//        return true
//    }
//
//
//
//    private fun updatePolyline(newPoint: LatLng) {
//        if (points.size >= 2) {
//            val previousPoint = points[points.size - 1]
//            val polylineOptions = PolylineOptions()
//                //.add(previousPoint)
//                .add(newPoint)
//            map?.addPolyline(polylineOptions)
//        }
//    }
//
//    private fun drawRectangle() {
//        // Clear previous polygon if exists
//        map?.clear()
//
//        // Add the polygon with all points up to this point.
//        map?.addPolygon(
//            PolygonOptions()
//                .addAll(points) // Add all points so far
//                .fillColor(resources.getColor(R.color.mapbox_blue_opacity))
//                .strokeColor(resources.getColor(R.color.mapbox_blue_opacity))
//        )
//        Log.d("Rectangle Coordinates", points.toString())
//    }


    fun minIndex(list: List<Double>): Int {
        return list.indexOf(list.minOrNull() ?: Double.MAX_VALUE)
    }

    fun <T> rotate(aL: MutableList<T>, shift: Int): MutableList<T> {
        if (aL.isEmpty()) return aL

        for (i in 0 until shift) {
            val element = aL.removeAt(aL.size - 1)
            aL.add(0, element)
        }

        return aL
    }

    private fun computeCentroid(points: List<LatLng>): LatLng {
        var latitude = 0.0
        var longitude = 0.0
        val n = points.size

        for (point in points) {
            latitude += point.latitude
            longitude += point.longitude
        }

        return LatLng(latitude / n, longitude / n)
    }

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
        //stopLocationUpdates()
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
        val builder = AlertDialog.Builder(this@MapManagerActivity)
        val regionNameEdit: EditText = EditText(this@MapManagerActivity)
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
                                this@MapManagerActivity,
                                "Region name cannot be empty.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            //Begin download process
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
                    this@MapManagerActivity.getResources().getDisplayMetrics().density
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
                            this@MapManagerActivity.offlineRegion = offlineRegion
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
                val dialog: AlertDialog = AlertDialog.Builder(this@MapManagerActivity)
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
                                    this@MapManagerActivity,
                                    items[regionSelected],
                                    Toast.LENGTH_LONG
                                ).show()
                                val bounds: LatLngBounds =
                                    offlineRegions[regionSelected].getDefinition().getBounds()
                                val regionZoom: Double =
                                    offlineRegions[regionSelected].getDefinition().getMinZoom()
                                val cameraPosition: CameraPosition = CameraPosition.Builder()
                                    .target(bounds.getCenter())
                                    .zoom(regionZoom)
                                    .build()
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
        Toast.makeText(this@MapManagerActivity, message, Toast.LENGTH_LONG).show()
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
                        this@MapManagerActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this@MapManagerActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) { return }
                points.clear()
                startlocation?.visibility = View.GONE
                stopLocation?.visibility = View.VISIBLE

                locationEngine.getLastLocation(callback)
                handler.postDelayed(this, 60000) // 30 seconds
            }
        }
        handler.post(runnable)
    }

    private fun stopLocationUpdates() {
        // Remove any pending callbacks for the Runnable
        handler.removeCallbacks(runnable)

        // If the locationEngine is not null, remove its location updates
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startlocation?.visibility = View.VISIBLE
            stopLocation?.visibility = View.GONE
            locationEngine.removeLocationUpdates(callback)
        }
    }

    private val callback = object : LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(result: LocationEngineResult?) {
            result?.lastLocation?.let { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                //points.add(latLng)
                //drawPolygon()
                Log.d("printlocationcoorinates",location.longitude.toString())

            }
        }

        override fun onFailure(exception: Exception) {
            // Handle the failure
            Log.d("printlocationcoorinates",exception.toString())

        }
    }


    val EARTH_RADIUS = 6371000.0  // in meters
    fun degreesToRadians(degrees: Double): Double {
        return degrees * (Math.PI / 180.0)
    }

    fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val lat1Rad = degreesToRadians(lat1)
        val lon1Rad = degreesToRadians(lon1)
        val lat2Rad = degreesToRadians(lat2)
        val lon2Rad = degreesToRadians(lon2)

        val deltaLat = lat2Rad - lat1Rad
        val deltaLon = lon2Rad - lon1Rad

        val a = sin(deltaLat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(deltaLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS * c  // Returns the distance in meters
    }

    fun calculatePolygonArea(points: List<LatLng>): Double {
        var area = 0.0

        for (i in 0 until points.size) {
            val j = (i + 1) % points.size  // The next point, wrapping around
            area += points[i].longitude * points[j].latitude
            area -= points[i].latitude * points[j].longitude
        }

        area = abs(area) / 2.0
        return area  // Returns the area in square meters
    }

    fun calculateAreaInSquareFeet(points: List<LatLng>): Double {
        val areaInSquareMeters = calculatePolygonArea(points)
        return areaInSquareMeters * 10.7639  // Convert square meters to square feet
    }

}
