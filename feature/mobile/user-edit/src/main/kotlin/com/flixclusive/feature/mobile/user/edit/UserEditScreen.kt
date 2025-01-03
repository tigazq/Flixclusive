package com.flixclusive.feature.mobile.user.edit

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flixclusive.core.theme.FlixclusiveTheme
import com.flixclusive.core.ui.common.CommonTopBar
import com.flixclusive.core.ui.common.navigation.navigator.CommonUserEditNavigator
import com.flixclusive.core.ui.common.user.UserAvatar
import com.flixclusive.core.ui.common.user.UserAvatarDefaults.DefaultAvatarSize
import com.flixclusive.core.ui.common.util.adaptive.AdaptiveUiUtil.getAdaptiveDp
import com.flixclusive.core.ui.common.util.noIndicationClickable
import com.flixclusive.core.ui.common.util.showToast
import com.flixclusive.feature.mobile.user.destinations.UserAvatarSelectScreenDestination
import com.flixclusive.feature.mobile.user.edit.tweaks.data.DataTweak
import com.flixclusive.feature.mobile.user.edit.tweaks.identity.IdentityTweak
import com.flixclusive.feature.mobile.user.edit.tweaks.renderTweakUi
import com.flixclusive.model.database.User
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.collections.immutable.persistentListOf
import com.flixclusive.core.locale.R as LocaleR
import com.flixclusive.core.ui.common.R as UiCommonR

@Destination
@Composable
internal fun UserEditScreen(
    navigator: CommonUserEditNavigator,
    resultRecipient: ResultRecipient<UserAvatarSelectScreenDestination, Int>,
    userArg: User
) {
    val viewModel = hiltViewModel<UserEditViewModel>()
    val context = LocalContext.current
    var user by remember { mutableStateOf(userArg) }

    val tweaks = remember {
        persistentListOf(
            IdentityTweak(
                initialName = userArg.name,
                onSetupPin = { /*TODO: Implement User PIN */
                    context.showToast(
                        context.getString(
                            LocaleR.string.coming_soon_feature
                        )
                    )
                },
                onNameChange = {
                    user = user.copy(name = it)
                    viewModel.onEditUser(user = user)
                }
            ),
            DataTweak(
                onClearSearchHistory = { viewModel.onClearSearchHistory(user.id) },
                onDeleteProfile = { viewModel.onRemoveUser(user.id) },
                onClearLibraries = { /*TODO: Implement onClearLibraries*/
                    context.showToast(
                        context.getString(
                            LocaleR.string.coming_soon_feature
                        )
                    )
                },
            )
        )
    }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    resultRecipient.onNavResult { result ->
        if (result is NavResult.Value) {
            user = user.copy(image = result.value)
            viewModel.onEditUser(user = user)
        }
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = stringResource(LocaleR.string.edit_profile),
                onNavigate = { navigator.goBack() }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp),
                contentPadding = PaddingValues(getAdaptiveDp(10.dp)),
                modifier = Modifier.fillMaxSize()
                    .noIndicationClickable {
                        keyboardController?.hide()
                        focusManager.clearFocus(true)
                    }
            ) {
                item {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(getAdaptiveDp(5.dp))
                        ) {
                            UserAvatar(
                                user = user,
                                borderWidth = 0.dp,
                                shadowBlur = 0.dp,
                                shadowSpread = 0.dp,
                                modifier = Modifier
                                    .height(
                                        getAdaptiveDp(
                                            dp = (DefaultAvatarSize.value * 1.2).dp,
                                            increaseBy = 80.dp
                                        )
                                    )
                                    .aspectRatio(1F)
                            )
                        }

                        ChangeImageButton(
                            onClick = { navigator.openUserAvatarSelectScreen(selected = user.image) },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                        )
                    }
                }

                tweaks.forEach {
                    renderTweakUi(it)
                }
            }
        }
    }
}

@Composable
private fun ChangeImageButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonSize = getAdaptiveDp(30.dp, 10.dp)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(buttonSize)
            .background(
                color = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape
            )
            .clickable(
                indication = ripple(
                    bounded = false,
                    radius = buttonSize / 2
                ),
                interactionSource = null,
            ) {
                onClick()
            }
    ) {
        Icon(
            painter = painterResource(UiCommonR.drawable.edit),
            contentDescription = stringResource(LocaleR.string.change_avatar_content_desc),
            tint = MaterialTheme.colorScheme.surface,
            modifier = Modifier.size(
                getAdaptiveDp(
                    dp = 18.dp,
                    increaseBy = 10.dp
                ),
            )
        )
    }
}

@Suppress("OVERRIDE_DEPRECATION")
@SuppressLint("ComposableNaming")
@Preview
@Composable
private fun UserEditScreenBasePreview() {
    FlixclusiveTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            UserEditScreen(
                navigator = object: CommonUserEditNavigator {
                    override fun openUserAvatarSelectScreen(selected: Int) = Unit
                    override fun openUserPinSetupScreen() = Unit
                    override fun goBack() = Unit
                    override fun openHomeScreen() = Unit
                },
                resultRecipient = object : ResultRecipient<UserAvatarSelectScreenDestination, Int> {
                    @Composable
                    override fun onNavResult(listener: (NavResult<Int>) -> Unit) = Unit
                    @Composable
                    override fun onResult(listener: (Int) -> Unit) = Unit
                },
                userArg = User(
                    id = 0,
                    image = 0,
                    name = "John Doe"
                )
            )
        }
    }
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun UserEditScreenCompactLandscapePreview() {
    UserEditScreenBasePreview()
}

@Preview(device = "spec:parent=medium_tablet,orientation=portrait")
@Composable
private fun UserEditScreenMediumPortraitPreview() {
    UserEditScreenBasePreview()
}

@Preview(device = "spec:parent=medium_tablet,orientation=landscape")
@Composable
private fun UserEditScreenMediumLandscapePreview() {
    UserEditScreenBasePreview()
}

@Preview(device = "spec:width=1920dp,height=1080dp,dpi=160,orientation=portrait")
@Composable
private fun UserEditScreenExtendedPortraitPreview() {
    UserEditScreenBasePreview()
}

@Preview(device = "spec:width=1920dp,height=1080dp,dpi=160,orientation=landscape")
@Composable
private fun UserEditScreenExtendedLandscapePreview() {
    UserEditScreenBasePreview()
}