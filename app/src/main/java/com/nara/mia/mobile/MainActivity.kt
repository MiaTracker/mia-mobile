package com.nara.mia.mobile

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.nara.mia.mobile.ui.theme.MiaTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nara.mia.mobile.infrastructure.Config
import com.nara.mia.mobile.infrastructure.isInstanceUrlInitialized
import com.nara.mia.mobile.infrastructure.isTokenPresent
import com.nara.mia.mobile.pages.IndexPage
import com.nara.mia.mobile.pages.InstanceSelectionPage
import com.nara.mia.mobile.pages.LoginPage
import com.nara.mia.mobile.services.Http
import com.nara.mia.mobile.services.Service
import com.nara.mia.mobile.view_models.InstanceSelectionViewModel
import com.nara.mia.mobile.view_models.LoginViewModel
import com.nara.mia.mobile.view_models.MediaIndexViewModel
import com.nara.mia.mobile.view_models.MoviesIndexViewModel
import com.nara.mia.mobile.view_models.SeriesIndexViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "config")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runBlocking {
            Config.init(baseContext.dataStore) {
                if (isInstanceUrlInitialized()) {
                    val connected = runBlocking {
                        Http.testConnection(Config.run?.instance)
                    }
                    if(connected) {
                        Service.init()
                        if(isTokenPresent()) setNavigation()
                        else setLoginPage()
                    } else setInstanceSelectionPage()
                } else {
                    setInstanceSelectionPage()
                }
            }
        }
    }

    private fun setLoginPage() {
        setContent {
            MiaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold { innerPadding ->
                        LoginPage(
                            viewModel(
                                initializer = {
                                    LoginViewModel({
                                        setNavigation()
                                    }, {
                                        setInstanceSelectionPage()
                                    })
                                }
                            ),
                            innerPadding
                        )
                    }
                }
            }
        }
    }

    private fun setNavigation() {
        setContent {
            MiaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }

    private fun setInstanceSelectionPage() {
        setContent {
            MiaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold { innerPadding ->
                        InstanceSelectionPage(
                            viewModel(
                                initializer = {
                                    InstanceSelectionViewModel {
                                        Service.init()
                                        setLoginPage()
                                    }
                                }
                            ),
                            innerPadding
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    val navController = rememberNavController()

    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: "media"
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text(text = "All") },
                    selected = (currentRoute == "media"),
                    onClick = { navController.navigate("media") }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Movies") },
                    selected = (currentRoute == "movies"),
                    onClick = { navController.navigate("movies") }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Series") },
                    selected = (currentRoute == "series"),
                    onClick = { navController.navigate("series") }
                )
            }
    }) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    title = { Text(text = "Mia") }
                )
            }
        ) { innerPadding ->
            NavHost(navController = navController, startDestination = "media") {
                composable("media") { IndexPage(viewModel<MediaIndexViewModel>(), innerPadding) }
                composable("movies") { IndexPage(viewModel<MoviesIndexViewModel>(), innerPadding) }
                composable("series") { IndexPage(viewModel<SeriesIndexViewModel>(), innerPadding) }
            }
        }
    }
}