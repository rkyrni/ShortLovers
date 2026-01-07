package com.app.shortlovers.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.shortlovers.ui.theme.BaseBlack
import com.app.shortlovers.ui.theme.BaseYellow
import com.app.shortlovers.ui.view.forYou.ForYouView
import com.app.shortlovers.ui.view.home.HomeView
import com.app.shortlovers.ui.view.myList.MyListView
import com.app.shortlovers.ui.view.profile.ProfileView

@Composable
fun MainView(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    val items =
        listOf(
            BottomNavItem("home", "Home", Icons.Default.Home),
            BottomNavItem("forYou", "For You", Icons.Default.Favorite),
            BottomNavItem("myList", "My List", Icons.Default.List),
            BottomNavItem("profile", "Profile", Icons.Default.Person)
        )

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = { BottomNavigationBar(navController, items) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable("home") { HomeView() }
            composable("forYou") { ForYouView() }
            composable("myList") { MyListView() }
            composable("profile") { ProfileView() }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, items: List<BottomNavItem>) {
    val backgroundColor = BaseBlack
    val selectedColor = BaseYellow
    val unselectedColor = Color(0xFFFFFFFF)

    Column {
        Divider(color = BaseYellow, thickness = 1.dp)

        NavigationBar(containerColor = backgroundColor, tonalElevation = 0.dp) {
            val currentRoute = currentRoute(navController)

            items.forEach { item ->
                val selected = currentRoute == item.route

                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                    selected = selected,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors =
                        NavigationBarItemDefaults.colors(
                            selectedIconColor = selectedColor,
                            unselectedIconColor = unselectedColor,
                            selectedTextColor = selectedColor,
                            unselectedTextColor = unselectedColor,
                            indicatorColor = Color.Transparent
                        )
                )
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)
