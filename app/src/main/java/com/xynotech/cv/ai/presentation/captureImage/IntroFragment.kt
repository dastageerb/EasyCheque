package com.xynotech.cv.ai.presentation.captureImage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.xynotech.converso.ai.R
import com.xynotech.cv.ai.utils.GreenButton
import com.xynotech.cv.ai.utils.PoweredByXynotechWhite
import com.xynotech.cv.ai.utils.font
import com.xynotech.cv.ai.utils.fontBold
import com.xynotech.cv.ai.utils.greenColor
import com.xynotech.cv.ai.utils.themeGradient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroFragment : Fragment() {

    val sharedViewModel:CaptureSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                Box(modifier = Modifier
                    .background(
                        themeGradient()
                    )
                    .fillMaxSize()) {
                    IntroScreenComposable()

                    PoweredByXynotechWhite(modifier =
                    Modifier
                        .width(100.dp)
                        .align(Alignment.BottomCenter)
                        .height(60.dp)
                    )
                }
            }
        }
    }
    @Composable
    fun IntroScreenComposable() {
        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                , horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(100.dp))

            Image(painter = painterResource(id = R.drawable.meezan_bank_logo),
                contentDescription = null,
                modifier = Modifier
                    .width(160.dp)
                    .height(160.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Deposit Cheque",
                textAlign = TextAlign.Center, color = Color.White,
                fontSize = 36.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(0.35f), fontFamily = fontBold()
            )

            Image(painter = painterResource(id = R.drawable.brush_effect),
                contentDescription = null,
                modifier = Modifier
                    .width(160.dp)
                    .height(40.dp))


            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Deposit your Meezan cheque digitally without visiting the bank",
                textAlign = TextAlign.Center, color = Color.White,
                fontSize = 18.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(0.8f), fontFamily = fontBold()
            )

            Spacer(modifier = Modifier.height(32.dp))

            GreenButton(modifier = Modifier
                .height(60.dp)
                .fillMaxWidth(0.7f)
                .background(
                    color = greenColor(),
                    shape = RoundedCornerShape(10)
                )
                , text = "GET STARTED",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold) {
                sharedViewModel.scannedQRResult = null
                findNavController().navigate(R.id.action_introFragment_to_capturingFragment)
            }
        }
    }

    @Composable
    @Preview
    fun preview() {

        Box(modifier = Modifier.fillMaxSize()) {
            IntroScreenComposable()

            PoweredByXynotechWhite(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}





//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        //requireActivity().requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//
//        binding.fragmentIntroButtonCapture.setOnClickListener() {
//            sharedViewModel.scannedQRResult = null
//            findNavController().navigate(R.id.action_introFragment_to_capturingFragment)
//        }
//    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }