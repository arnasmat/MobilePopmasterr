package com.example.mobilepopmasterr.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobilepopmasterr.ui.theme.WarningYellow
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mobilepopmasterr.ui.theme.WarningRed

@Composable
private fun GenericWarning(
    message: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconModifier: Modifier = Modifier,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Warning",
            tint = Color.Black,
            modifier = iconModifier,
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = message,
            color = Color.Black,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
fun SevereWarning(
    message: String,
    modifier: Modifier = Modifier,
) {
    GenericWarning(message, icon =Icons.Filled.Warning, iconModifier = Modifier.size(28.dp), backgroundColor = WarningRed, modifier = modifier)
}

@Composable
fun MediumWarning(
    message: String,
    modifier: Modifier = Modifier,
) {
    GenericWarning(message, icon = Icons.Rounded.Warning, iconModifier = Modifier.size(24.dp), backgroundColor = WarningYellow, modifier = modifier)
}


@Preview
@Composable
fun MediumWarningPreview() {
    MediumWarning(
        message = "This is a medium warning",
    )
}

@Preview
@Composable
fun SevereWarningPreview() {
    SevereWarning(
        message = "This is a severe warning",
    )
}