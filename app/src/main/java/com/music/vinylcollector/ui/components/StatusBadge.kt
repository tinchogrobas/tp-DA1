package com.music.vinylcollector.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.music.vinylcollector.domain.model.VinylStatus
import com.music.vinylcollector.ui.theme.StatusLent
import com.music.vinylcollector.ui.theme.StatusOwned
import com.music.vinylcollector.ui.theme.StatusWanted

/**
 * Badge de color que indica el estado del disco.
 * Verde = lo tengo, Amarillo = lo quiero, Rojo = prestado.
 */
@Composable
fun StatusBadge(status: VinylStatus, modifier: Modifier = Modifier) {
    val (color, text) = when (status) {
        VinylStatus.OWNED -> StatusOwned to "Lo tengo"
        VinylStatus.WANTED -> StatusWanted to "Lo quiero"
        VinylStatus.LENT -> StatusLent to "Prestado"
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = Color.White,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.85f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}
