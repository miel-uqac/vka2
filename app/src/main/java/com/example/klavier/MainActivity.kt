package com.example.klavier

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.klavier.ui.theme.KlavierTheme


class MainActivity : ComponentActivity() {

    var usbController : USBController? = null
    val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"

    override fun onCreate(savedInstanceState: Bundle?) {

        usbController = USBController(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KlavierTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    keyboardTest(
                        usbController = usbController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }


        val filter = IntentFilter()
        filter.addAction(ACTION_USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        registerReceiver(broadcastReceiver, filter)

        usbController?.ManageUSB()

    }

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent) {
            if (p1.action == ACTION_USB_PERMISSION) {
                val granted = p1.extras?.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED) ?: false
                Log.i("USB", p1.extras.toString())
                if (granted) {
                    usbController?.connectToDevice()
                } else {
                    Log.i("USB", "Permission denied for device")
                }
            } else if (p1.action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
                usbController?.ManageUSB()
            } else if (p1.action == UsbManager.ACTION_USB_DEVICE_DETACHED) {
                usbController?.disconnectFromDevice()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}




@Composable
fun keyboardTest(usbController: USBController? = null, modifier: Modifier = Modifier) {
    var input by remember { mutableStateOf("") }
    val backspace : String = "\b"
    TextField(
        value = input,
        onValueChange = { newText ->

            val addedText =if (newText.isEmpty()) {
                ""
            }
            else if ((newText.length > input.length)) {
                newText.substring(input.length)
            } else if ((newText.length < input.length) and (newText.last() != '.')) {
                backspace.repeat(input.length - newText.length)
            }
            else if(newText.last() == '.') {
                newText.substring(input.length-1)
            }
            else{
                ""
            }
            input = if (addedText != ".") newText else ""

            if (addedText.isNotEmpty()) {
                usbController?.USBWrite(addedText)
                Log.i("USB", addedText)
            }
                                        },
        label = { Text("Label") }

    )
}



