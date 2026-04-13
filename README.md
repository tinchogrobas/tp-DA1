# Vinyl Collector

Aplicación Android para gestionar una colección personal de discos de vinilo. Trabajo práctico universitario — primera iteración.

## Qué hace la app

- **CRUD completo** de discos de vinilo: crear, ver, editar y eliminar.
- **Búsqueda** por título y artista con barra animada.
- **Filtros** por género musical y estado del disco (lo tengo / lo quiero / prestado).
- **Offline-first**: funciona sin conexión usando Room como base de datos local, y sincroniza con el backend cuando hay internet.
- **Swipe-to-delete** con opción de deshacer vía Snackbar.
- **Bottom sheet** para alta y edición de discos, con validación de campos obligatorios.

## Pantallas

1. **Colección (pantalla principal)**: lista de vinilos con cards visuales, búsqueda, filtros por chips, FAB para agregar, swipe-to-delete.
2. **Acerca de**: información del equipo con tarjetas estilo "liner notes" de vinilo, y tecnologías usadas.
3. **Splash screen**: ícono de vinilo con fondo oscuro al iniciar la app.

## Cómo correrla

1. Abrir el proyecto en **Android Studio Hedgehog (2023.1.1)** o superior.
2. Sincronizar Gradle.
3. Configurar un emulador o dispositivo con **API 26+**.
4. Ejecutar la app (`Run > Run 'app'`).

### Backend (API REST)

La app apunta por defecto a una API en MockAPI.io. Para cambiar la URL base, editar `API_BASE_URL` en `app/build.gradle.kts`.

**Endpoints esperados:**

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/discos` | Listar todos los discos |
| GET | `/discos/{id}` | Obtener un disco por ID |
| POST | `/discos` | Crear un disco nuevo |
| PUT | `/discos/{id}` | Actualizar un disco |
| DELETE | `/discos/{id}` | Eliminar un disco |

**Estructura del JSON:**

```json
{
  "id": 1,
  "title": "OK Computer",
  "artist": "Radiohead",
  "year": 1997,
  "genre": "ROCK",
  "status": "OWNED",
  "rating": 5,
  "notes": "Edición original UK",
  "cover_url": "https://..."
}
```

### Personalizar el equipo

Editar los datos en `AboutScreen.kt` (línea ~30) con los nombres, roles y fotos del equipo.

## Tecnologías

- **Kotlin** + **Jetpack Compose** — UI declarativa
- **Clean Architecture** (data / domain / ui) con **MVVM**
- **Room** — persistencia local SQLite
- **Retrofit + OkHttp** — comunicación HTTP con backend REST
- **Coil** — carga asíncrona de imágenes
- **Navigation Compose** — navegación entre pantallas
- **Google Fonts (Downloadable)** — Playfair Display + Inter
- **Material 3** con tema custom (paleta ámbar/carbón)
- **JUnit** — tests unitarios del ViewModel
- **Compose UI Testing** — tests instrumentados de la UI

## Arquitectura

```
app/src/main/java/com/music/vinylcollector/
├── data/              # Capa de datos
│   ├── local/         # Room: Entity, DAO, Database
│   ├── remote/        # Retrofit: ApiService, DTO, NetworkMonitor
│   └── repository/    # Implementación del repositorio (offline-first)
├── domain/            # Capa de dominio
│   ├── model/         # Modelos puros: Vinyl, Genre, VinylStatus
│   └── repository/    # Interfaz del repositorio
├── ui/                # Capa de presentación
│   ├── components/    # Composables reutilizables
│   ├── navigation/    # NavGraph con rutas
│   ├── screens/       # Pantallas: Collection, About
│   └── theme/         # Colores, tipografía, tema M3 custom
├── di/                # Inyección de dependencias manual
├── MainActivity.kt
└── VinylCollectorApp.kt
```

## Tests

- **Unit tests** (`app/src/test/`): `CollectionViewModelTest` — verifica filtrado, CRUD y undo delete con repositorio fake.
- **UI tests** (`app/src/androidTest/`): `CollectionScreenTest` — verifica renderizado de lista, estado vacío, y datos en cards.

Ejecutar tests:
```bash
# Unit tests
./gradlew test

# UI tests (requiere emulador/dispositivo)
./gradlew connectedAndroidTest
```

## Diseño visual

- **Paleta**: fondo carbón (#1A1A1A), acentos ámbar (#D4A843), texto crema (#F5F0E1)
- **Tipografía**: Playfair Display (serif, títulos) + Inter (sans-serif, cuerpo)
- **Cards**: tapa del álbum prominente, badge de color por estado, rating con íconos de vinilo
- **Animaciones**: transiciones entre pantallas, filtros expandibles, búsqueda animada
- **Modo oscuro** (principal) y **modo claro** soportados
