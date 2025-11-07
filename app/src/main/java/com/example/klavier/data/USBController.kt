package com.example.klavier.data

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

// classe qui gère l'USB
class USBController(private val usbManager: UsbManager) {

    var m_driver : UsbSerialDriver? = null
    private var m_device : UsbDevice? = null
    var m_connection : UsbDeviceConnection? = null
    var m_port : UsbSerialPort? = null
    var hasDevicePermission = false
    var USBConnected = false
    val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    val WRITE_WAIT_MILLIS = 2000

    // Fonction qui gère la permission et l'ouverture de la connexion USB
    fun ManageUSB(context: Context) {
        val availableDrivers: List<UsbSerialDriver> =
            UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (availableDrivers.isEmpty()) {
            Log.i("USB", "No drivers found or no device connected")
            return
        }
        USBConnected = true
        m_driver = availableDrivers[0]
        if (m_driver != null) {
            m_device = m_driver!!.device
            Log.i("USB", "USB device name: " + m_device!!.deviceName)
            hasDevicePermission = usbManager.hasPermission(m_driver!!.device)
            if (hasDevicePermission) {
                // Permission already granted, no need to request
                connectToDevice()  // Skip to opening the connection
                Log.i("USB", "USB permission not requested")
            } else {
                // Request permission
                askPermission(context)
                Log.i("USB", "USB permission requested")
            }

            }
    }

    // Fonction qui écrit vers le microcontrôleur
    fun USBWrite(data: String) {
        Log.i("USB", m_port.toString())
        m_port?.write((data+"\r\n").toByteArray(), WRITE_WAIT_MILLIS)
        Log.i("USB", data)
    }

    // Fonction qui demande la permission d'utiliser l'appareil USB
    fun askPermission(context: Context){
        val flags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_MUTABLE else 0
        val intent: PendingIntent = if (Build.VERSION.SDK_INT >= 33 /* Android 14.0 (U) */) {
            val intent =  Intent(ACTION_USB_PERMISSION)
            intent.setPackage(context.packageName)
            PendingIntent.getBroadcast(context, 0, intent, flags)
        }
        else{
            PendingIntent.getBroadcast(context, 0, Intent(ACTION_USB_PERMISSION), flags)
        }
        usbManager.requestPermission(m_driver!!.device, intent)
    }

    // Fonction qui ouvre la connexion USB
    fun connectToDevice(){
        hasDevicePermission = usbManager.hasPermission(m_driver!!.device)
        if (!hasDevicePermission) {
            Log.i("USB", "USB permission not granted")
            return
        }
        m_connection = usbManager.openDevice(m_driver?.device)
        m_port = m_driver?.ports?.get(0)
        if (m_port != null) {
            m_port!!.open(m_connection)
            m_port!!.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        } else {
            Log.i("USB", "port is null")
        }
    }

    // Fonction qui ferme la connexion USB
    fun disconnectFromDevice(){
        m_port?.close()
        m_connection?.close()
        USBConnected = false
        hasDevicePermission = false
        Log.i("USB", "USB disconnected")
    }

}