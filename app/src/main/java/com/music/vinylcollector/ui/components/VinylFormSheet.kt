package com.music.vinylcollector.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.music.vinylcollector.domain.model.Genre
import com.music.vinylcollector.domain.model.Vinyl
import com.music.vinylcollector.domain.model.VinylStatus

/**
 * Bottom sheet para crear/editar un disco.
 * Valida que título y artista no estén vacíos antes de permitir guardar.
 * Si recibe un vinyl != null, funciona en modo edición.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VinylFormSheet(
    vinyl: Vinyl? = null,
    onDismiss: () -> Unit,
    onSave: (Vinyl) -> Unit
) {
    val isEditing = vinyl != null

    var title by remember { mutableStateOf(vinyl?.title ?: "") }
    var artist by remember { mutableStateOf(vinyl?.artist ?: "") }
    var yearText by remember { mutableStateOf(vinyl?.year?.toString() ?: "") }
    var genre by remember { mutableStateOf(vinyl?.genre ?: Genre.ROCK) }
    var status by remember { mutableStateOf(vinyl?.status ?: VinylStatus.OWNED) }
    var rating by remember { mutableStateOf(vinyl?.rating ?: 3) }
    var notes by remember { mutableStateOf(vinyl?.notes ?: "") }
    var coverUrl by remember { mutableStateOf(vinyl?.coverUrl ?: "") }

    // Validación
    var titleError by remember { mutableStateOf(false) }
    var artistError by remember { mutableStateOf(false) }

    // Dropdown de género expandido
    var genreExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isEditing) "Editar disco" else "Nuevo disco",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Título (obligatorio)
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = false
                },
                label = { Text("Título del álbum *") },
                isError = titleError,
                supportingText = if (titleError) {{ Text("El título es obligatorio") }} else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = vinylTextFieldColors()
            )

            // Artista (obligatorio)
            OutlinedTextField(
                value = artist,
                onValueChange = {
                    artist = it
                    artistError = false
                },
                label = { Text("Artista *") },
                isError = artistError,
                supportingText = if (artistError) {{ Text("El artista es obligatorio") }} else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = vinylTextFieldColors()
            )

            // Año
            OutlinedTextField(
                value = yearText,
                onValueChange = { if (it.length <= 4) yearText = it.filter { c -> c.isDigit() } },
                label = { Text("Año de lanzamiento") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = vinylTextFieldColors()
            )

            // Género — dropdown
            ExposedDropdownMenuBox(
                expanded = genreExpanded,
                onExpandedChange = { genreExpanded = it }
            ) {
                OutlinedTextField(
                    value = genre.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Género") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genreExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = vinylTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = genreExpanded,
                    onDismissRequest = { genreExpanded = false }
                ) {
                    Genre.entries.forEach { g ->
                        DropdownMenuItem(
                            text = { Text(g.displayName) },
                            onClick = {
                                genre = g
                                genreExpanded = false
                            }
                        )
                    }
                }
            }

            // Estado — dropdown
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = it }
            ) {
                OutlinedTextField(
                    value = status.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = vinylTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    VinylStatus.entries.forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s.displayName) },
                            onClick = {
                                status = s
                                statusExpanded = false
                            }
                        )
                    }
                }
            }

            // Rating
            Column {
                Text(
                    text = "Calificación",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                VinylRatingBar(
                    rating = rating,
                    onRatingChanged = { rating = it },
                    size = 32
                )
            }

            // URL de la tapa
            OutlinedTextField(
                value = coverUrl,
                onValueChange = { coverUrl = it },
                label = { Text("URL de la tapa") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = vinylTextFieldColors()
            )

            // Notas
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas / Comentarios") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth(),
                colors = vinylTextFieldColors()
            )

            // Botón de guardar
            Button(
                onClick = {
                    titleError = title.isBlank()
                    artistError = artist.isBlank()

                    if (!titleError && !artistError) {
                        onSave(
                            Vinyl(
                                id = vinyl?.id ?: 0,
                                title = title.trim(),
                                artist = artist.trim(),
                                year = yearText.toIntOrNull() ?: 2024,
                                genre = genre,
                                status = status,
                                rating = rating,
                                notes = notes.trim(),
                                coverUrl = coverUrl.trim()
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = if (isEditing) "Guardar cambios" else "Agregar a la colección",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
private fun vinylTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedLabelColor = MaterialTheme.colorScheme.primary
)
