package com.evduzeni.planner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evduzeni.planner.ui.components.PromotionalNoticeDialog
import com.evduzeni.planner.ui.screens.OnboardingScreen
import com.evduzeni.planner.ui.screens.PlanScreen
import com.evduzeni.planner.ui.screens.RoomsScreen
import com.evduzeni.planner.ui.screens.SettingsScreen
import com.evduzeni.planner.ui.screens.TodayScreen
import com.evduzeni.planner.ui.theme.BorderLine
import com.evduzeni.planner.ui.theme.EvDuzeniTheme
import com.evduzeni.planner.ui.theme.ForestInk
import com.evduzeni.planner.ui.theme.LightMint
import com.evduzeni.planner.ui.theme.SageGreen
import com.evduzeni.planner.ui.theme.SlateMuted
import com.evduzeni.planner.ui.theme.WarmCream
import com.evduzeni.planner.ui.viewmodel.PlannerViewModel

import android.content.Context
import com.evduzeni.planner.util.AppLanguageHelper

class MainActivity : ComponentActivity() {

    private val viewModel: PlannerViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            val savedLang = getSharedPreferences("evduzeni_quick_prefs", Context.MODE_PRIVATE)
                .getString("app_language", "") ?: ""
            AppLanguageHelper.applyLocale(this, savedLang)
        } catch (_: Exception) {}
        
        enableEdgeToEdge()

        setContent {
            EvDuzeniTheme {
                val prefs by viewModel.preferences.collectAsState()
                var currentTab by remember { mutableIntStateOf(0) }
                var showPromoDialog by remember { mutableStateOf(false) }

                    // First time launch after onboarding -> show promo dialog
                    LaunchedEffect(prefs.onboardingDone, prefs.promoDialogShown) {
                        if (prefs.onboardingDone && !prefs.promoDialogShown) {
                            showPromoDialog = true
                        }
                    }

                    if (!prefs.onboardingDone) {
                        OnboardingScreen(
                            viewModel = viewModel,
                            onFinished = {
                                if (!prefs.promoDialogShown) {
                                    showPromoDialog = true
                                }
                            }
                        )
                    } else {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            containerColor = WarmCream,
                            topBar = {
                                TopAppBar(
                                    title = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = stringResource(R.string.app_short_name),
                                                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = ForestInk
                                            )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = SageGreen,
                                            modifier = Modifier.clickable { showPromoDialog = true }
                                        ) {
                                            Text(
                                                text = stringResource(R.string.promo_badge),
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = WarmCream
                                )
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = Color.White,
                                tonalElevation = 2.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentTab == 0,
                                    onClick = { currentTab = 0 },
                                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                                    label = { Text(stringResource(R.string.nav_today)) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = SageGreen,
                                        selectedTextColor = SageGreen,
                                        indicatorColor = LightMint,
                                        unselectedIconColor = SlateMuted,
                                        unselectedTextColor = SlateMuted
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentTab == 1,
                                    onClick = { currentTab = 1 },
                                    icon = { Icon(Icons.Default.MeetingRoom, contentDescription = null) },
                                    label = { Text(stringResource(R.string.nav_rooms)) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = SageGreen,
                                        selectedTextColor = SageGreen,
                                        indicatorColor = LightMint,
                                        unselectedIconColor = SlateMuted,
                                        unselectedTextColor = SlateMuted
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentTab == 2,
                                    onClick = { currentTab = 2 },
                                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                                    label = { Text(stringResource(R.string.nav_plan)) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = SageGreen,
                                        selectedTextColor = SageGreen,
                                        indicatorColor = LightMint,
                                        unselectedIconColor = SlateMuted,
                                        unselectedTextColor = SlateMuted
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentTab == 3,
                                    onClick = { currentTab = 3 },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                    label = { Text(stringResource(R.string.nav_settings)) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = SageGreen,
                                        selectedTextColor = SageGreen,
                                        indicatorColor = LightMint,
                                        unselectedIconColor = SlateMuted,
                                        unselectedTextColor = SlateMuted
                                    )
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            when (currentTab) {
                                0 -> TodayScreen(viewModel = viewModel)
                                1 -> RoomsScreen(viewModel = viewModel)
                                2 -> PlanScreen(viewModel = viewModel)
                                3 -> SettingsScreen(viewModel = viewModel)
                            }
                        }
                    }

                    if (showPromoDialog) {
                        PromotionalNoticeDialog(
                            onDismiss = {
                                viewModel.setPromoDialogShown(true)
                                showPromoDialog = false
                            }
                        )
                    }
                }
            }
        }
    }
}


