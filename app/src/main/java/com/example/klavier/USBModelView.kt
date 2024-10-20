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
        usbController.ManageUSB(context)
        checkUSBPermission()
    }

    fun checkUSBPermission() {
        _hasPermission.value = usbController.hasDevicePermission
    }

    fun writeUSB(data: String) {
        usbController.USBWrite(data)
    }

}