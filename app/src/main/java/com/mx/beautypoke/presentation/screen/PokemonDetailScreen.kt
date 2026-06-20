package com.mx.beautypoke.presentation.screen

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import com.mx.beautypoke.domain.model.Pokemon
import com.mx.beautypoke.domain.model.PokemonCarouselUiState
import com.mx.beautypoke.domain.model.PokemonType
import com.mx.beautypoke.domain.model.TypeWeaknesses
import com.mx.beautypoke.presentation.component.AbstractPattern
import com.mx.beautypoke.presentation.component.InfoSection
import com.mx.beautypoke.presentation.component.MetricItem
import com.mx.beautypoke.presentation.component.PokemonTypeBadge
import com.mx.beautypoke.presentation.component.PokemonTypeBadgeStyle
import com.mx.beautypoke.presentation.component.StatBar
import com.mx.beautypoke.presentation.component.WeaknessPill
import com.mx.beautypoke.presentation.theme.PokemonTheme
import com.mx.beautypoke.presentation.theme.toTheme
import com.mx.beautypoke.presentation.viewmodel.PokemonDetailViewModel
import kotlin.math.abs

@Composable
fun PokemonDetailScreen(
    viewModel: PokemonDetailViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is PokemonCarouselUiState.Loading -> LoadingScreen()
        is PokemonCarouselUiState.Error -> ErrorScreen(
            message = state.message,
            onRetry = viewModel::onRetry
        )
        is PokemonCarouselUiState.Success -> PokemonCarousel(
            pokemonList = state.pokemonList,
            currentIndex = state.currentIndex,
            onPageSelected = viewModel::onPageSelected,
            onBackClick = onBackClick
        )
    }
}

@Composable
private fun PokemonCarousel(
    pokemonList: List<Pokemon>,
    currentIndex: Int,
    onPageSelected: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = currentIndex,
        pageCount = { pokemonList.size }
    )

    val primaryType = pokemonList.getOrNull(pagerState.currentPage)?.types?.firstOrNull()
        ?: PokemonType.NORMAL
    val theme: PokemonTheme = primaryType.toTheme()

    LaunchedEffect(pagerState.currentPage) {
        onPageSelected(pagerState.currentPage)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.surface)
    ) {
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
                        this.alpha = alpha
                    }
            ) {
                PokemonDetailCard(pokemon = pokemonList[page])
            }
        }

        BackButton(onClick = onBackClick, theme = theme)

        PageIndicator(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
            pageCount = pokemonList.size,
            currentPage = pagerState.currentPage,
            activeColor = theme.primary,
            inactiveColor = theme.onSurface
        )
    }
}

@Composable
private fun PageIndicator(
    modifier: Modifier = Modifier,
    pageCount: Int,
    currentPage: Int,
    activeColor: Color,
    inactiveColor: Color
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage
            val size by animateIntAsState(
                targetValue = if (isActive) 10 else 6,
                label = "dotSize"
            )
            Box(
                modifier = Modifier
                    .size(size.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) activeColor
                        else inactiveColor.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

@Composable
fun PokemonDetailCard(
    pokemon: Pokemon
) {
    val primaryType = pokemon.types.firstOrNull() ?: PokemonType.NORMAL
    val theme: PokemonTheme = primaryType.toTheme()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            TopSection(pokemon = pokemon, theme = theme, primaryType = primaryType)

            CurvedNameTransition(pokemon = pokemon, theme = theme)

            InfoPanel(pokemon = pokemon, theme = theme, primaryType = primaryType)
        }
    }
}

@Composable
private fun TopSection(
    pokemon: Pokemon,
    theme: PokemonTheme,
    primaryType: PokemonType
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        theme.primary,
                        theme.primary.copy(alpha = 0.7f),
                        theme.secondary
                    )
                )
            )
    ) {
        AbstractPattern(
            type = primaryType,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#${pokemon.id.toString().padStart(3, '0')}",
                fontSize = 100.sp,
                fontWeight = FontWeight.Black,
                color = Color.White.copy(alpha = 0.1f)
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier.size(220.dp),
                contentScale = ContentScale.Fit
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 80.dp)
        ) {
            pokemon.types.forEach { type ->
                PokemonTypeBadge(
                    type = type,
                    style = PokemonTypeBadgeStyle.CIRCULAR,
                    containerColor = Color.White.copy(alpha = 0.25f),
                    contentColor = Color.White
                )
            }
        }
    }
}

@Composable
private fun CurvedNameTransition(
    pokemon: Pokemon,
    theme: PokemonTheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        theme.secondary,
                        theme.surface
                    ),
                    startY = 0f,
                    endY = 120f
                )
            )
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = pokemon.name,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "N°${pokemon.id.toString().padStart(3, '0')}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun InfoPanel(
    pokemon: Pokemon,
    theme: PokemonTheme,
    primaryType: PokemonType
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(theme.surface)
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 32.dp)
    ) {
        InfoSection(title = "DESCRIPTION", onSurfaceColor = theme.onSurface) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = pokemon.description,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = theme.onSurface.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = theme.onSurface.copy(alpha = 0.1f))
        Spacer(modifier = Modifier.height(20.dp))

        InfoSection(title = "BASE STATS", onSurfaceColor = theme.onSurface) {
            pokemon.stats.forEach { stat ->
                StatBar(
                    stat = stat,
                    barColor = theme.primary,
                    onSurfaceColor = theme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = theme.onSurface.copy(alpha = 0.1f))
        Spacer(modifier = Modifier.height(20.dp))

        InfoSection(title = "TYPE & CATEGORY", onSurfaceColor = theme.onSurface) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                pokemon.types.forEach { type ->
                    PokemonTypeBadge(
                        type = type,
                        style = PokemonTypeBadgeStyle.CIRCULAR,
                        containerColor = theme.primary.copy(alpha = 0.2f),
                        contentColor = theme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = pokemon.category,
                    fontSize = 13.sp,
                    color = theme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = theme.onSurface.copy(alpha = 0.1f))
        Spacer(modifier = Modifier.height(20.dp))

        InfoSection(title = "WEAKNESSES", onSurfaceColor = theme.onSurface) {
            WeaknessRow(pokemon = pokemon, theme = theme)
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = theme.onSurface.copy(alpha = 0.1f))
        Spacer(modifier = Modifier.height(20.dp))

        InfoSection(title = "WEIGHT & HEIGHT", onSurfaceColor = theme.onSurface) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                MetricItem(
                    icon = Icons.Filled.MonitorWeight,
                    label = "Weight",
                    value = "${pokemon.weight / 10.0} kg",
                    onSurfaceColor = theme.onSurface
                )
                MetricItem(
                    icon = Icons.Filled.Straighten,
                    label = "Height",
                    value = "${pokemon.height / 10.0} m",
                    onSurfaceColor = theme.onSurface
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WeaknessRow(
    pokemon: Pokemon,
    theme: PokemonTheme
) {
    val weaknesses = pokemon.types
        .flatMap { TypeWeaknesses.weaknessesOf(it) }
        .distinct()
        .filter { it !in pokemon.types }

    if (weaknesses.isEmpty()) {
        Text(
            text = "No weaknesses",
            fontSize = 13.sp,
            color = theme.onSurface.copy(alpha = 0.5f)
        )
    } else {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            weaknesses.forEach { type ->
                WeaknessPill(
                    type = type,
                    containerColor = theme.primary.copy(alpha = 0.15f),
                    contentColor = theme.primary.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun BackButton(
    onClick: () -> Unit,
    theme: PokemonTheme
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(start = 12.dp, top = 48.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(theme.onSurface.copy(alpha = 0.15f))
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = theme.onSurface
        )
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFF74CB48))
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = Color(0xFF666666),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74CB48))
        ) {
            Text("Retry")
        }
    }
}
