package com.xynotech.cv.ai.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xynotech.converso.ai.R

@Composable
    fun ImageWithTextView(modifier: Modifier, image: Int, text: String) =
        Column(modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Image(painter = painterResource(id = image),
                contentDescription = null, modifier = Modifier
                    .size(100.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = text, fontSize = 26.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.arialbd))
            , textAlign = TextAlign.Center)
        }