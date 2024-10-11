package com.example.klavier

import android.os.Build
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.klavier.ui.theme.KlavierTheme
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber


class MainActivity : ComponentActivity() {

    var m_manager : UsbManager? = null
    var m_device : UsbDevice? = null
    var m_driver : UsbSerialDriver? = null
    var m_connection : UsbDeviceConnection? = null
    var m_port : UsbSerialPort? = null
    val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    val WRITE_WAIT_MILLIS = 2000;

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KlavierTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    KlavierApp()
                }
            }
        }

        m_manager = getSystemService(Context.USB_SERVICE) as UsbManager
        val filter = IntentFilter()
        filter.addAction(ACTION_USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        registerReceiver(broadcastReceiver, filter)

        USBConnect()

    }

    fun USBConnect() {
        val availableDrivers : List<UsbSerialDriver> = UsbSerialProber.getDefaultProber().findAllDrivers(m_manager)
        if (availableDrivers.isEmpty()) {
            return
        }
        else {
            m_driver = availableDrivers[0]
            if (m_driver != null) {
                m_device = m_driver!!.device
                Log.i("USB", "USB device name: " + m_device!!.deviceName)

                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_MUTABLE else 0
                val intent: PendingIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), flags)


                if (m_manager!!.hasPermission(m_driver!!.device)) {
                    // Permission already granted, no need to request
                    connect()  // Skip to opening the connection
                    Log.i("USB", "USB permission not requested")
                } else {
                    // Request permission
                    m_manager!!.requestPermission(m_driver!!.device, intent)
                    Log.i("USB", "USB permission requested")
                }

            }
        }


    }

    fun USBWrite(data: String) {
        Log.i("USB", "USB write")
        m_port?.write((data+"\r\n").toByteArray(), WRITE_WAIT_MILLIS)
    }

    fun USBRead() {
        //TODO
    }

    fun connect(){
        m_connection = m_manager?.openDevice(m_driver?.device)
        m_port = m_driver?.ports?.get(0)
        if (m_port != null) {
            m_port!!.open(m_connection)
            m_port!!.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
            USBWrite("red")
        } else {
            Log.i("USB", "port is null")
        }
    }

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent) {
            if (p1.action == ACTION_USB_PERMISSION) {
                val granted = p1.extras?.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED) ?: false
                Log.i("USB", p1.extras.toString())
                if (granted) {
                    connect()
                } else {
                    Log.i("USB", "Permission denied for device")
                }
            } else if (p1.action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
                USBConnect()
            } else if (p1.action == UsbManager.ACTION_USB_DEVICE_DETACHED) {
                m_port?.close()
                m_connection?.close()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister the BroadcastReceiver to prevent memory leaks
        unregisterReceiver(broadcastReceiver)
    }
}




@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}