package com.example.multilocator.components.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun ShadedBox(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .padding(20.dp)
            .clip(RoundedCornerShape(30.dp))
            .height(40.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = MaterialTheme.typography.headlineSmall.fontSize
            )
        )
    }
}