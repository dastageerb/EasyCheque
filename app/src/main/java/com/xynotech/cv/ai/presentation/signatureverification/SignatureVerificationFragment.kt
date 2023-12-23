package com.xynotech.cv.ai.presentation.signatureverification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.xynotech.converso.ai.R
import com.xynotech.cv.ai.domain.CheckVerificationResponse
import com.xynotech.cv.ai.presentation.captureImage.CaptureSharedViewModel
import com.xynotech.cv.ai.utils.CustomDialog
import com.xynotech.cv.ai.utils.DialogUiState
import com.xynotech.cv.ai.utils.GreenButton
import com.xynotech.cv.ai.utils.GreyButton
import com.xynotech.cv.ai.utils.ImageWithTextView
import com.xynotech.cv.ai.utils.NetworkResource
import com.xynotech.cv.ai.utils.PoweredByXynotechBlack
import com.xynotech.cv.ai.utils.buttonGrey
import com.xynotech.cv.ai.utils.greenColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignatureVerificationFragment : Fragment() {

    val viewModel: SignatureVerificationViewModel by viewModels()

    val sharedViewModel: CaptureSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            sharedViewModel.filePath?.let { viewModel.analyseCheck(it) }
            setContent {
                val uiState = viewModel.uiState.collectAsStateWithLifecycle()
                when (uiState.value) {
                    is SignatureVerificationResponse.ApiSuccess -> {
                        (uiState.value as SignatureVerificationResponse.ApiSuccess<VerifyCheckResponse>).data?.data?.let {
                            SignatureVerificationComposable(confidenceValue = it.confidence ?: 0.00)
                        } ?: kotlin.run {
                            SignatureVerificationComposable(confidenceValue = -2.00, error = true)
                        }
                    }

                    is SignatureVerificationResponse.Navigate -> {
                        sharedViewModel.capturedBitmap = null
                        sharedViewModel.scannedQRResult = null
                        sharedViewModel.filePath = null
                        findNavController().navigate(R.id.action_signatureVerificationFragment_to_introFragment)
                    }

                    is SignatureVerificationResponse.Loading -> {
                        CustomDialog(
                            dialogUiState = DialogUiState.LOADING,
                            shouldDismiss = false,
                            onDismiss = {
                                        viewModel.onRemoveErrorState()
                            },
                            text = "Analysing Cheque..."
                        )
                    }

                     is SignatureVerificationResponse.SUBMITTING-> {
                    CustomDialog(
                        dialogUiState = DialogUiState.LOADING,
                        shouldDismiss = false,
                        onDismiss = {
                            viewModel.onRemoveErrorState()
                        },
                        text = "Submitting Cheque..."
                    )
                }
                    is SignatureVerificationResponse.Error -> {
                        SignatureVerificationComposable(confidenceValue = -2.00, (uiState.value as SignatureVerificationResponse.Error<VerifyCheckResponse>).msg, error = true)
                    }
                    else -> {
                        SignatureVerificationComposable(confidenceValue = -2.00, error = true)
                    }
                }
            }
        }
    }

    @Composable
    private fun SignatureVerificationComposable(
        confidenceValue: Double, message: String? = null, error: Boolean = false
    ) {
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

            if (error) {
                ImageWithTextView(
                    modifier = Modifier.align(Alignment.Center),
                    image = R.drawable.baseline_error_24,
                    text = message ?: "Some error occurred"
                )
            } else {
                SignatureStatus(
                    modifier = Modifier
                        .align(Alignment.Center),
                    confidenceValue = confidenceValue
                )
            }
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
                    sharedViewModel.capturedBitmap = null
                    sharedViewModel.scannedQRResult = null
                    sharedViewModel.filePath = null
                    findNavController().navigate(R.id.action_signatureVerificationFragment_to_introFragment)
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (confidenceValue >= 60) {
                    GreenButton(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(
                                greenColor(), shape = RoundedCornerShape(10)
                            )
                            .height(40.dp), text = "SUBMIT",
                        fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                    ) {
                        viewModel.mockApi()
                    }
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
            confidenceValue >= 80 -> {
                ImageWithTextView(
                    modifier = modifier,
                    image = R.drawable.signature_verified_icon,
                    text = "Signature verified"
                )
            }

            confidenceValue >= 60 && confidenceValue < 80 -> {
                ImageWithTextView(
                    modifier = modifier,
                    image = R.drawable.ic_human_verification_required,
                    text = "Consult an agent to verify the signature"
                )
            }

            else -> {
                ImageWithTextView(
                    modifier = modifier,
                    image = R.drawable.signature_not_verified_icon,
                    text = "Unable to verify Signature"
                )
            }
        }
}
