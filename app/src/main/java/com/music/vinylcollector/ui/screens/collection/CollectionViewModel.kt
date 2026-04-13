package com.music.vinylcollector.ui.screens.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.music.vinylcollector.domain.model.Genre
import com.music.vinylcollector.domain.model.Vinyl
import com.music.vinylcollector.domain.model.VinylStatus
import com.music.vinylcollector.domain.repository.VinylRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Estado de la UI — inmutable, se reconstruye ante cada cambio.
 */
data class CollectionUiState(
    val vinyls: List<Vinyl> = emptyList(),
    val filteredVinyls: List<Vinyl> = emptyList(),
    val searchQuery: String = "",
    val selectedGenre: Genre? = null,
    val selectedStatus: VinylStatus? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel de la colección — maneja estado con StateFlow (MVVM).
 * Aplica filtros y búsqueda sobre la lista completa de vinyls.
 */
class CollectionViewModel(
    private val repository: VinylRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionUiState(isLoading = true))
    val uiState: StateFlow<CollectionUiState> = _uiState.asStateFlow()

    // Último disco eliminado para undo
    private var lastDeletedVinyl: Vinyl? = null

    init {
        // Observa cambios en Room y aplica filtros reactivamente
        viewModelScope.launch {
            repository.getAllVinyls().collect { vinyls ->
                _uiState.update { state ->
                    state.copy(
                        vinyls = vinyls,
                        filteredVinyls = applyFilters(vinyls, state.searchQuery, state.selectedGenre, state.selectedStatus),
                        isLoading = false
                    )
                }
            }
        }

        // Intenta sincronizar con el backend al iniciar
        viewModelScope.launch {
            runCatching { repository.syncWithRemote() }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredVinyls = applyFilters(state.vinyls, query, state.selectedGenre, state.selectedStatus)
            )
        }
    }

    fun onGenreFilterChange(genre: Genre?) {
        _uiState.update { state ->
            state.copy(
                selectedGenre = genre,
                filteredVinyls = applyFilters(state.vinyls, state.searchQuery, genre, state.selectedStatus)
            )
        }
    }

    fun onStatusFilterChange(status: VinylStatus?) {
        _uiState.update { state ->
            state.copy(
                selectedStatus = status,
                filteredVinyls = applyFilters(state.vinyls, state.searchQuery, state.selectedGenre, status)
            )
        }
    }

    fun addVinyl(vinyl: Vinyl) {
        viewModelScope.launch {
            runCatching { repository.insertVinyl(vinyl) }
                .onFailure { _uiState.update { it.copy(errorMessage = "Error al guardar") } }
        }
    }

    fun updateVinyl(vinyl: Vinyl) {
        viewModelScope.launch {
            runCatching { repository.updateVinyl(vinyl) }
                .onFailure { _uiState.update { it.copy(errorMessage = "Error al actualizar") } }
        }
    }

    fun deleteVinyl(vinyl: Vinyl) {
        lastDeletedVinyl = vinyl
        viewModelScope.launch {
            runCatching { repository.deleteVinyl(vinyl) }
                .onFailure { _uiState.update { it.copy(errorMessage = "Error al eliminar") } }
        }
    }

    /** Restaura el último disco eliminado (para undo via Snackbar) */
    fun undoDelete() {
        lastDeletedVinyl?.let { vinyl ->
            viewModelScope.launch {
                repository.insertVinyl(vinyl)
                lastDeletedVinyl = null
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /** Aplica búsqueda por texto + filtros de género y estado */
    private fun applyFilters(
        vinyls: List<Vinyl>,
        query: String,
        genre: Genre?,
        status: VinylStatus?
    ): List<Vinyl> {
        return vinyls.filter { vinyl ->
            val matchesQuery = query.isBlank() ||
                vinyl.title.contains(query, ignoreCase = true) ||
                vinyl.artist.contains(query, ignoreCase = true)
            val matchesGenre = genre == null || vinyl.genre == genre
            val matchesStatus = status == null || vinyl.status == status
            matchesQuery && matchesGenre && matchesStatus
        }
    }

    /** Factory para crear el ViewModel con el repositorio inyectado */
    class Factory(private val repository: VinylRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CollectionViewModel(repository) as T
        }
    }
}
