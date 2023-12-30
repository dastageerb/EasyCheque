package com.xynotech.cv.ai.presentation.processImage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
import com.xynotech.cv.ai.presentation.captureImage.UploadImageViewModel
import com.xynotech.cv.ai.utils.CustomDialog
import com.xynotech.cv.ai.utils.DialogUiState
import com.xynotech.cv.ai.utils.GreenButton
import com.xynotech.cv.ai.utils.GreyButton
import com.xynotech.cv.ai.utils.NetworkResource
import com.xynotech.cv.ai.utils.PoweredByXynotechBlack
import com.xynotech.cv.ai.utils.buttonGrey
import com.xynotech.cv.ai.utils.greenColor
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProcessFragment : Fragment() {

    val sharedViewModel: CaptureSharedViewModel by activityViewModels()

    val uploadViewModel: UploadImageViewModel by viewModels()

    val processViewModel: ProcessViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState = processViewModel.uiState.collectAsStateWithLifecycle()

                ProcessFragmentComposable()

                when(uiState.value) {
                    is NetworkResource.Success -> {
                        sharedViewModel.details = (uiState.value as NetworkResource.Success<CheckVerificationResponse>).data
                        sharedViewModel.filePath = (uiState.value as NetworkResource.Success<CheckVerificationResponse>).data?.data?.filePath

                        findNavController().navigate(R.id.action_processFragment_to_detailsFragment)
                    }

                    is NetworkResource.Loading -> {
                        CustomDialog(
                            dialogUiState = DialogUiState.LOADING,
                            shouldDismiss = false,
                            onDismiss = {  },
                            text = "Reading cheque contents..."
                        )
                    }

                    is NetworkResource.Error -> {
                        (uiState.value as NetworkResource.Error<CheckVerificationResponse>).msg?.let {
                            CustomDialog(
                                dialogUiState = DialogUiState.ERROR,
                                shouldDismiss = true,
                                onDismiss = {
                                    processViewModel.onRemoveErrorState()
                                },
                                text = it
                            )
                        }
                    }

                    is NetworkResource.NONE -> {

                    }
                }


            }
        }
    }

    @Preview
    @Composable
    fun preview() {
        ProcessFragmentComposable()
    }

    @Composable
    private fun ProcessFragmentComposable() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Submit Cheque", fontSize = 24.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.arial))
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Front side of your cheque", fontSize = 16.sp,
                    color = colorResource(id = R.color.color_grey),
                    fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.arial))
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.75f)
                        .fillMaxWidth(0.9f)
                        .background(
                            colorResource(id = R.color.grey_background),
                            shape = RoundedCornerShape(4)
                        )
                )
                {
                    Image(
                        bitmap = sharedViewModel.capturedBitmap?.asImageBitmap()!!,
                        contentDescription = null, modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                GreyButton(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(
                            buttonGrey(),
                            shape = RoundedCornerShape(10)
                        )
                        .height(40.dp), text = "RETAKE",
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                ) {
                    sharedViewModel.capturedBitmap = null
                    sharedViewModel.scannedQRResult = null
                    sharedViewModel.filePath = null
                    findNavController().clearBackStack(R.id.capturingFragment)
                    findNavController().popBackStack()
                }

                Spacer(modifier = Modifier.height(8.dp))

                GreenButton(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(
                            greenColor(), shape = RoundedCornerShape(10)
                        )
                        .height(40.dp)
                    , text = "CONTINUE",
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                ) {
                    if (sharedViewModel.capturedBitmap != null && sharedViewModel.scannedQRResult != null) {
                        processViewModel.processCheck(sharedViewModel.capturedBitmap!!, sharedViewModel.scannedQRResult!!)
                    }
                }
            }

            PoweredByXynotechBlack(modifier = Modifier
                .width(100.dp)
                .align(Alignment.BottomCenter)
                .height(60.dp))
        }
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        sharedViewModel.capturedBitmap?.let { bitmap ->
//            sharedViewModel.scannedQRResult?.let {
//                uploadViewModel.uploadImage(bitmap, it)
//                sharedViewModel.scannedQRResult = null
//            }
//
//            viewLifecycleOwner.lifecycleScope.launch {
//                uploadViewModel.state.collect {
//                    when(it) {
//                        is NetworkResource.Success -> {
//                            binding.processingCard.hide()
//                            binding.errorCard.hide()
//
//                            binding.txtName.text = "Name : "+it.data?.comparison?.extractedText?.name
//
//                            binding.txtamount.text = "Amount : "+it.data?.comparison?.extractedText?.amountInDigits
//
//                            binding.txtAmountInWords.text = "Amount in Words : "+it.data?.comparison?.extractedText?.amountInWords
//
//                            try {
//                                val score = it.data?.comparison?.confidence ?:0.0
//                                if (score > 80) {
//                                        binding.txtConfidenceValue.setTextColor(
//                                            ContextCompat.getColor(requireContext(), R.color.color_green)
//                                        )
//
//
//                                } else if ( score > 60 && score <80) {
//
//                                    binding.txtConfidenceValue.setTextColor(
//                                        ContextCompat.getColor(requireContext(), R.color.color_orange)
//                                    )
//
//                                    binding.txtConfidenceValue.text = "Consult agent to verify signature"
//
//
//                                }
//                                else  {
//                                    binding.txtConfidenceValue.setTextColor(
//                                        ContextCompat.getColor(requireContext(), R.color.color_red)
//                                    )
//
//                                    binding.txtConfidenceValue.text = "Signature not verified"
//
//                                }
//
//                            } catch (e:Exception) {
//
//                            }
//
//                            binding.txtVerified.text = if (compareAmounts(it.data?.comparison?.
//                                extractedText?.amountInWords?:"",
//                                it.data?.comparison?.extractedText?.converted?:
//                                "")) "Amount verified" else "Amount does not match"
//
//                            binding.successCard.show()
//                        }
//
//                        is NetworkResource.Error -> {
//                            binding.errorTextView.text = it.msg
//                            binding.processingCard.hide()
//                            binding.errorCard.show()
//                            binding.successCard.hide()
//                        }
//
//                        is NetworkResource.Loading -> {
//                            binding.processingCard.show()
//                            binding.errorCard.hide()
//                            binding.successCard.hide()
//                        }
//                    }
//                }
//            }
//        }
//
//        binding.buttonSubmit.setOnClickListener() {
//            viewLifecycleOwner.lifecycleScope.launch() {
//                binding.textProcessing.text = "Submiting"
//                binding.processingCard.show()
//                binding.successCard.hide()
//                delay(2500)
//                findNavController().navigate(R.id.action_cropFragment_to_introFragment)
//            }
//        }
//
//        binding.buttonCancel.setOnClickListener {
//            findNavController().navigate(R.id.action_cropFragment_to_introFragment)
//        }
//
//    }

    fun refineText(rawText: String): String {
        val  commaRefinedText = rawText
            .replace(",", "")
            .replace("-","")
            .replace("rupees","")
            .replace("only","")
            .trim()
        return commaRefinedText.lowercase();
    }

    fun compareAmounts(amount1: String, amount2: String): Boolean {

        val amt1 = this.refineText(amount1)
        val amt2 = this.refineText(amount2)

        Log.d("1234", "compareAmounts: "+amount1 + " -> "+amt1)
        Log.d("1234", "compareAmounts: "+amount2 + " -> "+amt2)
        return amt1 == amt2
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedViewModel.capturedBitmap = null

    }
}