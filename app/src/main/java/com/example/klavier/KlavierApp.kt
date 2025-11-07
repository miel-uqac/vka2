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
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.klavier.data.Layout
import com.example.klavier.ui.screens.MainScreen
import com.example.klavier.ui.screens.SettingsScreen
import com.example.klavier.ui.screens.SplashScreen
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

enum class KlavierScreen(@StringRes val title: Int) {
    Start(title = R.string.splash_screen),
    Main(title = R.string.app_name),
    Settings(title = R.string.settings)
}

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

    var macroLabels by remember { mutableStateOf(ArrayList<String>()) }
    var macroIcons by remember { mutableStateOf(ArrayList<Int>()) }
    var macroFunctions by remember { mutableStateOf(ArrayList<() -> Unit>()) }

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = KlavierScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Écran de démarrage
            composable(route = KlavierScreen.Start.name) {
                // ⚠️ On ajoute une possibilité de "Continuer en mode démo"
                SplashScreen(
                    connected = connected,
                    hasPermission = hasPermission,
                    onGranted = { navController.navigate(route = KlavierScreen.Main.name) },
                    askPermission = viewModel::askPermission,
                    onSkipDemo = { navController.navigate(route = KlavierScreen.Main.name) } // <-- NOUVEAU
                )
            }

            // Écran principal
            composable(route = KlavierScreen.Main.name) {
                val ctx = LocalContext.current

                // Wrapper sûr : si pas connecté/permission → Toast, pas de crash
                val safeSendData: (String) -> Unit = { data ->
                    if (connected && hasPermission) {
                        viewModel.writeUSB(data)
                    } else {
                        Toast.makeText(
                            ctx,
                            "Mode démo : aucun micro-contrôleur connecté",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

                MainScreen(
                    sendData = safeSendData,
                    sensibility = sensibility,
                    onSettingsButtonClicked = { navController.navigate(route = KlavierScreen.Settings.name) },
                    macroLabels,
                    macroIcons,
                    macroFunctions
                )
            }

            // Écran paramètres
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

        // ❌ Ancien comportement (forçait le retour au Splash)
        // LaunchedEffect(!hasPermission) { if (!hasPermission) navController.navigate(KlavierScreen.Start.name) }

        // ✅ Nouveau : on NE renvoie plus automatiquement au Splash si la permission n'est pas donnée.
        // On laisse l'utilisateur naviguer en Main (mode démo) ou demander la permission depuis Splash.
        LaunchedEffect(Unit) {
            // Optionnel : rien ici
        }
    }
}
