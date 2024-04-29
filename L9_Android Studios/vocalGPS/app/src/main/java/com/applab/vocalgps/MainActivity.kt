package com.applab.vocalgps

import android.annotation.SuppressLint
import android.content.ContentProviderClient
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.applab.vocalgps.ui.theme.VocalGpsTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import android.location.Geocoder
import java.util.Locale
import androidx.appcompat.app.AppCompatActivity
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import java.util.*

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    @RequiresApi(Build.VERSION_CODES.Q)
    private val permissions = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequired: Boolean = false

    override fun onResume() {
        super.onResume()
        if (locationRequired) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        locationCallback.let{
            fusedLocationClient.removeLocationUpdates(it)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        locationCallback.let {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 100
            )
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(3000)
                .setMaxUpdateAgeMillis(100)
                .build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()

            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContent {

            var currentLocation by remember {
                mutableStateOf(LatLng(0.toDouble(),0.toDouble()))
            }

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    for(location in p0.locations) {
                        currentLocation = LatLng(location.latitude, location.longitude)
                    }
                }
            }
            VocalGpsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LocationScreen(this@MainActivity,currentLocation)
                }
            }
        }
    }

    private fun speakOut(text : String) {
//        val stringText = text.toString()
        Log.i("TAG", "test: "+text);
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    public override fun onDestroy() {
        // Shutdown TTS when
        // activity is destroyed
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    private fun LocationScreen(context : Context, currentLocation: LatLng) {

        val launchMultiplePermissions = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissionMaps ->
            val areGranted = permissionMaps.values.reduce {acc, next -> acc && next}
            if(areGranted) {
                locationRequired = true
                startLocationUpdates()
                Log.i("TAG", "MESSAGE1");
                Toast.makeText(context, "permission Granted", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context, "permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Your Location: ${currentLocation.latitude}/${currentLocation.longitude}")
                Button(onClick = {
                    Log.i("TAG", "MESSAGE2");
                    if (permissions.all{
                        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                        })
                    {
                        Log.i("TAG", "MESSAGE3");
                        startLocationUpdates()

                        val geocoder = Geocoder(context, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)


                        // Extract address components (e.g., street, city, state, postal code)
                        if (addresses != null) {
                            if (addresses.isNotEmpty()) {
                                val address = addresses[0]
                                val street = address.thoroughfare // Street name
                                val city = address.locality // City
                                val state = address.adminArea // State
                                val postalCode = address.postalCode // Postal code

                                // Use the extracted address components as needed
//                                println("Street: $street, City: $city, State: $state, Postal Code: $postalCode")
                                Log.i("TAG", "Street: $street, City: $city, State: $state, Postal Code: $postalCode");
                                speakOut(city)

                            } else {
//                                println("No address found for the given location.")
                                Log.i("TAG", "No address found for the given location.");
                            }
                        }
                    }
                    else {
                        launchMultiplePermissions.launch(permissions)
                    }

                }) {
                    Text(text = "Get your location")
                }
            }

        }
    }
}