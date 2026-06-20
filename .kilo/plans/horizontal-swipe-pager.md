# Horizontal Swipe Between Pokemon Cards

## Overview

Replace the single static Charizard card with a horizontally swipeable `HorizontalPager` (from Accompanist or Foundation Pager) that allows the user to swipe left/right between multiple Pokemon. Add crossfade/scale animation during transitions.

## Key Changes

| Aspect | Current | Proposed |
|--------|---------|----------|
| Data | Fetches only Pokemon #6 (Charizard) | Fetches a range (e.g. 1–151 Kanto) |
| Layout | Single scrollable card | HorizontalPager with page snap |
| Navigation | Static back button | Back + page indicator dots |
| Animation | None between cards | Crossfade + scale transition per card |
| VM State | `PokemonDetailUiState` (single Pokemon) | `PokemonCarouselUiState` with list and current index |

## Architecture Decisions

- **Data prefetching**: Load all Pokemon in a range (e.g. 1–151) sequentially on init. Option A: one-shot `async` for each. Option B: load lazily as the user swipes (prefetch adjacent). **Recommendation: Option B** — load the first few (e.g. 3–5) on init, then prefetch adjacent pages as the user swipes using a `snapshotFlow` on the current page.
- **HorizontalPager**: Use `androidx.compose.foundation.pager.HorizontalPager` (stable since Compose Foundation 1.4) — no Accompanist dependency needed.
- **Transition animation**: `PageSize.Fill` + `graphicsLayer { alpha = scaleX = scaleY }` based on page offset for a smooth crossfade + scale effect. Use `Modifier.graphicsLayer` with `lerp` between pages.
- **ViewModel**: Replace `PokemonDetailUiState` with a new sealed interface `PokemonCarouselUiState` containing `Loading`, `Success(pokemonList, currentIndex)`, `Error`. Add `onPageSelected(index)` and `prefetch(index)` methods.

## Approach Comparison

| Approach | Pros | Cons |
|----------|------|------|
| **A: Single HorizontalPager, load all at once** | Simplest implementation | Slow initial load for 151 Pokemon; blocks UI |
| **B: HorizontalPager + lazy prefetch** | Fast initial load, smooth swiping | Slightly more complex VM logic |
| **C: HorizontalPager + incremental chunk load** | Best UX for large sets (e.g. all 1025) | Overkill for Kanto-only; added complexity |

**Recommendation: Approach B** — load first 5 immediately, then prefetch adjacent as user swipes.

## Files to Create

### 1. `domain/usecase/GetPokemonRangeUseCase.kt` (New)

Single-use use case to fetch a range of Pokemon by IDs:

```kotlin
class GetPokemonRangeUseCase(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(ids: List<Int>): Result<List<Pokemon>>
}
```

This avoids bloating `GetPokemonDetailUseCase`. The repository already has `getPokemonDetail(id)` which we can call in a loop with `async/awaitAll`.

### 2. `domain/model/PokemonCarouselUiState.kt` (New)

Replace `PokemonDetailUiState` with a carousel-specific state:

```kotlin
sealed interface PokemonCarouselUiState {
    data object Loading : PokemonCarouselUiState
    data class Success(
        val pokemonList: List<Pokemon>,
        val currentIndex: Int
    ) : PokemonCarouselUiState
    data class Error(val message: String) : PokemonCarouselUiState
}
```

## Files to Modify

### 1. `presentation/viewmodel/PokemonDetailViewModel.kt`

- Inject `GetPokemonRangeUseCase` alongside existing `GetPokemonDetailUseCase` (or remove the single-use case if no longer needed).
- Replace `_uiState` type with `PokemonCarouselUiState`.
- On init: load IDs 1..5, emit `Success`.
- Add `onPageSelected(index: Int)` — updates `currentIndex`, triggers prefetch for pages `index-1`, `index+1`, `index+2`.
- Add `prefetchIfNeeded(index: Int)` — checks if adjacent pages are loaded, fetches missing ones.
- Maintain a `Map<Int, Pokemon>` internally (or `SparseArray`) for O(1) lookup by ID; rebuild `pokemonList` sorted by ID.
- Use `snapshotFlow { currentIndex }` to trigger prefetch in a coroutine.

### 2. `presentation/screen/PokemonDetailScreen.kt`

- Replace the single `PokemonDetailCard( pokemon = state.pokemon )` with a `HorizontalPager`.
- Extract `PokemonDetailCard` into a reusable composable that receives a `Pokemon` and renders the existing card layout.
- Wrap the `HorizontalPager` content with a `Modifier.graphicsLayer` that applies crossfade + scale based on `page.offset`:

```kotlin
@OptIn(ExperimentalFoundationApi::class)
val pagerState = rememberPagerState(
    pageCount = { pokemonList.size },
    initialPage = currentIndex
)

HorizontalPager(
    state = pagerState,
    modifier = Modifier.fillMaxSize()
) { page ->
    val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
    val scale = lerp(0.85f, 1f, 1f - abs(pageOffset))
    val alpha = lerp(0.3f, 1f, 1f - abs(pageOffset))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = alpha
            }
    ) {
        PokemonDetailCard(pokemon = pokemonList[page], ...)
    }
}
```

- Add `LaunchedEffect(pagerState.currentPage)` to sync with ViewModel: call `onPageSelected`.
- Remove `BackButton` or keep it (it still makes sense for navigation).
- Add page indicator dots at the bottom. Can use a simple `Row` with `Box` circles sized by active/inactive.

### 3. `presentation/navigation/BeautyPokeNavHost.kt`

- No changes needed — screen composable name stays `PokemonDetailScreen`, ViewModel injection unchanged.

### 4. `di/UseCaseModule.kt`

- Register `GetPokemonRangeUseCase` factory.

## Transition Animation Details

Use `graphicsLayer` with page offset for a **crossfade + scale** effect:

- **Scale**: 85% → 100% as page approaches center.
- **Alpha**: 30% → 100% as page approaches center.
- The `pageOffset` is in [-1, 1] range: -1 = fully left, 0 = centered, 1 = fully right.
- Use `lerp(start, end, 1f - abs(pageOffset))` for both scale and alpha.
- Overlap pages by setting `pageSpacing = 0.dp` (default).

Optionally add a subtle **rotation** effect or **elevation** shadow for depth. Keep it performant — `graphicsLayer` is GPU-accelerated.

## Page Indicator

Simple dot indicator at the bottom of the screen:

```kotlin
Row(
    modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
) {
    repeat(pokemonList.size) { index ->
        Box(
            modifier = Modifier
                .size(if (index == currentIndex) 10.dp else 6.dp)
                .clip(CircleShape)
                .background(if (index == currentIndex) theme.primary else theme.onSurface.copy(alpha = 0.3f))
        )
    }
}
```

## Order of Implementation

1. Create `GetPokemonRangeUseCase` in domain layer
2. Create `PokemonCarouselUiState` sealed interface
3. Update `PokemonDetailViewModel` — inject range use case, replace state, add prefetch logic
4. Rewrite `PokemonDetailScreen` — wrap card in `HorizontalPager` with crossfade/scale animation and page indicator
5. Register `GetPokemonRangeUseCase` in `UseCaseModule.kt`
6. Build and verify
7. Commit

## Future Considerations (not in scope)

- Lazy loading from API with pagination for all 1025 Pokemon.
- Swipe-to-dismiss with drag gesture.
- Shared element transition between list/grid and detail page.
- Haptic feedback on page change.
