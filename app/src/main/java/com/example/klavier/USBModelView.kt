package com.example.klavier

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class USBViewModel(private val usbController: USBController,context: Context) : ViewModel(){

    private var _hasPermission = MutableLiveData(false)
    val hasPermission: LiveData<Boolean> = _hasPermission

    private var _isConnected = MutableLiveData(false)
    val isConnected: LiveData<Boolean> = _isConnected

    init {
        ManageUSB(context)
    }

    fun ManageUSB(context: Context) {
        usbController.ManageUSB(context)
        checkConnected()
        checkUSBPermission()
    }

    fun connectToDevice() {
        usbController.connectToDevice()
        checkConnected()
        checkUSBPermission()
    }

    fun disconnectUSB() {
        usbController.disconnectFromDevice()
        checkConnected()
        checkUSBPermission()
    }

    fun writeUSB(data: String) {
        usbController.USBWrite(data)
    }

    fun checkConnected() {
        _isConnected.value = usbController.USBConnected
    }

    fun checkUSBPermission() {
        _hasPermission.value = usbController.hasDevicePermission
    }

}