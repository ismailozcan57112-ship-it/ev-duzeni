package com.evduzeni.planner.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evduzeni.planner.R
import com.evduzeni.planner.ui.theme.ForestInk
import com.evduzeni.planner.ui.theme.LightMint
import com.evduzeni.planner.ui.theme.SageGreen
import com.evduzeni.planner.ui.theme.SlateMuted
import com.evduzeni.planner.ui.theme.WarmCream
import com.evduzeni.planner.ui.viewmodel.PlannerViewModel

@Composable
fun OnboardingScreen(
    viewModel: PlannerViewModel,
    onFinished: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.setNotificationsEnabled(isGranted)
    }

    val slides = listOf(
        Triple(
            Icons.Default.Home,
            stringResource(R.string.onboarding_1_title),
            stringResource(R.string.onboarding_1_desc)
        ),
        Triple(
            Icons.Default.CalendarMonth,
            stringResource(R.string.onboarding_2_title),
            stringResource(R.string.onboarding_2_desc)
        ),
        Triple(
            Icons.Default.Notifications,
            stringResource(R.string.onboarding_3_title),
            stringResource(R.string.onboarding_3_desc)
        )
    )

    val currentSlide = slides[step]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
            .padding(24.dp)
    ) {
        // Skip button
        TextButton(
            onClick = {
                viewModel.setOnboardingDone(true)
                onFinished()
            },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Text(stringResource(R.string.btn_skip_setup), color = SlateMuted)
        }

        // Center card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Indicator
                Text(
                    text = "${step + 1} / 3",
                    style = MaterialTheme.typography.labelMedium,
                    color = SlateMuted
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(LightMint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = currentSlide.first,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = SageGreen
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = currentSlide.second,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestInk,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentSlide.third,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SlateMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Progress dots
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 0..2) {
                        Box(
                            modifier = Modifier
                                .size(if (i == step) 20.dp else 8.dp, 8.dp)
                                .clip(CircleShape)
                                .background(if (i == step) SageGreen else LightMint)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (step < 2) {
                    Button(
                        onClick = { step++ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SageGreen)
                    ) {
                        Text(stringResource(R.string.btn_continue), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    viewModel.setNotificationsEnabled(true)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SageGreen)
                        ) {
                            Text(stringResource(R.string.btn_enable_notifications), fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                viewModel.setupSamplePlan()
                                onFinished()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestInk)
                        ) {
                            Text(stringResource(R.string.btn_start_with_starter_plan), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
