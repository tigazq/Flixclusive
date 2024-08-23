package com.flixclusive.feature.mobile.provider.test

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastSumBy
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.flixclusive.core.theme.FlixclusiveTheme
import com.flixclusive.core.ui.common.CommonNoticeDialog
import com.flixclusive.core.ui.common.navigation.GoBackAction
import com.flixclusive.core.ui.common.util.DummyDataForPreview.getDummyProviderData
import com.flixclusive.core.ui.common.util.buildImageUrl
import com.flixclusive.domain.provider.test.TestStage.Idle.Companion.isIdle
import com.flixclusive.feature.mobile.provider.test.component.ButtonControllerDivider
import com.flixclusive.feature.mobile.provider.test.component.ProviderTestScreenTopBar
import com.flixclusive.feature.mobile.provider.test.component.TestResultCard
import com.flixclusive.feature.mobile.provider.test.component.TestScreenHeader
import com.flixclusive.gradle.entities.ProviderData
import com.ramcosta.composedestinations.annotation.Destination
import com.flixclusive.core.util.R as UtilR

private const val THE_MATRIX_NEO_BACKDROP = "/wkzeNsJjQBYCzkbiI2jxWnlrwR6.jpg"

data class ProviderTestScreenNavArgs(
    val providers: ArrayList<ProviderData>
)

@Composable
private fun Modifier.drawScrimOnForeground(
    scrimColor: Color = MaterialTheme.colorScheme.surface
) = drawWithCache {
        onDrawWithContent {
            drawContent()
            drawRect(
                brush = Brush.verticalGradient(
                    0F to scrimColor.copy(alpha = 0.8F),
                    0.8F to scrimColor
                )
            )
        }
    }

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Destination(
    navArgsDelegate = ProviderTestScreenNavArgs::class
)
@Composable
fun ProviderTestScreen(
    navigator: GoBackAction,
    args: ProviderTestScreenNavArgs
) {
     val viewModel = hiltViewModel<ProviderTestScreenViewModel>()

    val stage by viewModel.testProviderUseCase.testStage.collectAsStateWithLifecycle()
    val testJobState by viewModel.testProviderUseCase.testJobState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val localDensity = LocalDensity.current
    var headerHeight by remember { mutableStateOf(0.dp) }
    var headerHeightPx by remember { mutableFloatStateOf(0F) }

    val listState = rememberLazyListState()
    val topBarBackgroundAlpha by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0) {
                (listState.firstVisibleItemScrollOffset.toFloat() / headerHeightPx).coerceIn(0F, 1F)
            } else 1F
        }
    }

    val topBarBackgroundColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.surface.copy(alpha = topBarBackgroundAlpha),
        label = ""
    )

    LaunchedEffect(stage, viewModel.testProviderUseCase.results.size) {
        val resultsAreNotEmpty = viewModel.testProviderUseCase.results.isNotEmpty()
        if (stage.isIdle && resultsAreNotEmpty) {
            val totalTestsPerformed
                = viewModel.testProviderUseCase.results
                    .fastSumBy { it.outputs.size }
            val totalTestsPassed
                = viewModel.testProviderUseCase.results
                    .fastSumBy { result ->
                        result.outputs.count { it.isSuccess }
                    }

            snackbarHostState.showSnackbar(
                message = context.getString(UtilR.string.test_providers_completed, "$totalTestsPassed/$totalTestsPerformed"),
                duration = SnackbarDuration.Indefinite,
                withDismissAction = true
            )
        } else if (!stage.isIdle) {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            ProviderTestScreenTopBar(
                onNavigationIconClick = navigator::goBack,
                onTestPreferencesClick = {},
                modifier = Modifier
                    .background(topBarBackgroundColor)
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 10.dp),
            state = listState
        ) {
            item {
                Box {
                    AnimatedVisibility(
                        visible = headerHeight > 0.dp,
                        enter = fadeIn(initialAlpha = 0.3F),
                        exit = fadeOut(targetAlpha = 0.3F)
                    ) {
                        AsyncImage(
                            model = context.buildImageUrl(
                                imagePath = THE_MATRIX_NEO_BACKDROP,
                                imageSize = "w600_and_h900_multi_faces"
                            ),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(headerHeight)
                                .drawScrimOnForeground()
                        )
                    }

                    Column(
                        modifier = Modifier.onGloballyPositioned { coordinates ->
                            headerHeightPx = coordinates.size.height.toFloat()
                            headerHeight = with(localDensity) { coordinates.size.height.toDp() }
                        }
                    ) {
                        TestScreenHeader(stage = stage)

                        ButtonControllerDivider(
                            testJobState = testJobState,
                            onStop = viewModel::stopTests,
                            onPause = viewModel::pauseTests,
                            onResume = viewModel::resumeTests,
                            onStart = {
                                viewModel.startTests(args.providers)
                            },
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.padding(10.dp))
            }

            items(viewModel.testProviderUseCase.results) { data ->
                TestResultCard(
                    isExpanded = true,
                    testResult = data,
                    onToggle = { /*TODO*/ },
                    showFullLog = { /*TODO*/ }
                )
            }
        }
    }

    if (viewModel.showRepetitiveTestWarning) {
        CommonNoticeDialog(
            label = stringResource(id = UtilR.string.repetitive_test_warning_label),
            description = stringResource(id = UtilR.string.repetitive_test_warning_description),
            confirmButtonLabel = stringResource(id = UtilR.string.skip),
            dismissButtonLabel = stringResource(id = UtilR.string.re_test),
            onConfirm = {
                viewModel.startTests(
                    providers = args.providers,
                    skipTestedProviders = true
                )
            },
            onDismiss = {
                viewModel.startTests(
                    providers = args.providers,
                    skipTestedProviders = false
                )
            },
        )
    }
}

@Preview
@Composable
private fun ProviderTestScreenPreview() {
    FlixclusiveTheme {
        Surface {
            ProviderTestScreen(
                navigator = object : GoBackAction {
                    override fun goBack() {}
                },
                args = ProviderTestScreenNavArgs(
                    arrayListOf(getDummyProviderData())
                )
            )
        }
    }
}
