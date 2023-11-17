package com.xynotech.cv.ai.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xynotech.converso.ai.R

@Composable
fun themeGradient() = Brush.verticalGradient(
    listOf(
        colorResource(id = R.color.color_primary_light),
        colorResource(id = R.color.color_primary_dark)
    )
)

@Composable
fun GreenButton(modifier: Modifier,
                text: String, fontSize: TextUnit,
                fontWeight: FontWeight, fontFamily: FontFamily = font(), onClick: () -> Unit) {
    Button(
        modifier = modifier,
        onClick = {
            onClick()
        }, colors = ButtonDefaults.buttonColors(containerColor = greenColor())
    ) {
        Text(text = text, color = colorResource(id = R.color.white), fontSize = fontSize, fontWeight = fontWeight, fontFamily = fontFamily)
    }
}

@Composable
fun GreyButton(modifier: Modifier, text:String, fontSize: TextUnit, fontWeight: FontWeight, onClick:()->Unit) {
    Button(
        modifier = modifier,
        onClick = {
            onClick()
        }, colors = ButtonDefaults.buttonColors(containerColor = buttonGrey() )
    ) {
        Text(text = text, color = colorResource(id = R.color.black), fontSize = fontSize, fontWeight = fontWeight)
    }
}

@Composable
fun PoweredByXynotechWhite(modifier: Modifier = Modifier) {
    Image(painter = painterResource(id = R.drawable.powered_by_xynotech),
        contentDescription = null, modifier = modifier)
}


@Composable
fun PoweredByXynotechBlack(modifier: Modifier = Modifier) {
    Image(painter = painterResource(id = R.drawable.powered_by_xynotech_black),
        contentDescription = null, modifier = modifier)
}

// R.drawable.powered_by_xynotech


@Composable
fun font() = FontFamily(Font(R.font.arial))
@Composable
fun fontBold() = FontFamily(Font(R.font.arialbd))


@Composable
fun buttonGrey() = colorResource(id = R.color.color_grey)

@Composable
fun greenColor() = colorResource(id = R.color.color_green)