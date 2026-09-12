package com.mrapps.bloodmatch.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Project-wide primary button: blood-red fill, white text.
 * Disabled state uses darker muted tones + a border so it stays visible.
 */
@Composable
fun BloodButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = BloodRed,
            contentColor = BloodOnPrimary,
            disabledContainerColor = BloodDisabled,
            disabledContentColor = BloodDisabledText
        ),
        border = if (enabled) null else BorderStroke(1.dp, BloodRedDark),
        content = content
    )
}
