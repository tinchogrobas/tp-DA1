package com.music.vinylcollector.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.music.vinylcollector.ui.theme.Amber
import com.music.vinylcollector.ui.theme.WarmGray

/**
 * Rating con íconos de vinilo en vez de estrellas genéricas.
 * Soporta modo interactivo (formulario) y de solo lectura (cards).
 */
@Composable
fun VinylRatingBar(
    rating: Int,
    onRatingChanged: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
    size: Int = 24
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Album else Icons.Outlined.Album,
                contentDescription = "Rating $i",
                tint = if (i <= rating) Amber else WarmGray,
                modifier = Modifier
                    .size(size.dp)
                    .then(
                        if (onRatingChanged != null) Modifier.clickable { onRatingChanged(i) }
                        else Modifier
                    )
            )
        }
    }
}
