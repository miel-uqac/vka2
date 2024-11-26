package com.example.klavier

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.klavier.data.USBController

// ViewModel pour la gestion de la communication USB avec le device et la récupération des données de connection/permission pour l'interface utilisateur.
class USBViewModel(private val usbController: USBController, context: Context) : ViewModel(){

    private var _hasPermission = MutableLiveData(false)
    val hasPermission: LiveData<Boolean> = _hasPermission

    private var _isConnected = MutableLiveData(false)
    val isConnected: LiveData<Boolean> = _isConnected

    init {
        ManageUSB(context)
    }

    //Supervise la permission USB puis la connexion si la permission était déjà accordée
    fun ManageUSB(context: Context) {
        usbController.ManageUSB(context)
        checkConnected()
        checkUSBPermission()
    }

    //Ouvre la connexion avec le device
    fun connectToDevice() {
        usbController.connectToDevice()
        checkConnected()
        checkUSBPermission()
    }

    //Gère la déconnexion
    fun disconnectUSB() {
        usbController.disconnectFromDevice()
        checkConnected()
        checkUSBPermission()
    }

    //Gère l'envoi des données
    fun writeUSB(data: String) {
        usbController.USBWrite(data)
    }

    //Gère la demande de permission à l'utilisateur
    fun askPermission(context: Context){
        usbController.askPermission(context)
    }

    //Met à jour la valeur de la connexion
    private fun checkConnected() {
        _isConnected.value = usbController.USBConnected
    }

    //Met à jour la valeur de la permission
    private fun checkUSBPermission() {
        _hasPermission.value = usbController.hasDevicePermission
    }

}

