package com.example.multilocator.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.multilocator.R
import com.example.multilocator.components.utils.Colors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel()
) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .background(Colors.backgroundColor),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Multi Locator", fontSize = 30.sp)

            LottieAnimation(
                composition = composition,
                iterations = Int.SIZE_BITS,
                speed = 1.5f,
                modifier = Modifier
                    .size(140.dp),
                contentScale = ContentScale.Crop
            )
        }
    }

    LaunchedEffect(key1 = true) {
        delay(600L)
        viewModel.onAppStart(openAndPopUp)
    }

}