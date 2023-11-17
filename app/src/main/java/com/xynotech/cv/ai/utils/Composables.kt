package com.xynotech.cv.ai.utils

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import com.xynotech.converso.ai.R

@Composable
fun themeGradient() = Brush.verticalGradient(
    listOf(
        colorResource(id = R.color.color_primary_light),
        colorResource(id = R.color.color_primary_dark)
    )
)


@Composable
fun GreenButton(modifier: Modifier,text:String, onClick:()->Unit) {
    Button(
        modifier = Modifier,
        onClick = {
        onClick()
    }, colors = ButtonDefaults.buttonColors(containerColor = greenColor())
    ) {
      Text(text = text, color = colorResource(id = R.color.white))
    }
}


@Composable
fun GreyButton(modifier: Modifier,text:String, onClick:()->Unit) {
    Button(
        modifier = Modifier,
        onClick = {
            onClick()
        }, colors = ButtonDefaults.buttonColors(containerColor = buttonGrey())
    ) {
        Text(text = text, color = colorResource(id = R.color.black))
    }
}

@Composable
fun PoweredByImage() {

}


@Composable
fun buttonGrey() = colorResource(id = R.color.color_grey_button)

@Composable
fun greenColor() = colorResource(id = R.color.color_green)