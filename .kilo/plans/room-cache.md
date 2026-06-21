# Room Cache Implementation Plan

## Architecture

Room database used as a local cache for Pokemon data. Repository implements cache-first strategy: check Room first, fall back to network, then persist to Room.

## Files Created

| File | Purpose |
|---|---|
| `data/local/entity/PokemonEntity.kt` | Room entity with `@Entity(tableName = "pokemon")`, stores complex fields as JSON strings |
| `data/local/dao/PokemonDao.kt` | Room DAO with `@Upsert` (insert or update), `getById`, `getByIds`, `getAllCachedIds` |
| `data/local/database/BeautyPokeDatabase.kt` | Room `@Database` class with version 1, `exportSchema = false` |
| `data/local/mapper/PokemonEntityMapper.kt` | Singleton object mapping `PokemonEntity ↔ Pokemon` using Gson for serializing `types` and `stats` |

## Files Modified

| File | Changes |
|---|---|
| `gradle/libs.versions.toml` | Added `room = "2.7.1"` version, `room-runtime`, `room-ktx`, `room-compiler` libraries |
| `app/build.gradle.kts` | Added `implementation(libs.room.runtime)`, `implementation(libs.room.ktx)`, `annotationProcessor(libs.room.compiler)` |
| `data/repository/PokemonRepositoryImpl.kt` | Added `PokemonDao` constructor param; cache-first logic: check Room → return cached, else fetch API → upsert → return |
| `di/DatabaseModule.kt` (new) | Koin module providing `BeautyPokeDatabase` and `PokemonDao` |
| `di/RepositoryModule.kt` | Passes `dao = get()` to `PokemonRepositoryImpl` |
| `di/UseCaseModule.kt` | Added `databaseModule` to `appModules` list (order: network, database, repository, usecase, viewmodel) |
| `BeautyPokeApp.kt` | Added `androidContext(this@BeautyPokeApp)` before `modules(appModules)` |

## Key Decisions

- **`annotationProcessor` over `kapt`**: AGP 9.1.1 has built-in Kotlin support; `kotlin("kapt")` plugin is incompatible. Room annotation processing works with `annotationProcessor`.
- **`@Upsert` over `@Insert(onConflict = REPLACE)`**: Cleaner API, available since Room 2.5.0.
- **Gson serialization for complex fields**: `types: List<PokemonType>` and `stats: List<PokemonStat>` stored as JSON strings using `Gson()` directly (avoids coupling to NetworkModule's Gson instance).
- **PokemonColor stored as name string**: `colorName: String` stored via `PokemonColor.name`, mapped back with `PokemonColor.valueOf()`.
- **No ViewModel changes needed**: Cache-first behavior is transparent to the presentation layer.
- **`databaseModule` before `repositoryModule`** in `appModules` to satisfy DI dependency order.
