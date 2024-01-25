package com.flixclusive.feature.tv.player.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.IconButtonDefaults
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.flixclusive.core.ui.common.util.onMediumEmphasis
import com.flixclusive.core.ui.tv.util.getGlowRadialGradient
import com.flixclusive.core.ui.tv.util.glowOnFocus
import com.flixclusive.core.ui.tv.util.useLocalDirectionalFocusRequester
import com.flixclusive.core.ui.player.R as PlayerR

internal val PlaybackButtonsSize = 24.dp

@OptIn(ExperimentalComposeUiApi::class, ExperimentalTvMaterial3Api::class)
@Composable
internal fun TopControls(
    modifier: Modifier = Modifier,
    isTvShow: Boolean,
    isLastEpisode: Boolean = false,
    title: String,
    extendControlsVisibility: () -> Unit,
    onNavigationIconClick: () -> Unit,
    onNextEpisodeClick: () -> Unit,
    onVideoSettingsClick: () -> Unit,
) {
    val directionalFocusRequester = useLocalDirectionalFocusRequester()
    val bottomFocusRequester = directionalFocusRequester.bottom
    val topFocusRequester = directionalFocusRequester.top

    val iconSurfaceSize = 34.dp
    val unfocusedContentColor = LocalContentColor.current.onMediumEmphasis()
    val largeRadialGradient = getGlowRadialGradient(unfocusedContentColor)

    var isArrowIconFocused by remember { mutableStateOf(false) }
    var isEpisodeIconFocused by remember { mutableStateOf(false) }
    var isSpeedometerIconFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
            .padding(top = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(top = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            IconButton(
                onClick = onNavigationIconClick,
                scale = IconButtonDefaults.scale(focusedScale = 1F),
                colors = IconButtonDefaults.colors(
                    containerColor = Color.Transparent,
                    contentColor = unfocusedContentColor,
                    focusedContainerColor = Color.Transparent,
                    focusedContentColor = Color.White
                ),
                modifier = Modifier
                    .focusRequester(topFocusRequester)
                    .onFocusChanged {
                        isArrowIconFocused = it.isFocused

                        if (it.isFocused) {
                            extendControlsVisibility()
                        }
                    }
                    .focusProperties {
                        down = bottomFocusRequester
                        left = FocusRequester.Cancel
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(iconSurfaceSize)
                        .glowOnFocus(
                            isFocused = isArrowIconFocused,
                            brush = largeRadialGradient
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier
                            .size(PlaybackButtonsSize),
                    )
                }
            }

            if (isTvShow) {
                Box(
                    modifier = Modifier.padding(start = 10.dp)
                ) {
                    IconButton(
                        enabled = !isLastEpisode,
                        onClick = onNextEpisodeClick,
                        scale = IconButtonDefaults.scale(focusedScale = 1F),
                        colors = IconButtonDefaults.colors(
                            containerColor = Color.Transparent,
                            contentColor = unfocusedContentColor,
                            focusedContainerColor = Color.Transparent,
                            focusedContentColor = Color.White
                        ),
                        modifier = Modifier
                            .onFocusChanged {
                                isEpisodeIconFocused = it.isFocused

                                if (it.isFocused) {
                                    extendControlsVisibility()
                                }
                            }
                            .focusProperties {
                                down = bottomFocusRequester
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(iconSurfaceSize)
                                .glowOnFocus(
                                    isFocused = isEpisodeIconFocused,
                                    brush = largeRadialGradient
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = PlayerR.drawable.round_skip_next_24),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(PlaybackButtonsSize),
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.padding(start = 5.dp)
            ) {
                IconButton(
                    onClick = onVideoSettingsClick,
                    shape = IconButtonDefaults.shape(CircleShape),
                    scale = IconButtonDefaults.scale(focusedScale = 1F),
                    colors = IconButtonDefaults.colors(
                        containerColor = Color.Transparent,
                        contentColor = unfocusedContentColor,
                        focusedContainerColor = Color.Transparent,
                        focusedContentColor = Color.White
                    ),
                    modifier = Modifier
                        .onFocusChanged {
                            isSpeedometerIconFocused = it.isFocused

                            if (it.isFocused) {
                                extendControlsVisibility()
                            }
                        }
                        .focusProperties {
                            right = if (!isSpeedometerIconFocused) FocusRequester.Cancel
                                else FocusRequester.Default

                            down = bottomFocusRequester
                        }
                ) {
                    val iconId = when (isSpeedometerIconFocused) {
                        true -> PlayerR.drawable.speedometer_filled
                        false -> PlayerR.drawable.speedometer
                    }

                    Box(
                        modifier = Modifier
                            .size(iconSurfaceSize)
                            .glowOnFocus(
                                isFocused = isSpeedometerIconFocused,
                                brush = largeRadialGradient
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = iconId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(PlaybackButtonsSize)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 8.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}