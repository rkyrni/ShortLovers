package com.app.shortlovers.ui.view.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.shortlovers.ui.theme.BaseYellow
import com.app.shortlovers.viewModel.profile.ProfileViewModel

@Composable
fun ProfileView(viewModel: ProfileViewModel = viewModel()) {

    Box(modifier = Modifier.fillMaxSize()) {
        //        Image(
        //            painter = painterResource(id = R.drawable.bg_1),
        //            contentDescription = null,
        //            contentScale = ContentScale.Crop,
        //            modifier = Modifier.fillMaxSize()
        //        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors =
                                listOf(
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
                text = "Unlock Your",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Personal Experience",
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
                    text = "Enjoy your favorite movies and TV shows,",
                    color = Color.White,
                    fontSize = 19.sp,
                    modifier = Modifier.padding(0.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "with recommendations",
                    color = Color.White,
                    fontSize = 19.sp,
                    modifier = Modifier.padding(0.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "personalized just for you.",
                    color = Color.White,
                    fontSize = 19.sp,
                    modifier = Modifier.padding(0.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 19.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BaseYellow),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Sign In",
                    color = Color.Black,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}
