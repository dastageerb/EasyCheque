package com.xynotech.cv.ai.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

        Column(
            Modifier
                .fillMaxWidth(0.75f)
                .height(300.dp)
        ) {

            if (dialogUiState == DialogUiState.ERROR) {
                ImageWithTextView(
                    modifier = Modifier.fillMaxWidth(),
                    image = R.drawable.signature_not_verified_icon,
                    text = text
                )
            }

            if (dialogUiState == DialogUiState.LOADING) {
                LoadingView(modifier = Modifier, text = text)
            }
        }
    }
}