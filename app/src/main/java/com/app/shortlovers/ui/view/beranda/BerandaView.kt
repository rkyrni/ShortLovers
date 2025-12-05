package com.app.shortlovers.ui.view.beranda

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.shortlovers.R
import com.app.shortlovers.ui.theme.BaseYellow
import com.app.shortlovers.ui.theme.BaseBackground
import com.app.shortlovers.ui.theme.BaseBlack
import com.app.shortlovers.viewModel.beranda.BerandaViewModel
import kotlinx.coroutines.delay

@Composable
fun BerandaView(viewModel: BerandaViewModel = viewModel()) {
    val greeting by viewModel.greeting.collectAsState()

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize()
            .background(color = BaseBackground)
    )
    {
        HeaderBar()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(all = 0.dp)
        ) {
            CarouselComponent()

            Spacer(modifier = Modifier.height(15.dp))

            ScrollableMenu()

        }
    }


}

@Composable
fun HeaderBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BaseBlack)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Ikon Menu
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        // Judul Tengah
        Text(
            text = "NontonYok",
            color = BaseYellow,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // Ikon Search
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun CarouselComponent() {
    val images = listOf(
        R.drawable.poster1,
        R.drawable.poster2,
        R.drawable.poster3
    )

    val titles = listOf(
        "Sang Konglomerat Terlahir Kembali",
        "Cinta di Balik Langit Jingga",
        "Petualangan Tanpa Akhir"
    )

    val pagerState = rememberPagerState(pageCount = { images.size })

    // Auto scroll
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000) // Delay 3 detik
            val nextPage = (pagerState.currentPage + 1) % images.size
            pagerState.animateScrollToPage(nextPage)
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
                Image(
                    painter = painterResource(id = images[page]),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay teks
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = titles[page],
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Indikator (titik di bawah)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        ) {
            repeat(images.size) { index ->
                val color =
                    if (pagerState.currentPage == index) Color.Yellow else Color.Gray
                Box(
                    modifier = Modifier
                        .padding(3.dp)
                        .size(8.dp)
                        .background(color, shape = CircleShape)
                )
            }
        }
    }
}

@Composable
fun ScrollableMenu() {
    val items = listOf("Utama", "Terbaru", "Populer", "Semua", "Film", "Serial", "Anime")
    var selectedItem by remember { mutableStateOf("Utama") }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(items) {idx, item ->
            val isSelected = item == selectedItem
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Color(0xFF6B5B00) else Color(0xFF2C2C2C))
                    .clickable() { selectedItem = item }
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = item,
                    color = if (isSelected) BaseYellow else Color.LightGray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
