package com.flixclusive.feature.mobile.user.add.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flixclusive.core.theme.FlixclusiveTheme
import com.flixclusive.core.ui.common.util.adaptive.AdaptiveStylesUtil.getAdaptiveTextStyle
import com.flixclusive.core.ui.common.util.adaptive.AdaptiveUiUtil.getAdaptiveDp
import com.flixclusive.core.ui.common.util.adaptive.TextStyleMode
import com.flixclusive.core.ui.common.util.adaptive.TypographyStyle
import com.flixclusive.core.locale.R as LocaleR
import com.flixclusive.core.ui.common.R as UiCommonR

@Composable
internal fun AddUserScaffold(
    onBack: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
            .padding(
                horizontal = getAdaptiveDp(16.dp)
            )
    ) {
        FloatingBackButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
        )

        content()
    }
}

@Composable
private fun FloatingBackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .statusBarsPadding()
            .padding(vertical = getAdaptiveDp(6.dp))
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 6.dp)
                .clickable {
                    onClick()
                }
        ) {
            Icon(
                painter = painterResource(UiCommonR.drawable.left_arrow),
                contentDescription = stringResource(LocaleR.string.navigate_up),
                modifier = Modifier
                    .size(getAdaptiveDp(14.dp))
            )

            Text(
                text = stringResource(LocaleR.string.navigate_up),
                style = getAdaptiveTextStyle(
                    style = TypographyStyle.Label,
                    mode = TextStyleMode.NonEmphasized
                )
            )
        }
    }
}

@Preview
@Composable
private fun AddUserScaffoldBasePreview() {
    FlixclusiveTheme {
        Surface {
            AddUserScaffold(
                onBack = {}
            ) {}
        }
    }
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun AddUserScaffoldCompactLandscapePreview() {
    AddUserScaffoldBasePreview()
}

@Preview(device = "spec:parent=medium_tablet,orientation=portrait")
@Composable
private fun AddUserScaffoldMediumPortraitPreview() {
    AddUserScaffoldBasePreview()
}

@Preview(device = "spec:parent=medium_tablet,orientation=landscape")
@Composable
private fun AddUserScaffoldMediumLandscapePreview() {
    AddUserScaffoldBasePreview()
}

@Preview(device = "spec:width=1920dp,height=1080dp,dpi=160,orientation=portrait")
@Composable
private fun AddUserScaffoldExtendedPortraitPreview() {
    AddUserScaffoldBasePreview()
}

@Preview(device = "spec:width=1920dp,height=1080dp,dpi=160,orientation=landscape")
@Composable
private fun AddUserScaffoldExtendedLandscapePreview() {
    AddUserScaffoldBasePreview()
}