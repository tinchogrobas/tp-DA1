package com.music.vinylcollector

import com.music.vinylcollector.domain.model.Genre
import com.music.vinylcollector.domain.model.Vinyl
import com.music.vinylcollector.domain.model.VinylStatus
import com.music.vinylcollector.domain.repository.VinylRepository
import com.music.vinylcollector.ui.screens.collection.CollectionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios del ViewModel — verifica filtrado, CRUD y undo.
 * Usa un repositorio fake in-memory para aislar la lógica.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CollectionViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeRepository: FakeVinylRepository
    private lateinit var viewModel: CollectionViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeVinylRepository()
        viewModel = CollectionViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads vinyls from repository`() = runTest {
        val vinyl = createTestVinyl(title = "OK Computer")
        fakeRepository.addVinyl(vinyl)

        // El ViewModel recoge los datos del flow
        val state = viewModel.uiState.value
        assertTrue(state.vinyls.any { it.title == "OK Computer" })
    }

    @Test
    fun `search filters vinyls by title`() = runTest {
        fakeRepository.addVinyl(createTestVinyl(title = "OK Computer", artist = "Radiohead"))
        fakeRepository.addVinyl(createTestVinyl(title = "The Dark Side of the Moon", artist = "Pink Floyd"))

        viewModel.onSearchQueryChange("radiohead")

        val state = viewModel.uiState.value
        assertEquals(1, state.filteredVinyls.size)
        assertEquals("Radiohead", state.filteredVinyls[0].artist)
    }

    @Test
    fun `genre filter works correctly`() = runTest {
        fakeRepository.addVinyl(createTestVinyl(title = "Kind of Blue", genre = Genre.JAZZ))
        fakeRepository.addVinyl(createTestVinyl(title = "Nevermind", genre = Genre.ROCK))

        viewModel.onGenreFilterChange(Genre.JAZZ)

        val state = viewModel.uiState.value
        assertEquals(1, state.filteredVinyls.size)
        assertEquals("Kind of Blue", state.filteredVinyls[0].title)
    }

    @Test
    fun `status filter works correctly`() = runTest {
        fakeRepository.addVinyl(createTestVinyl(title = "Abbey Road", status = VinylStatus.OWNED))
        fakeRepository.addVinyl(createTestVinyl(title = "Wish You Were Here", status = VinylStatus.WANTED))

        viewModel.onStatusFilterChange(VinylStatus.WANTED)

        val state = viewModel.uiState.value
        assertEquals(1, state.filteredVinyls.size)
        assertEquals("Wish You Were Here", state.filteredVinyls[0].title)
    }

    @Test
    fun `add vinyl updates the list`() = runTest {
        val vinyl = createTestVinyl(title = "Thriller")
        viewModel.addVinyl(vinyl)

        val state = viewModel.uiState.value
        assertTrue(state.vinyls.any { it.title == "Thriller" })
    }

    @Test
    fun `delete and undo restores vinyl`() = runTest {
        val vinyl = createTestVinyl(id = 1, title = "Rumours")
        fakeRepository.addVinyl(vinyl)

        viewModel.deleteVinyl(vinyl)
        assertFalse(viewModel.uiState.value.vinyls.any { it.title == "Rumours" })

        viewModel.undoDelete()
        assertTrue(viewModel.uiState.value.vinyls.any { it.title == "Rumours" })
    }

    private fun createTestVinyl(
        id: Long = 0,
        title: String = "Test Album",
        artist: String = "Test Artist",
        genre: Genre = Genre.ROCK,
        status: VinylStatus = VinylStatus.OWNED
    ) = Vinyl(
        id = id,
        title = title,
        artist = artist,
        year = 2020,
        genre = genre,
        status = status,
        rating = 4,
        notes = "",
        coverUrl = ""
    )
}

/**
 * Repositorio fake para testing — almacena en memoria con StateFlow.
 */
class FakeVinylRepository : VinylRepository {

    private val vinyls = MutableStateFlow<List<Vinyl>>(emptyList())
    private var nextId = 1L

    fun addVinyl(vinyl: Vinyl) {
        val withId = if (vinyl.id == 0L) vinyl.copy(id = nextId++) else vinyl
        vinyls.update { it + withId }
    }

    override fun getAllVinyls(): Flow<List<Vinyl>> = vinyls

    override suspend fun getVinylById(id: Long): Vinyl? =
        vinyls.value.find { it.id == id }

    override suspend fun insertVinyl(vinyl: Vinyl): Long {
        val id = if (vinyl.id == 0L) nextId++ else vinyl.id
        val withId = vinyl.copy(id = id)
        vinyls.update { it + withId }
        return id
    }

    override suspend fun updateVinyl(vinyl: Vinyl) {
        vinyls.update { list -> list.map { if (it.id == vinyl.id) vinyl else it } }
    }

    override suspend fun deleteVinyl(vinyl: Vinyl) {
        vinyls.update { list -> list.filter { it.id != vinyl.id } }
    }

    override suspend fun syncWithRemote() { /* no-op en tests */ }
}
