# Data Layer Implementation Plan — PokéAPI Integration

## API: PokéAPI v2

- **Base URL:** `https://pokeapi.co/api/v2/`
- **Auth:** None required (free, no key)
- **Rate limit:** None (but cache locally per fair use policy)
- **Endpoints needed for Pokemon detail:**

| Endpoint | Response provides |
|----------|------------------|
| `GET /pokemon/{id}` | id, name, types, stats (base_stat), height, weight, sprites (official artwork) |
| `GET /pokemon-species/{id}` | color, flavor_text_entries (English), evolution_chain, genus |

### Key JSON field mapping to current `Pokemon` domain model

| Domain field | JSON source |
|---|---|
| `id` | `pokemon.id` |
| `name` | `pokemon.name` (capitalize first letter) |
| `imageUrl` | `pokemon.sprites.other.official-artwork.front_default` |
| `types` | `pokemon.types[].type.name` → map to `PokemonType` enum |
| `stats` | `pokemon.stats[].base_stat` + `pokemon.stats[].stat.name` → `PokemonStat` |
| `height` | `pokemon.height` (decimeters) |
| `weight` | `pokemon.weight` (hectograms) |
| `description` | `pokemon-species.flavor_text_entries[0]` where `language.name == "en"` and `version.name == "sword"` (clean `\f` → ` `) |
| `color` | `pokemon-species.color.name` → map to `PokemonColor` enum |

## Files to Create (in order)

### 1. DTOs — `data/remote/dto/`

**`PokemonDto.kt`**
```kotlin
@Serializable
data class PokemonResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<PokemonTypeDto>,
    val stats: List<PokemonStatDto>,
    val sprites: PokemonSprites
)

@Serializable
data class PokemonTypeDto(
    val slot: Int,
    val type: NamedApiResource
)

@Serializable
data class PokemonStatDto(
    val base_stat: Int,
    val stat: NamedApiResource
)

@Serializable
data class PokemonSprites(
    val other: OtherSprites
)

@Serializable
data class OtherSprites(
    @SerialName("official-artwork") val officialArtwork: OfficialArtwork
)

@Serializable
data class OfficialArtwork(
    @SerialName("front_default") val frontDefault: String?
)

@Serializable
data class NamedApiResource(
    val name: String
)
```

**`PokemonSpeciesDto.kt`**
```kotlin
@Serializable
data class PokemonSpeciesResponse(
    val color: NamedApiResource,
    @SerialName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>
)

@Serializable
data class FlavorTextEntry(
    @SerialName("flavor_text") val flavorText: String,
    val language: NamedApiResource,
    val version: NamedApiResource
)
```

### 2. API Service — `data/remote/api/`

**`PokemonApiService.kt`**
```kotlin
interface PokemonApiService {
    @GET("pokemon/{id}")
    suspend fun getPokemon(@Path("id") id: Int): PokemonResponse

    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpecies(@Path("id") id: Int): PokemonSpeciesResponse
}
```

### 3. Retrofit Instance — `data/remote/` (simple object for now)

**`RetrofitClient.kt`** — standalone singleton (no Hilt yet)
```kotlin
object RetrofitClient {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val apiService: PokemonApiService = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PokemonApiService::class.java)
}
```

### 4. Mapper — `data/remote/mapper/`

**`PokemonMapper.kt`**
```kotlin
object PokemonMapper {
    fun mapToDomain(pokemon: PokemonResponse, species: PokemonSpeciesResponse): Pokemon {
        // Map types, stats, color, clean description, etc.
    }
}
```

Key mapping logic:
- `PokemonType` from `type.name` (e.g., "fire" → `PokemonType.FIRE`)
- `PokemonColor` from `species.color.name` (e.g., "red" → `PokemonColor.RED`)
- Clean `\f` characters from flavor text
- First English flavor text for "sword" version (prefer latest generation)
- Default stats max value = 255 (standard for Pokemon)

### 5. Repository Interface — `domain/repository/`

**`PokemonRepository.kt`**
```kotlin
interface PokemonRepository {
    suspend fun getPokemonDetail(id: Int): Result<Pokemon>
}
```

### 6. Repository Implementation — `data/repository/`

**`PokemonRepositoryImpl.kt`**
```kotlin
class PokemonRepositoryImpl : PokemonRepository {
    override suspend fun getPokemonDetail(id: Int): Result<Pokemon> {
        return runCatching {
            val pokemon = apiService.getPokemon(id)
            val species = apiService.getPokemonSpecies(id)
            mapper.mapToDomain(pokemon, species)
        }
    }
}
```

### 7. Use Case — `domain/usecase/`

**`GetPokemonDetailUseCase.kt`**
```kotlin
class GetPokemonDetailUseCase(private val repository: PokemonRepository) {
    suspend operator fun invoke(id: Int): Result<Pokemon> {
        return repository.getPokemonDetail(id)
    }
}
```

### 8. Update ViewModel

**`PokemonDetailViewModel.kt`** — replace mock data with use case call
- Inject `GetPokemonDetailUseCase` (via constructor or manually)
- Remove `delay(800)` mock
- Call `getPokemonDetailUseCase(6)` in `loadPokemonDetail()`

## Dependencies to Add (libs.versions.toml + app/build.gradle.kts)

| Library | Version | Purpose |
|---------|---------|---------|
| `com.squareup.retrofit2:retrofit` | 2.11.0 | HTTP client |
| `com.squareup.retrofit2:converter-gson` | 2.11.0 | JSON parsing |
| `com.squareup.okhttp3:okhttp` | 4.12.0 | HTTP engine |
| `com.google.code.gson:gson` | 2.11.0 | JSON parser |

Also add `android:usesCleartextTraffic="true"` to `AndroidManifest.xml` (PokéAPI uses HTTPS so not needed, but good to know).

## Files to Modify

- `gradle/libs.versions.toml` — add Retrofit + Gson versions/libraries
- `app/build.gradle.kts` — add Retrofit + Gson + OkHttp dependencies
- `PokemonDetailViewModel.kt` — wire use case, remove mock data
- `PokemonDetailScreen.kt` — no changes needed (already uses state hoisting)
- `BeautyPokeNavHost.kt` — pass use case to ViewModel
- `AGENTS.md` — add data layer implementation details

## Commit Plan

1. `build: add Retrofit, Gson, and OkHttp dependencies`
2. `feat: create Pokemon DTOs and API service for PokéAPI`
3. `feat: create RetrofitClient singleton and PokemonMapper`
4. `feat: add PokemonRepository interface in domain layer`
5. `feat: implement PokemonRepositoryImpl in data layer`
6. `feat: create GetPokemonDetailUseCase`
7. `refactor: update PokemonDetailViewModel to use use case instead of mock data`
8. `docs: update AGENTS.md with data layer conventions`

## Design Decisions

- **Gson** over Kotlinx Serialization: simpler setup, no KSP plugin needed for now.
- **Standalone RetrofitClient**: no Hilt yet (keeping it simple); will migrate to Hilt later.
- **Result<T> wrapper**: Kotlin's built-in `runCatching` for error handling in repository.
- **Description logic**: Prefer the latest generation English flavor text (filter by `version.name == "sword"` → fallback to first English entry).
- **Stat max value**: Hardcoded to 255 (standard Pokemon base stat max).
