package com.hescul.urgent.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.hescul.urgent.ui.theme.UrgentTheme
import com.hescul.urgent.ui.versatile.UrgentTopBar
import com.hescul.urgent.R
import com.hescul.urgent.navigation.HomeScreens
import com.hescul.urgent.ui.screens.home.doctor.DoctorScreen
import com.hescul.urgent.ui.screens.home.patient.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    patientViewModel: PatientViewModel,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        patientViewModel.onLaunch()
    }
    val patientListState = rememberLazyListState()
    val scaffoldState = rememberScaffoldState()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
        sheetBackgroundColor = MaterialTheme.colors.surface,
        sheetContent = {
            SubscribeSheet(
                patientViewModel = patientViewModel,
                onSubscribeRequest = {
                    patientViewModel.onSubscribeRequest(
                        onDeviceSatisfied = {
                            coroutineScope.launch {
                                sheetState.animateTo(
                                    targetValue = ModalBottomSheetValue.Hidden,
                                    anim = tween(durationMillis = 800)
                                )
                            }
                        }
                    )
                }
            )
        }
    ) {
        val homeNavController = rememberAnimatedNavController()
        Scaffold(
            scaffoldState = scaffoldState,
            modifier = modifier.fillMaxSize(),
            topBar = {
                UrgentTopBar(
                    title = stringResource(id = when(homeViewModel.currentScreen) {
                        HomeScreens.Doctor.route -> R.string.ui_homeScreen_doctorScreenTitle
                        else -> R.string.ui_homeScreen_patientScreenTitle
                    }),
                    showNavigateBack = false,
                    showRightAction = false
                )
            },
            floatingActionButton = {
                HomeFAB(
                    onCLick = {
                        coroutineScope.launch {
                            sheetState.animateTo(
                                targetValue = ModalBottomSheetValue.Expanded,
                                anim = tween(durationMillis = 800)
                            )
                        }
                    },
                    extended = patientListState.firstVisibleItemScrollOffset == 0,
                    enabled = !patientViewModel.isProgressing
                )
            },
            bottomBar = {
                HomeBottomBar(
                    homeNavController = homeNavController,
                    screens = listOf(HomeScreens.Patient, HomeScreens.Doctor),
                    onScreenChange = homeViewModel::onCurrentScreenChange,
                    enabled = !patientViewModel.isProgressing
                )
            }
        ) { contentPadding ->
            AnimatedNavHost(
                navController = homeNavController,
                startDestination = HomeScreens.Patient.route,
                modifier = Modifier.padding(contentPadding)
            ) {
                composable(
                    HomeScreens.Patient.route,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis = 600)
                        )
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(durationMillis = 400))
                    }
                ) { PatientScreen(patientViewModel, patientListState) }
                composable(
                    HomeScreens.Doctor.route,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(durationMillis = 600)
                        )
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(durationMillis = 400))
                    }
                ) { DoctorScreen() }
            }
        }
    }
}

@Preview("Home Screen")
@Composable
fun PreviewHomeScreen() {
    val homeViewModel = HomeViewModel()
    val patientViewModel = PatientViewModel(LocalContext.current)
    UrgentTheme {
        Surface {
            HomeScreen(
                homeViewModel = homeViewModel,
                patientViewModel = patientViewModel,
            )
        }
    }
}