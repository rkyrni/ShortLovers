package com.app.shortlovers.ui.view.beranda

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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.app.shortlovers.core.models.MainResponseDrama
import com.app.shortlovers.ui.components.EmptyView
import com.app.shortlovers.ui.components.ErrorView
import com.app.shortlovers.ui.components.LoadingView
import com.app.shortlovers.ui.components.OfflineView
import com.app.shortlovers.ui.theme.BaseBackground
import com.app.shortlovers.ui.theme.BaseBlack
import com.app.shortlovers.ui.theme.BaseYellow
import com.app.shortlovers.ui.theme.KumbhSansFamily
import com.app.shortlovers.viewModel.beranda.BerandaViewModel
import kotlinx.coroutines.delay

@Composable
fun BerandaView(viewModel: BerandaViewModel = viewModel()) {
    val mainData by viewModel.mainData
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Get selected tab index
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Get tabs from API data
    val tabs = mainData?.map { it.tabName ?: "" } ?: listOf("Utama", "Terbaru", "Populer", "Semua")

    // Get dramas for current selected tab
    val currentDramas =
        remember(mainData, selectedTabIndex) {
            mainData?.getOrNull(selectedTabIndex)?.let { tab ->
                // If tab has categories, flatten all dramas from categories
                if (!tab.categories.isNullOrEmpty()) {
                    tab.categories.flatMap { it.dramas ?: emptyList() }
                } else {
                    tab.dramas ?: emptyList()
                }
            }
                ?: emptyList()
        }

    // Get featured dramas for carousel (from first tab/Utama)
    val featuredDramas =
        remember(mainData) {
            mainData?.firstOrNull()?.let { tab ->
                if (!tab.categories.isNullOrEmpty()) {
                    tab.categories.flatMap { it.dramas ?: emptyList() }.take(5)
                } else {
                    tab.dramas?.take(5) ?: emptyList()
                }
            }
                ?: emptyList()
        }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = BaseBackground)) {
        HeaderBar()

        when {
            isLoading -> {
                LoadingView()
            }

            error != null -> {
                if (error!!.code == DirectusErrorCode.NETWORK_ERROR) {
                    OfflineView(
                        onRetry = {
                            viewModel.clearError()
                            viewModel.fetchMainData()
                        }
                    )
                } else {
                    ErrorView(
                        message = error!!.getUserFriendlyMessage(),
                        onRetry = {
                            viewModel.clearError()
                            viewModel.fetchMainData()
                        }
                    )
                }
            }

            mainData.isNullOrEmpty() -> {
                EmptyView(
                    title = "Belum Ada Konten",
                    message = "Konten sedang disiapkan. Silakan coba lagi nanti.",
                    actionLabel = "Muat Ulang",
                    onAction = { viewModel.fetchMainData() }
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // Carousel
                    item { CarouselComponent(featuredDramas) }

                    // Tab Menu
                    item {
                        Spacer(modifier = Modifier.height(15.dp))
                        ScrollableTabMenu(
                            tabs = tabs,
                            selectedTabIndex = selectedTabIndex,
                            onTabSelected = { selectedTabIndex = it }
                        )
                    }

                    // Dropdown Filters
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        DropdownFilterRow()
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Series Grid - manual 2-column layout
                    items(currentDramas.chunked(2)) { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { drama ->
                                Box(modifier = Modifier.weight(1f)) { SeriesCard(drama) }
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

@Composable
fun HeaderBar() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(BaseBlack)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Menu Icon
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        // Title
        Text(
            text = "ShortLovers",
            color = BaseYellow,
            fontFamily = KumbhSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        // Search Icon
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun CarouselComponent(dramas: List<MainResponseDrama>) {
    // Fallback to placeholder images if no data
    val images =
        if (dramas.isEmpty()) {
            listOf(R.drawable.poster1, R.drawable.poster2, R.drawable.poster3)
        } else {
            dramas.map { it.coverLink ?: it.cover }
        }

    val titles =
        if (dramas.isEmpty()) {
            listOf(
                "Sang Konglomerat Terlahir Kembali",
                "Cinta di Balik Langit Jingga",
                "Petualangan Tanpa Akhir"
            )
        } else {
            dramas.map { it.title ?: "" }
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

                // Title overlay
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                ) {
                    Text(
                        text = titles.getOrNull(page) ?: "",
                        color = Color.White,
                        fontFamily = KumbhSansFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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
fun ScrollableTabMenu(tabs: List<String>, selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
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
                                Modifier.border(
                                    1.dp,
                                    Color.White,
                                    RoundedCornerShape(20.dp)
                                )
                            }
                        )
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = tab,
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
        // Kategori dropdown
        DropdownFilterButton(text = "Kategori", onClick = { /* Visual only for now */ })

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
fun SeriesCard(drama: MainResponseDrama) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Poster
        AsyncImage(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(drama.coverLink ?: drama.cover)
                    .crossfade(true)
                    .build(),
            contentDescription = drama.title,
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
            text = drama.title ?: "",
            color = Color.White,
            fontFamily = KumbhSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
