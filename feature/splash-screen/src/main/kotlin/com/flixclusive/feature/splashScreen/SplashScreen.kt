package com.flixclusive.feature.splashScreen

import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flixclusive.core.network.util.Resource
import com.flixclusive.core.ui.common.navigation.navigator.SplashScreenNavigator
import com.flixclusive.core.ui.common.util.showToast
import com.flixclusive.data.configuration.UpdateStatus
import com.flixclusive.feature.splashScreen.component.ErrorDialog
import com.flixclusive.feature.splashScreen.component.LoadingTag
import com.flixclusive.feature.splashScreen.screen.consent.ConsentScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.delay
import com.flixclusive.core.locale.R as LocaleR

internal const val APP_TAG_KEY = "tag_image"
internal const val ENTER_DELAY = 800
internal const val EXIT_DELAY = 600

internal val PaddingHorizontal = 8.dp
internal val TagSize = 300.dp

@OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalSharedTransitionApi::class
)
@Destination
@Composable
internal fun SplashScreen(
    navigator: SplashScreenNavigator
) {
    val context = LocalContext.current
    val viewModel: SplashScreenViewModel = hiltViewModel()

    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
    val onBoardingPreferences by viewModel.onBoardingPreferences.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val updateStatus by viewModel.appUpdateCheckerUseCase.updateStatus.collectAsStateWithLifecycle()
    val configurationStatus by viewModel.configurationStatus.collectAsStateWithLifecycle()
    val userLoggedIn by viewModel.userLoggedIn.collectAsStateWithLifecycle()
    val noUsersFound by viewModel.noUsersFound.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = PaddingHorizontal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SharedTransitionLayout {
            AnimatedContent(
                targetState = onBoardingPreferences.isFirstTimeUserLaunch_,
                transitionSpec = {
                    EnterTransition.None togetherWith ExitTransition.None
                },
                label = "splash_screen"
            ) { state ->
                if (state) {
                    ConsentScreen(
                        animatedScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        onAgree = { isOptingIn ->
                            with(viewModel) {
                                updateOnBoardingPreferences {
                                    it.copy(isFirstTimeUserLaunch_ = false)
                                }

                                updateSettings(
                                    appSettings.copy(
                                        isSendingCrashLogsAutomatically = isOptingIn
                                    )
                                )
                            }
                        }
                    )
                } else {
                    var hasErrors by rememberSaveable { mutableStateOf(false) }
                    var isLoading by rememberSaveable { mutableStateOf(false) }
                    var isDoneLoading by rememberSaveable { mutableStateOf(false) }
                    var areAllPermissionsGranted by rememberSaveable {
                        mutableStateOf(context.hasAllPermissionGranted())
                    }

                    LoadingTag(
                        isLoading = isLoading,
                        animatedScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )

                    LaunchedEffect(true) {
                        if (!isLoading) {
                            delay(3000L)
                            isLoading = true
                            delay(3000L)
                            isDoneLoading = true
                        }
                    }

                    LaunchedEffect(updateStatus, configurationStatus, areAllPermissionsGranted, isDoneLoading, userLoggedIn) {
                        if (areAllPermissionsGranted && isDoneLoading) {
                            val hasAutoUpdate = appSettings.isUsingAutoUpdateAppFeature
                            val isAppOutdated = updateStatus is UpdateStatus.Outdated
                            val isAppUpdated = updateStatus is UpdateStatus.UpToDate
                            val updateHasErrors = updateStatus is UpdateStatus.Error
                            val isConfigFetched = configurationStatus is Resource.Success
                            val configHasErrors = configurationStatus is Resource.Failure
                            val isHomeScreenReady = uiState is SplashScreenUiState.Okay
                            val hasOldUserSession = userLoggedIn != null

                            val isNavigatingToHome = ((isAppUpdated && hasAutoUpdate) || isConfigFetched)
                                    && isHomeScreenReady
                                    && hasOldUserSession
                            hasErrors = (updateHasErrors && hasAutoUpdate) || configHasErrors

                            if (isAppOutdated && hasAutoUpdate) {
                                with(viewModel.appUpdateCheckerUseCase) {
                                    navigator.openUpdateScreen(
                                        newVersion = newVersion ?: throw NullPointerException("App's new version is null"),
                                        updateInfo = updateInfo,
                                        updateUrl = updateUrl ?: throw NullPointerException("App's new update URL is null"),
                                        isComingFromSplashScreen = true,
                                    )
                                }
                            }
                            else if (noUsersFound) {
                                navigator.openAddProfileScreen(isInitializing = true)
                            }
                            else if (!hasOldUserSession) {
                                navigator.openProfilesScreenFromSplashScreen()
                            }
                            else if (isNavigatingToHome) {
                                if (updateHasErrors) {
                                    val message = "${context.getString(LocaleR.string.failed_to_get_app_updates)}: ${updateStatus.errorMessage?.asString(context)}"
                                    context.showToast(message = message)
                                }

                                navigator.openHomeScreen()
                            }
                        }
                    }

                    if (hasErrors) {
                        val (title, errorMessage) = if (updateStatus is UpdateStatus.Error)
                            stringResource(LocaleR.string.failed_to_get_app_updates) to updateStatus.errorMessage
                        else stringResource(LocaleR.string.something_went_wrong) to (configurationStatus as Resource.Failure).error

                        ErrorDialog(
                            title = title,
                            description = errorMessage!!.asString(),
                            dismissButtonLabel = stringResource(LocaleR.string.close_label),
                            onDismiss = navigator::openHomeScreen
                        )
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && isDoneLoading) {
                        val notificationsPermissionState = rememberPermissionState(
                            android.Manifest.permission.POST_NOTIFICATIONS
                        )

                        val textToShow = if (notificationsPermissionState.status.shouldShowRationale) {
                            stringResource(LocaleR.string.notification_persist_request_message)
                        } else {
                            stringResource(LocaleR.string.notification_request_message)
                        }

                        if (!notificationsPermissionState.status.isGranted) {
                            ErrorDialog(
                                title = stringResource(LocaleR.string.splash_notice_permissions_header),
                                description = textToShow,
                                dismissButtonLabel = stringResource(LocaleR.string.allow),
                                onDismiss = notificationsPermissionState::launchPermissionRequest
                            )
                        }

                        if (notificationsPermissionState.status.isGranted && !areAllPermissionsGranted) {
                            areAllPermissionsGranted = true
                        }
                    } else areAllPermissionsGranted = true
                }
            }
        }
    }

}