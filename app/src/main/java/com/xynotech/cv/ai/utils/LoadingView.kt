package com.xynotech.cv.ai.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Preview
@Composable
fun previe() {

    Column(
        Modifier
            .fillMaxSize()
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

        LoadingView(modifier = Modifier, text = "Processing Check")
    }

}

@Composable
fun LoadingView(modifier:Modifier,text:String) {
    Card(
        Modifier
            .fillMaxWidth(0.75f), colors = CardDefaults.cardColors(containerColor = Color(0xFFD3EBD4))) {
        Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

            LoadingAnimation3()

            Spacer(Modifier.height(16.dp))

            Text(text = text, textAlign = TextAlign.Center, fontSize = 16.sp, fontFamily = font(), fontWeight = FontWeight.SemiBold)
        }
    }
}
