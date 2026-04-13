package com.music.vinylcollector.ui.screens.collection

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.music.vinylcollector.di.AppContainer
import com.music.vinylcollector.domain.model.Vinyl
import com.music.vinylcollector.ui.components.*

/**
 * Pantalla principal — lista la colección de vinilos con búsqueda,
 * filtros, swipe-to-delete con undo, y FAB para agregar.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CollectionScreen(
    container: AppContainer,
    onNavigateToAbout: () -> Unit,
    viewModel: CollectionViewModel = viewModel(
        factory = CollectionViewModel.Factory(container.vinylRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Bottom sheet de formulario
    var showForm by remember { mutableStateOf(false) }
    var editingVinyl by remember { mutableStateOf<Vinyl?>(null) }

    // Diálogo de confirmación de eliminación
    var vinylToDelete by remember { mutableStateOf<Vinyl?>(null) }

    // Filtros
    var showFilters by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }

    // Snackbar para undo de eliminación
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    AnimatedVisibility(
                        visible = !isSearchExpanded,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = "Vinyl Collector",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    AnimatedSearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange,
                        isExpanded = isSearchExpanded,
                        onExpandChange = { isSearchExpanded = it },
                        modifier = if (isSearchExpanded) Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                        else Modifier
                    )

                    if (!isSearchExpanded) {
                        IconButton(onClick = { showFilters = !showFilters }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filtros",
                                tint = if (showFilters || uiState.selectedGenre != null || uiState.selectedStatus != null)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onBackground
                            )
                        }

                        IconButton(onClick = onNavigateToAbout) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Acerca de",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingVinyl = null
                    showForm = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar disco")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filtros animados
            AnimatedVisibility(
                visible = showFilters,
                enter = expandVertically(tween(300)) + fadeIn(),
                exit = shrinkVertically(tween(300)) + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "Género",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    GenreFilterChips(
                        selectedGenre = uiState.selectedGenre,
                        onGenreSelected = viewModel::onGenreFilterChange
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Estado",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    StatusFilterChips(
                        selectedStatus = uiState.selectedStatus,
                        onStatusSelected = viewModel::onStatusFilterChange
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Contenido principal
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                uiState.filteredVinyls.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (uiState.vinyls.isEmpty()) "Tu colección está vacía"
                                       else "No se encontraron discos",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (uiState.vinyls.isEmpty()) "Tocá + para agregar tu primer vinilo"
                                       else "Probá con otros filtros o términos de búsqueda",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("vinyl_list"),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Contador de resultados
                        item {
                            Text(
                                text = "${uiState.filteredVinyls.size} disco${if (uiState.filteredVinyls.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                            )
                        }

                        items(
                            items = uiState.filteredVinyls,
                            key = { it.id }
                        ) { vinyl ->
                            // Swipe to delete con diálogo de confirmación
                            val dismissState = rememberDismissState(
                                confirmValueChange = { dismissValue ->
                                    if (dismissValue == DismissValue.DismissedToStart) {
                                        vinylToDelete = vinyl
                                        false
                                    } else false
                                }
                            )

                            SwipeToDismiss(
                                state = dismissState,
                                background = { SwipeDeleteBackground() },
                                directions = setOf(DismissDirection.EndToStart),
                                dismissContent = {
                                    VinylCard(
                                        vinyl = vinyl,
                                        onClick = {
                                            editingVinyl = vinyl
                                            showForm = true
                                        }
                                    )
                                },
                                modifier = Modifier.animateItemPlacement()
                            )
                        }

                        // Espaciado inferior para el FAB
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación de eliminación
    vinylToDelete?.let { vinyl ->
        AlertDialog(
            onDismissRequest = { vinylToDelete = null },
            title = { Text("Eliminar disco", style = MaterialTheme.typography.titleLarge) },
            text = {
                Text(
                    "¿Estás seguro de que querés eliminar \"${vinyl.title}\" de ${vinyl.artist}?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteVinyl(vinyl)
                        vinylToDelete = null
                        // Snackbar con undo
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "\"${vinyl.title}\" eliminado",
                                actionLabel = "Deshacer",
                                duration = SnackbarDuration.Long
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.undoDelete()
                            }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { vinylToDelete = null }) {
                    Text("Cancelar")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Bottom sheet del formulario
    if (showForm) {
        VinylFormSheet(
            vinyl = editingVinyl,
            onDismiss = { showForm = false },
            onSave = { vinyl ->
                if (editingVinyl != null) {
                    viewModel.updateVinyl(vinyl)
                } else {
                    viewModel.addVinyl(vinyl)
                }
                showForm = false
            }
        )
    }
}
