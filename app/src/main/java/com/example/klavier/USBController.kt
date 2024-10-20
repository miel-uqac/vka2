package com.example.klavier

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber

class USBController(usbManager: UsbManager) {

    private val usbManager = usbManager as UsbManager
    var m_driver : UsbSerialDriver? = null
    private var m_device : UsbDevice? = null
    var m_connection : UsbDeviceConnection? = null
    var m_port : UsbSerialPort? = null
    var hasDevicePermission = false
    val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    val WRITE_WAIT_MILLIS = 2000;

    fun ManageUSB(context: Context) {
        val availableDrivers: List<UsbSerialDriver> =
            UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (availableDrivers.isEmpty()) {
            Log.i("USB", "No drivers found or no device connected")
            return
        }
        m_driver = availableDrivers[0]
        if (m_driver != null) {
            m_device = m_driver!!.device
            Log.i("USB", "USB device name: " + m_device!!.deviceName)

            val flags =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_MUTABLE else 0
            val intent: PendingIntent =
                PendingIntent.getBroadcast( context,0, Intent(ACTION_USB_PERMISSION), flags)
            hasDevicePermission = usbManager.hasPermission(m_driver!!.device)
            if (hasDevicePermission) {
                // Permission already granted, no need to request
                connectToDevice()  // Skip to opening the connection
                Log.i("USB", "USB permission not requested")
            } else {
                // Request permission
                usbManager.requestPermission(m_driver!!.device, intent)
                Log.i("USB", "USB permission requested")
            }

            }
    }
    fun USBWrite(data: String) {
        Log.i("USB", m_port.toString())
        m_port?.write((data+"\r\n").toByteArray(), WRITE_WAIT_MILLIS)
        Log.i("USB", data)
    }

    fun USBRead() {
        //TODO
    }

    fun connectToDevice(){
        m_connection = usbManager.openDevice(m_driver?.device)
        m_port = m_driver?.ports?.get(0)
        if (m_port != null) {
            m_port!!.open(m_connection)
            m_port!!.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        } else {
            Log.i("USB", "port is null")
        }
    }

    fun disconnectFromDevice(){
        m_port?.close()
        m_connection?.close()
        Log.i("USB", "USB disconnected")
    }

}