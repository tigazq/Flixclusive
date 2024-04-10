package com.flixclusive.core.ui.mobile.component.provider

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flixclusive.core.theme.FlixclusiveTheme
import com.flixclusive.gradle.entities.Author
import com.flixclusive.gradle.entities.Language
import com.flixclusive.gradle.entities.ProviderData
import com.flixclusive.gradle.entities.ProviderType
import com.flixclusive.gradle.entities.Status

@Composable
fun InstalledProviderCard(
    providerData: ProviderData,
    enabled: Boolean,
    isDraggable: Boolean,
    displacementOffset: Float?,
    openSettings: () -> Unit,
    uninstallProvider: () -> Unit,
    onToggleProvider: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val isBeingDragged = remember(displacementOffset) {
        displacementOffset != null
    }
    
    val isNotMaintenance = providerData.status != Status.Maintenance && enabled

    val color = if (isBeingDragged && isNotMaintenance && isDraggable) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = Modifier
            .graphicsLayer { translationY = if (!isDraggable) 0F else displacementOffset ?: 0f }
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            enabled = isNotMaintenance,
            onClick = onToggleProvider,
            interactionSource = interactionSource,
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = color,
                contentColor = contentColorFor(backgroundColor = color)
            ),
            border = if (isBeingDragged && isNotMaintenance || pressed)
                BorderStroke(
                    width = 2.dp,
                    color = contentColorFor(color)
                ) else null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        horizontal = 15.dp,
                        vertical = 10.dp
                    )
            ) {
                TopCardContent(
                    isDraggable = isDraggable,
                    providerData = providerData,
                )

                Divider(
                    thickness = 0.5.dp,
                    modifier = Modifier
                        .padding(top = 15.dp)
                )

                BottomCardContent(
                    providerData = providerData,
                    enabled = enabled,
                    openSettings = openSettings,
                    unloadProvider = uninstallProvider,
                    toggleUsage = onToggleProvider
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProviderCardPreview() {
    val providerData = ProviderData(
        authors = listOf(Author("FLX")),
        repositoryUrl = null,
        buildUrl = null,
        changelog = null,
        changelogMedia = null,
        versionName = "1.0.0",
        versionCode = 10000,
        description = null,
        iconUrl = null,
        language = Language.Multiple,
        name = "123Movies",
        providerType = ProviderType.All,
        status = Status.Working
    )

    FlixclusiveTheme {
        Surface {
            InstalledProviderCard(
                providerData = providerData,
                enabled = true,
                isDraggable = true,
                displacementOffset = null,
                openSettings = { /*TODO*/ },
                uninstallProvider = { /*TODO*/ }) {

            }
        }
    }
}