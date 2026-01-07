package com.app.shortlovers.ui.view.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.shortlovers.R
import com.app.shortlovers.ui.theme.BaseYellow
import com.app.shortlovers.viewModel.auth.StartedViewModel

@Composable
fun StartedView(modifier: Modifier = Modifier, viewModel: StartedViewModel = viewModel()) {

    val isChecked = viewModel.isChecked.value

    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.White, fontSize = 16.sp)) {
            append("I agree to the ")
        }
        pushStringAnnotation(tag = "TERMS", annotation = "https://example.com/terms")
        withStyle(
            style =
                SpanStyle(
                    color = Color(0xFF2196F3),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
        ) { append("Terms of Service") }
        pop()

        withStyle(style = SpanStyle(color = Color.White, fontSize = 16.sp)) { append(" and ") }

        pushStringAnnotation(tag = "PRIVACY", annotation = "https://example.com/privacy")
        withStyle(
            style =
                SpanStyle(
                    color = Color(0xFF2196F3), // blue
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
        ) { append("Privacy Policy") }
        pop()

        withStyle(style = SpanStyle(color = Color.White, fontSize = 16.sp)) { append(".") }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

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
                text = "Welcome to",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Your Cinema",
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { checked -> viewModel.onCheckboxChange(checked) },
                    colors = CheckboxDefaults.colors(checkedColor = BaseYellow)
                )
                ClickableText(
                    text = annotatedText,
                    onClick = { offset ->
                        annotatedText
                            .getStringAnnotations(
                                tag = "TERMS",
                                start = offset,
                                end = offset
                            )
                            .firstOrNull()
                            ?.let { annotation -> println("Click: ${annotation.item}") }

                        annotatedText
                            .getStringAnnotations(
                                tag = "PRIVACY",
                                start = offset,
                                end = offset
                            )
                            .firstOrNull()
                            ?.let { annotation -> println("Click: ${annotation.item}") }
                    }
                )
            }

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 19.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BaseYellow),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Get Started",
                    color = Color.Black,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}
