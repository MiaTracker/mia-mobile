package com.nara.mia.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nara.mia.mobile.infrastructure.Config
import com.nara.mia.mobile.infrastructure.PrefDataStore
import com.nara.mia.mobile.infrastructure.isInstanceUrlInitialized
import com.nara.mia.mobile.infrastructure.isTokenPresent
import com.nara.mia.mobile.pages.IndexPage
import com.nara.mia.mobile.pages.InstanceSelectionPage
import com.nara.mia.mobile.pages.LogPage
import com.nara.mia.mobile.pages.LoginPage
import com.nara.mia.mobile.pages.MoviePage
import com.nara.mia.mobile.pages.SeriesPage
import com.nara.mia.mobile.services.Http
import com.nara.mia.mobile.services.Service
import com.nara.mia.mobile.ui.theme.MiaTheme
import com.nara.mia.mobile.view_models.InstanceSelectionViewModel
import com.nara.mia.mobile.view_models.LoginViewModel
import com.nara.mia.mobile.view_models.MediaIndexViewModel
import com.nara.mia.mobile.view_models.MovieViewModel
import com.nara.mia.mobile.view_models.MoviesIndexViewModel
import com.nara.mia.mobile.view_models.SeriesIndexViewModel
import com.nara.mia.mobile.view_models.SeriesViewModel
import com.nara.mia.mobile.view_models.WatchlistViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runBlocking {
            Config.init(PrefDataStore.get(baseContext)) {
                setInitialPage()
            }
        }
    }

    private fun setInitialPage() {
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


@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "media") {
        composable("media") {
            BasePage(navController = navController) { drawerState ->
                IndexPage(viewModel<MediaIndexViewModel>(), navController, drawerState)
            }
        }
        composable("movies") {
            BasePage(navController = navController) { drawerState ->
                IndexPage(
                    viewModel<MoviesIndexViewModel>(),
                    navController,
                    drawerState
                )
            }
        }
        composable("series") {
            BasePage(navController = navController) { drawerState ->
                IndexPage(
                    viewModel<SeriesIndexViewModel>(),
                    navController,
                    drawerState
                )
            }
        }
        composable("watchlist") {
            BasePage(navController = navController) { drawerState ->
                IndexPage(
                    viewModel<WatchlistViewModel>(),
                    navController,
                    drawerState
                )
            }
        }
        composable("movie/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) {
            MoviePage(
                viewModel(
                    initializer = {
                        MovieViewModel(it.arguments!!.getInt("id"))
                    }
                ),
                navController
            )
        }
        composable("series/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) {
            SeriesPage(
                viewModel = viewModel(
                    initializer = {
                        SeriesViewModel(it.arguments!!.getInt("id"))
                    }
                ),
                navController = navController
            )
        }
        composable("log") {
            LogPage { navController.popBackStack() }
        }
    }
}

@Composable
fun BasePage(navController: NavController, page: @Composable (DrawerState) -> Unit) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: "media"
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                Modifier.width(300.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 10.dp, end = 10.dp, bottom = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(start = 10.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.app_icon),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "Mia",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text(text = "All") },
                        selected = (currentRoute == "media"),
                        onClick = { navController.navigate("media") },
                        shape = RoundedCornerShape(topEndPercent = 100, bottomEndPercent = 100)
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "Movies") },
                        selected = (currentRoute == "movies"),
                        onClick = { navController.navigate("movies") },
                        shape = RoundedCornerShape(topEndPercent = 100, bottomEndPercent = 100)
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "Series") },
                        selected = (currentRoute == "series"),
                        onClick = { navController.navigate("series") },
                        shape = RoundedCornerShape(topEndPercent = 100, bottomEndPercent = 100)
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "Watchlist") },
                        selected = (currentRoute == "watchlist"),
                        onClick = { navController.navigate("watchlist") },
                        shape = RoundedCornerShape(topEndPercent = 100, bottomEndPercent = 100)
                    )
                    Spacer(modifier = Modifier.weight(2.0f))
                    NavigationDrawerItem(
                        label = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Text(text = "New log")
                            }
                        },
                        selected = (currentRoute == "log"),
                        onClick = { navController.navigate("log") },
                        shape = RoundedCornerShape(topEndPercent = 100, bottomEndPercent = 100)
                    )
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                                Text(text = "Logout")
                            }
                        },
                        selected = false,
                        onClick = {
                            coroutineScope.launch {
                                Config.run?.clearToken()
                            }
                        },
                        shape = RoundedCornerShape(topEndPercent = 100, bottomEndPercent = 100)
                    )
                }
            }
        }) {
        page(drawerState)
    }
}