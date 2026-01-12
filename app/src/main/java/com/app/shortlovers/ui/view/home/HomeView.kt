package com.app.shortlovers.ui.view.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.shortlovers.R
import com.app.shortlovers.core.models.DirectusErrorCode
import com.app.shortlovers.core.models.TabItem
import com.app.shortlovers.core.models.Title
import com.app.shortlovers.ui.components.EmptyView
import com.app.shortlovers.ui.components.ErrorView
import com.app.shortlovers.ui.components.LoadingView
import com.app.shortlovers.ui.components.OfflineView
import com.app.shortlovers.ui.components.frostedOverlay
import com.app.shortlovers.ui.theme.BaseBackground
import com.app.shortlovers.ui.theme.BaseYellow
import com.app.shortlovers.ui.theme.GlassBorder
import com.app.shortlovers.ui.theme.GlassWhite
import com.app.shortlovers.ui.theme.GlowYellow
import com.app.shortlovers.ui.theme.KumbhSansFamily
import com.app.shortlovers.viewModel.home.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeView(viewModel: HomeViewModel = viewModel()) {
    val tabs by viewModel.tabs.collectAsState()
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    val titles by viewModel.titles.collectAsState()
    val featuredTitles by viewModel.featuredTitles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(color = BaseBackground)
                .drawBehind {
                    // Yellow glow at top-left corner
                    drawCircle(
                        brush =
                            Brush.radialGradient(
                                colors = listOf(GlowYellow, Color.Transparent),
                                center = Offset(0f, 0f),
                                radius = 500f
                            ),
                        center = Offset(0f, 0f),
                        radius = 500f
                    )
                    // Yellow glow at top-right corner
                    drawCircle(
                        brush =
                            Brush.radialGradient(
                                colors =
                                    listOf(
                                        GlowYellow.copy(alpha = 0.1f),
                                        Color.Transparent
                                    ),
                                center = Offset(size.width, 0f),
                                radius = 400f
                            ),
                        center = Offset(size.width, 0f),
                        radius = 400f
                    )
                }
    ) {
        // Main content layer
        Column(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    LoadingView()
                }

                error != null -> {
                    if (error!!.code == DirectusErrorCode.NETWORK_ERROR) {
                        OfflineView(
                            onRetry = {
                                viewModel.clearError()
                                viewModel.refresh()
                            }
                        )
                    } else {
                        ErrorView(
                            message = error!!.getUserFriendlyMessage(),
                            onRetry = {
                                viewModel.clearError()
                                viewModel.refresh()
                            }
                        )
                    }
                }

                tabs.isEmpty() -> {
                    EmptyView(
                        title = "No Content Yet",
                        message = "Content is being prepared. Please try again later.",
                        actionLabel = "Reload",
                        onAction = { viewModel.refresh() }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        // Search Bar - at the top
                        item { SearchBar() }

                        // Tab Menu - above carousel
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            ScrollableTabMenu(
                                tabs = tabs,
                                selectedTabIndex = selectedTabIndex,
                                onTabSelected = { viewModel.selectTab(it) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Carousel
                        item { CarouselComponent(featuredTitles) }

                        // Spacer before grid
                        item { Spacer(modifier = Modifier.height(16.dp)) }

                        // Series Grid - manual 2-column layout
                        items(titles.chunked(2)) { rowItems ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowItems.forEach { title ->
                                    Box(modifier = Modifier.weight(1f)) { SeriesCard(title) }
                                }
                                // Fill empty space if odd number
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
                .frostedOverlay(cornerRadius = 24.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Search drama...",
                color = Color.Gray,
                fontSize = 14.sp,
                fontFamily = KumbhSansFamily
            )
        }
    }
}

@Composable
fun CarouselComponent(titles: List<Title>) {
    // Fallback to placeholder images if no data
    val images =
        remember(titles) {
            if (titles.isEmpty()) {
                listOf(R.drawable.poster1, R.drawable.poster2, R.drawable.poster3)
            } else {
                titles.map { it.posterUrl }
            }
        }

    val titleTexts =
        remember(titles) {
            if (titles.isEmpty()) {
                listOf("The Reborn Tycoon", "Love Behind the Orange Sky", "Endless Adventure")
            } else {
                titles.map { it.title ?: "" }
            }
        }

    val pagerState = rememberPagerState(pageCount = { images.size.coerceAtLeast(1) })

    // Auto scroll
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            if (images.isNotEmpty()) {
                val nextPage = (pagerState.currentPage + 1) % images.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) { page ->
            Box(modifier = Modifier.fillMaxSize()) {
                // Image - either from URL or local resource
                val imageData = images.getOrNull(page)
                if (imageData is Int) {
                    Image(
                        painter = painterResource(id = imageData),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AsyncImage(
                        model =
                            ImageRequest.Builder(LocalContext.current)
                                .data(imageData)
                                .crossfade(true)
                                .build(),
                        placeholder = painterResource(R.drawable.placeholder_drama),
                        error = painterResource(R.drawable.placeholder_drama),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Gradient overlay for text readability
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors =
                                        listOf(
                                            Color.Transparent,
                                            Color.Black.copy(
                                                alpha = 0.7f
                                            )
                                        )
                                )
                            )
                )

                // Title overlay with shadow
                Text(
                    text = titleTexts.getOrNull(page) ?: "",
                    color = Color.White,
                    fontFamily = KumbhSansFamily,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style =
                        androidx.compose.ui.text.TextStyle(
                            shadow =
                                Shadow(
                                    color = Color.Black.copy(alpha = 0.8f),
                                    offset = Offset(2f, 2f),
                                    blurRadius = 8f
                                )
                        ),
                    modifier =
                        Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                            .padding(bottom = 24.dp)
                )
            }
        }

        // Indicators
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        ) {
            repeat(images.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier =
                        Modifier
                            .padding(3.dp)
                            .size(if (isSelected) 8.dp else 6.dp)
                            .background(
                                if (isSelected) BaseYellow else Color.Gray,
                                shape = CircleShape
                            )
                )
            }
        }
    }
}

@Composable
fun ScrollableTabMenu(tabs: List<TabItem>, selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(tabs) { index, tab ->
            val isSelected = index == selectedTabIndex

            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .then(
                            if (isSelected) {
                                Modifier.background(BaseYellow)
                            } else {
                                Modifier
                                    .background(GlassWhite)
                                    .border(
                                        1.dp,
                                        GlassBorder,
                                        RoundedCornerShape(20.dp)
                                    )
                            }
                        )
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = tab.name,
                    color = if (isSelected) Color.Black else Color.White,
                    fontFamily = KumbhSansFamily,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun DropdownFilterRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Category dropdown
        DropdownFilterButton(text = "Category", onClick = { /* Visual only for now */ })

        // A-Z dropdown
        DropdownFilterButton(text = "A-Z", onClick = { /* Visual only for now */ })
    }
}

@Composable
fun DropdownFilterButton(text: String, onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(BaseYellow)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = text,
                color = Color.Black,
                fontFamily = KumbhSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun SeriesCard(title: Title) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Poster
        AsyncImage(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(title.posterUrl)
                    .crossfade(true)
                    .build(),
            placeholder = painterResource(R.drawable.placeholder_drama),
            error = painterResource(R.drawable.placeholder_drama),
            contentDescription = title.title,
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.7f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.DarkGray)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
            text = title.title ?: "",
            color = Color.White,
            fontFamily = KumbhSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
