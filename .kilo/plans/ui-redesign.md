# UI Redesign Plan — Pokemon Detail Card (Dribbble-inspired)

## Analysis of the Design Spec

The design describes a **single-card layout** (not scrollable with tabs), with:

1. **Top half** — Pokemon illustration emerging from a Pokéball, with type-themed abstract patterns
2. **Curved transition** with Pokemon name + national index number
3. **Bottom half** — Dark/solid panel with info grid:
   - Description (Pokédex flavor text)
   - Stats bars (HP, Attack, Defense, Sp. Atk, Sp. Def, Speed)
   - Type & Category (circular icons + text)
   - Weaknesses (pill badges with icon + name)
   - Weight & Height (line icons + metrics)

**Color palette is dynamic** per Pokemon's type/color.

## Key Changes from Current UI

| Aspect | Current | Redesign |
|--------|---------|----------|
| Layout | Scrollable with tabs (About / Base Stats) | Single card, all-in-one, no tabs |
| Hero | Simple gradient background | Pokéball theme + abstract patterns per type |
| Name position | In the gradient header | Curved transition between top and bottom halves |
| Bottom panel | White background with dividers | Dark/solid themed background panel |
| Stats | Simple bars | Redesigned bars with better visual hierarchy |
| Type display | Text badges only | Circular icon + text badges |
| Weaknesses | Not present | Pill badges with type icons |
| Category | Not present | Genus from API (e.g. "Flame Pokémon") |
| Back button | Top-left inside the gradient | Floating circular button |

## Files to Create

### 1. New Domain Models — Add fields for redesign

Since PokéAPI provides `genera` (category) but not weaknesses directly (that requires type damage relations), we have options:

- **Category**: Already available via `genera` in species response. Add `category: String` to `Pokemon` domain model.
- **Weaknesses**: Requires fetching type detail for each Pokemon type to get `damage_relations.double_damage_from`. Option A: fetch `GET /type/{id}` for each of the Pokemon's types. Option B: use a static lookup table. Let me ask the user.

### 2. `PokemonDetailCard.kt` (replaces `PokemonDetailScreen.kt`)

Complete rewrite of the screen — replaces the tab-based layout with a single card design.

**Component breakdown:**

```
PokemonDetailCard
├── BackButton (floating circular, top-left)
├── TopSection (illustration + type pattern decorations)
│   ├── AbstractPattern (geometric shapes based on type)
│   ├── PokemonImage (Coil AsyncImage, emerging from Pokéball)
│   └── TypeBadge (circular icon + name, at top-right area)
├── CurvedNameTransition
│   ├── PokemonName (large, bold, curved divider)
│   └── NationalIndex (N°282, top-right)
└── InfoPanel (dark/solid background with rounded corners on top)
    ├── Description (flavor text, uppercased subtitle "DESCRIPCIÓN")
    ├── StatsSection
    │   ├── SectionTitle "STATS"
    │   └── StatBar (for each stat — HP, Attack, Defense, Sp. Atk, Sp. Def, Speed)
    ├── TypeCategorySection
    │   ├── SectionTitle "TIPO Y CATEGORÍA"
    │   ├── TypeIcon (circular badge with type color + name)
    │   └── CategoryText (e.g. "Llama Pokémon")
    ├── WeaknessesSection
    │   ├── SectionTitle "DEBILIDADES"
    │   └── WeaknessPill (rounded pill with type icon + name)
    └── WeightHeightSection
        ├── SectionTitle "PESO Y ALTURA"
        ├── WeightIcon + WeightValue (kg)
        └── HeightIcon + HeightValue (m)
```

### 3. New Component Files

| File | Description |
|------|-------------|
| `presentation/component/PokemonTypeBadge.kt` | **Updated** — add circular variant with icon support |
| `presentation/component/StatBar.kt` | **Updated** — align with new visual style |
| `presentation/component/WeaknessPill.kt` | **New** — pill badge with type icon + name |
| `presentation/component/InfoSection.kt` | **New** — reusable section title + content |
| `presentation/component/MetricItem.kt` | **New** — weight/height display with icon |
| `presentation/component/AbstractPattern.kt` | **New** — decorative shapes per type (geometric for Gardevoir, smoke for Gengar, rings for Alakazam) |

### 4. Updated Domain Model — Add `category` field

```kotlin
data class Pokemon(
    ...
    val category: String,  // "Flame Pokémon", "Seed Pokémon", etc.
)
```

### 5. Updated DTO — Add `genera` to species response

```kotlin
data class PokemonSpeciesResponse(
    val color: NamedApiResource,
    @SerializedName("genera") val genera: List<GenusEntry>,
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>
)

data class GenusEntry(
    val genus: String,
    val language: NamedApiResource
)
```

### 6. Updated Mapper — Extract English genus

```kotlin
private fun extractCategory(species: PokemonSpeciesResponse): String {
    return species.genera
        .find { it.language.name == "en" }
        ?.genus ?: "Unknown"
}
```

## Color System

Each Pokemon type will map to a cohesive color palette:

```kotlin
data class PokemonTheme(
    val primary: Color,       // Main type color (used for panel, badges)
    val secondary: Color,     // Lighter/background tint
    val surface: Color,       // Info panel background
    val onSurface: Color      // Text on info panel
)
```

Examples from the spec:
- **Gardevoir** (Psychic-type): Primary=#D5A6E6, Surface=#3A2E4A, OnSurface=#E8DCF0
- **Gengar** (Ghost/Poison): Primary=#7B4B9A, Surface=#1A1A2E, OnSurface=#C4A0D8
- **Alakazam** (Psychic): Primary=#D4A843, Surface=#3D2E1A, OnSurface=#E8D4A0

For Pokemon with dual types, use the primary type for the main palette.

## Weaknesses Strategy (Question for User)

Option A: **Static lookup table** — Hardcode type weaknesses in a map (faster, no extra API calls):
```kotlin
object TypeWeaknesses {
    val chart = mapOf(
        PokemonType.FIRE to listOf(PokemonType.WATER, PokemonType.ROCK, PokemonType.GROUND),
        PokemonType.WATER to listOf(PokemonType.ELECTRIC, PokemonType.GRASS),
        ...
    )
}
```

Option B: **Dynamic API call** — Fetch `GET /type/{id}` for each type and extract `double_damage_from`.

**Recommendation: Option A (static table)** — simpler, no extra network calls, and type weaknesses don't change.

## Order of Implementation

1. Add `category` to `Pokemon` domain model + DTO + mapper (genus extraction)
2. Create `PokemonTheme` data class and type-to-theme mapping
3. Create new components: `MetricItem`, `InfoSection`, `WeaknessPill`
4. Rewrite `PokemonDetailScreen` → `PokemonDetailCard` (single card, no tabs)
5. Update existing components (`PokemonTypeBadge`, `StatBar`) for new style
6. Remove tab-related state from ViewModel (no longer needed)
7. Build and verify
