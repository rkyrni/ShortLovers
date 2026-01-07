package com.app.shortlovers.ui.view.daftarSaya

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.app.shortlovers.core.models.MovieItem
import com.app.shortlovers.ui.theme.BaseBackground
import com.app.shortlovers.ui.theme.BaseYellow
import com.app.shortlovers.viewModel.daftarSaya.DaftarSayaViewModel


@Composable
fun DaftarSayaView(viewModel: DaftarSayaViewModel = viewModel()) {

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Lanjut Nonton", "Disimpan", "Disukai")

    Column(
        Modifier
            .fillMaxSize()
            .background(BaseBackground)
    ) {
        // --- Header Tabs ---
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF101010),
            contentColor = BaseYellow,
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = BaseYellow
                    )
                }
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            title,
                            color = if (selectedTab == index) BaseYellow else Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (selectedTab) {
            0 -> {
                // 🟡 TAB 1: Lanjut Nonton
                FilterBarSection()
                MovieGridSection(viewModel)
            }

            1 -> {
                // 🟢 TAB 2: Disimpan (dummy)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📁 Belum ada film yang disimpan", color = Color.White, fontSize = 16.sp)
                }
            }

            2 -> {
                // 🔵 TAB 3: Disukai (dummy)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("❤️ Belum ada film yang disukai", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun FilterBarSection() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilterChip("Kategori") { println("Kategori diklik") }
        FilterChip("Tanggal") { println("Tanggal diklik") }
        FilterChip("A - Z") { println("A - Z diklik") }
    }
}

@Composable
fun MovieGridSection(viewModel: DaftarSayaViewModel) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {
        items(viewModel.movies) { movie ->
            MovieCard(movie)
        }
    }
}

@Composable
fun FilterChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(Color(0xFF0D0D0D), shape = RoundedCornerShape(6.dp)) // corner radius kecil
            .border(
                width = 1.dp,
                color = BaseYellow,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable() { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                color = BaseYellow,
                fontSize = 14.sp
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Dropdown",
                tint = BaseYellow,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun MovieCard(movie: MovieItem) {
    Column(
        Modifier
            .padding(6.dp)
            .width(160.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(movie.imageUrl),
            contentDescription = movie.title,
            modifier = Modifier
                .height(210.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )
        Text(
            movie.title,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            maxLines = 1,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            "${movie.rating} • ${movie.year}",
            color = BaseYellow,
            fontSize = 13.sp,
            textAlign = TextAlign.Start
        )
    }
}