package com.xynotech.cv.ai.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.mlkit.vision.common.InputImage
import com.xynotech.converso.ai.R
import com.xynotech.cv.ai.domain.CheckVerificationResponse
import com.xynotech.cv.ai.domain.Comparison
import com.xynotech.cv.ai.domain.ExtractedText
import com.xynotech.cv.ai.presentation.captureImage.UploadImageViewModel
import com.xynotech.cv.ai.utils.GreenButton
import com.xynotech.cv.ai.utils.GreyButton
import com.xynotech.cv.ai.utils.ImageWithTextView
import com.xynotech.cv.ai.utils.NetworkResource
import com.xynotech.cv.ai.utils.PoweredByXynotechBlack
import com.xynotech.cv.ai.utils.buttonGrey
import com.xynotech.cv.ai.utils.font
import com.xynotech.cv.ai.utils.greenColor

class SignatureVerificationFragment : Fragment() {

    val uploadViewModel: UploadImageViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState = uploadViewModel.state.collectAsStateWithLifecycle()
                when (uiState.value) {
                    is NetworkResource.Success -> {
                        (uiState.value as NetworkResource.Success<CheckVerificationResponse>).data?.comparison?.confidence?.let {
                            SignatureVerificationComposable(confidenceValue = it)
                        } ?: kotlin.run {
                            SignatureVerificationComposable(confidenceValue = -2.00)
                        }
                    }

                    else -> {
                        SignatureVerificationComposable(confidenceValue = -2.00)
                    }
                }
            }
        }
    }

    @Composable
    private fun SignatureVerificationComposable(confidenceValue: Double) {
        Box(Modifier.fillMaxSize()) {
            Text(
                text = "Cheque Analyser",
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily(Font(R.font.arialbd)),
                modifier = Modifier
                    .padding(top = 24.dp)
                    .align(Alignment.TopCenter)
            )

            SignatureStatus(
                modifier = Modifier.align(Alignment.Center),
                confidenceValue = confidenceValue
            )

            Column(
                Modifier.align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                GreyButton(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(
                            buttonGrey(),
                            shape = RoundedCornerShape(10)
                        )
                        .height(40.dp), text = "BACK",
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                ) {
                }

                Spacer(modifier = Modifier.height(8.dp))

                GreenButton(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(
                            greenColor(), shape = RoundedCornerShape(10)
                        )
                        .height(40.dp), text = "SUBMIT",
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                ) {

                }
                PoweredByXynotechBlack(
                    modifier = Modifier
                        .width(100.dp)
                        .height(60.dp)
                )
            }
        }
    }

    @Composable
    fun SignatureStatus(modifier: Modifier, confidenceValue: Double) =
        when {
            confidenceValue > 80 -> {
                ImageWithTextView(
                    modifier = modifier,
                    image = R.drawable.signature_verified_icon,
                    text = "Signature verified"
                )
            }

            confidenceValue > 60 && confidenceValue < 80 -> {
                ImageWithTextView(
                    modifier = modifier,
                    image = R.drawable.ic_human_verification_required,
                    text = "Consult an agent to verify the signature"
                )
            }

            confidenceValue > -1 && confidenceValue < 60 -> {
                ImageWithTextView(
                    modifier = modifier,
                    image = R.drawable.signature_not_verified_icon,
                    text = "Unable to verify Signature"
                )
            }

            else -> {
                ImageWithTextView(
                    modifier = modifier,
                    image = R.drawable.baseline_error_24,
                    text = "Something Went Wrong"
                )
            }
        }
}
