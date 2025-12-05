package com.app.shortlovers.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.app.shortlovers.ui.theme.BaseBlack
import com.app.shortlovers.ui.theme.BaseYellow
import com.app.shortlovers.ui.view.beranda.BerandaView
import com.app.shortlovers.ui.view.daftarSaya.DaftarSayaView
import com.app.shortlovers.ui.view.profil.ProfilView
import com.app.shortlovers.ui.view.untukKamu.UntukKamuView

@Composable
fun MainView(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem("home", "Beranda", Icons.Default.Home),
        BottomNavItem("forYou", "Untuk Kamu", Icons.Default.Favorite),
        BottomNavItem("myList", "Daftar Saya", Icons.Default.List),
        BottomNavItem("profile", "Profil", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, items) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
        ) {
            composable("home") { BerandaView() }
            composable("forYou") { UntukKamuView() }
            composable("myList") { DaftarSayaView() }
            composable("profile") { ProfilView() }
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

        NavigationBar(
            containerColor = backgroundColor,
            tonalElevation = 0.dp
        ) {
            val currentRoute = currentRoute(navController)

            items.forEach { item ->
                val selected = currentRoute == item.route

                NavigationBarItem(
                    icon = {
                        Icon(
                            item.icon,
                            contentDescription = item.label
                        )
                    },
                    label = { Text(item.label) },
                    selected = selected,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
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