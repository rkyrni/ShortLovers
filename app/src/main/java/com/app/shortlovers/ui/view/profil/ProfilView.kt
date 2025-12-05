package com.app.shortlovers.ui.view.profil

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.shortlovers.viewModel.profile.ProfileViewModel
import com.app.shortlovers.R
import com.app.shortlovers.ui.theme.BaseYellow

@Composable
fun ProfilView(viewModel: ProfileViewModel = viewModel()) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
//        Image(
//            painter = painterResource(id = R.drawable.bg_1),
//            contentDescription = null,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.fillMaxSize()
//        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.8f),
                            Color.Black.copy(alpha = 0.8f),
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Buka Pengalaman",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Pribadi Anda",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(15.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Nikmati film dan acara TV favorit Anda,",
                    color = Color.White,
                    fontSize = 19.sp,
                    modifier = Modifier.padding(0.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "dengan rekomendasi yang",
                    color = Color.White,
                    fontSize = 19.sp,
                    modifier = Modifier.padding(0.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "dipersonalisasi hanya untuk Anda.",
                    color = Color.White,
                    fontSize = 19.sp,
                    modifier = Modifier.padding(0.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(50.dp))



            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 19.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BaseYellow),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Masuk",
                    color = Color.Black,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}