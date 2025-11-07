package com.example.klavier

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import com.example.klavier.data.SettingPreferenceRepository
import com.example.klavier.data.USBController
import com.example.klavier.ui.theme.KlavierTheme


class MainActivity : ComponentActivity() {

    // Création des variables globales
    private lateinit var usbController : USBController
    lateinit var viewModel : USBViewModel
    private lateinit var SettingViewModel : SettingViewModel



    override fun onCreate(savedInstanceState: Bundle?) {

        usbController = USBController(usbManager = getSystemService(USB_SERVICE) as UsbManager)
        viewModel = USBViewModel(usbController,this)
        SettingViewModel = SettingViewModel(SettingPreferenceRepository(dataStore = preferencesDataStore),viewModel::writeUSB,this)



        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme = SettingViewModel.settingPreferences.asLiveData().observeAsState().value?.isDarkTheme ?: false
            val sensibility = SettingViewModel.settingPreferences.asLiveData().observeAsState().value?.sensibility ?: 1.2f
            KlavierTheme(darkTheme = isDarkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    KlavierApp(
                        viewModel = viewModel,
                        SettingViewModel = SettingViewModel,
                        isDarkTheme = isDarkTheme,
                        sensibility = sensibility,
                        modifier = Modifier.padding(innerPadding))
                }
            }
        }

        // Gère les intents
        val filter = IntentFilter().apply{
        addAction(ACTION_USB_PERMISSION)
        addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }

        // Enregistre le broadcastReceiver
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
        registerReceiver(broadcastReceiver, filter, RECEIVER_NOT_EXPORTED)
        }else{
        registerReceiver(broadcastReceiver, filter)
        }


    }

    // Définition du broadcastReceiver
    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                ACTION_USB_PERMISSION -> handleUsbPermission(intent)
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> viewModel.ManageUSB(context!!)
                UsbManager.ACTION_USB_DEVICE_DETACHED -> viewModel.disconnectUSB()
            }
    }

        // Vérifie si l'autorisation d'utiliser le port USB est accordée
    private fun handleUsbPermission(intent: Intent) {
        val granted = intent.extras?.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED) ?: false
        Log.i("USB", intent.extras.toString())
        if (granted) {
            try {
                viewModel.connectToDevice()
            } catch (e: Exception) {
                Log.e("USB", "Error connecting to device: ${e.message}")
            }
        } else {
            Log.i("USB", "Permission denied for device")
        }
    }
}

    //Détruit le broadcastReceiver
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    companion object {
        // Nom du fichier de préférences (datastore)
        private const val USER_PREFERENCES_NAME = "user_preferences"
        const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
        private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES_NAME)
    }
}








