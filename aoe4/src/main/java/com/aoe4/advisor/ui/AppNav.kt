package com.aoe4.advisor.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aoe4.advisor.ui.screens.CivDetailScreen
import com.aoe4.advisor.ui.screens.CivListScreen
import com.aoe4.advisor.ui.screens.TierListScreen

private object Routes {
    const val LIST = "list"
    const val TIER = "tier"
    const val DETAIL = "detail" // detail/{civId}
}

@Composable
fun Aoe4App() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.LIST) {
        composable(Routes.LIST) {
            CivListScreen(
                onCivClick = { id -> nav.navigate("${Routes.DETAIL}/$id") },
                onTierListClick = { nav.navigate(Routes.TIER) }
            )
        }
        composable(Routes.TIER) {
            TierListScreen(
                onCivClick = { id -> nav.navigate("${Routes.DETAIL}/$id") },
                onBack = { nav.popBackStack() }
            )
        }
        composable(
            route = "${Routes.DETAIL}/{civId}",
            arguments = listOf(navArgument("civId") { type = NavType.StringType })
        ) { entry ->
            CivDetailScreen(
                civId = entry.arguments?.getString("civId").orEmpty(),
                onBack = { nav.popBackStack() }
            )
        }
    }
}
