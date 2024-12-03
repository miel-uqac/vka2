package com.example.klavier

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.asLiveData
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.klavier.data.Layout
import com.example.klavier.ui.screens.MainScreen
import com.example.klavier.ui.screens.SettingsScreen
import com.example.klavier.ui.screens.SplashScreen

enum class KlavierScreen(@StringRes val title: Int) {
    Start(title = R.string.splash_screen),
    Main(title = R.string.app_name),
    Settings(title = R.string.settings)
}

// Composant principal de l'application
@Composable
fun KlavierApp(
    viewModel: USBViewModel,
    SettingViewModel: SettingViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    isDarkTheme: Boolean,
    sensibility: Float

    ) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val connected by viewModel.isConnected.observeAsState(false)
    val hasPermission by viewModel.hasPermission.observeAsState(false)
    val currentLayout = SettingViewModel.settingPreferences.asLiveData().observeAsState().value?.layout ?: Layout.FR
    val currentScreen = KlavierScreen.valueOf(
        backStackEntry?.destination?.route ?: KlavierScreen.Start.name
    )

    // Création des tableaux des macros
    var macroLabels by remember { mutableStateOf(ArrayList<String>()) }
    var macroIcons by remember { mutableStateOf(ArrayList<Int>()) }
    var macroFunctions by remember { mutableStateOf(ArrayList<() -> Unit>()) }

    //Gère les routes
    Scaffold{ innerPadding ->
        NavHost(
            navController = navController,
            startDestination = KlavierScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            //Route vers l'écran de démarrage
            composable(route = KlavierScreen.Start.name) {
                SplashScreen(
                    connected = connected,
                    hasPermission = hasPermission,
                    onGranted = { navController.navigate(route = KlavierScreen.Main.name) },
                    askPermission = viewModel::askPermission
                )
            }
            //Route vers l'écran principal
            composable(route = KlavierScreen.Main.name) {
                MainScreen(
                    sendData = viewModel::writeUSB,
                    sensibility = sensibility,
                    onSettingsButtonClicked = { navController.navigate(route = KlavierScreen.Settings.name) },

                    // Les tableaux des macros sont passés en paramètre à l'écran principal
                    macroLabels,
                    macroIcons,
                    macroFunctions
                )
            }
            //Route vers l'écran de paramètres
            composable(route = KlavierScreen.Settings.name) {
                SettingsScreen(
                    ChangeTheme = SettingViewModel::updateDarkTheme,
                    ChangeSensibility = SettingViewModel::updateSensibility,
                    sensibility = sensibility,
                    isDarkTheme = isDarkTheme,
                    SetLayout = SettingViewModel::updateLayout,
                    actualLayout = currentLayout,
                    onBackButtonClicked = { navController.navigate(route = KlavierScreen.Main.name) }
                )
            }
        }
        //Si la permission n'est pas accordée, navigue vers l'écran de démarrage
        LaunchedEffect(!hasPermission) {
            if(!hasPermission) {
                navController.navigate(route =KlavierScreen.Start.name)
            }
        }
    }
}