package com.xynotech.cv.ai.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.xynotech.converso.ai.R

enum class DialogUiState {
    ERROR, LOADING
}

@Composable
fun CustomDialog(
    dialogUiState: DialogUiState,
    shouldDismiss: Boolean,
    onDismiss: () -> Unit,
    text: String
) {
    Dialog(
        onDismissRequest = {
            if (shouldDismiss) {
                onDismiss()
            }
        }, properties = DialogProperties(
            dismissOnBackPress = shouldDismiss,
            dismissOnClickOutside = shouldDismiss,
            usePlatformDefaultWidth = false
        )
    ) {


        if (dialogUiState == DialogUiState.ERROR) {

            Column(
                Modifier

                    .fillMaxWidth(0.85f)
                    .background(Color.White, shape = RoundedCornerShape(6))
                    .height(300.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ImageWithTextView(
                    modifier = Modifier
                        .fillMaxHeight(),
                    image = R.drawable.signature_not_verified_icon,
                    text = text
                )
            }
        }

        if (dialogUiState == DialogUiState.LOADING) {

            Column(
                Modifier.fillMaxWidth(), horizontalAlignment =
                Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                LoadingView(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth(), text = text
                )
            }
        }

    }
}