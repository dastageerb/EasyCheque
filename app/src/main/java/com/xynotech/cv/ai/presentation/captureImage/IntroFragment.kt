package com.xynotech.cv.ai.presentation.captureImage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.xynotech.converso.ai.R
import com.xynotech.cv.ai.utils.themeGradient
import dagger.hilt.android.AndroidEntryPoint
import kotlin.coroutines.jvm.internal.CompletedContinuation.context

@AndroidEntryPoint
class IntroFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                IntroScreenComposable()
            }
        }
    }
}

@Composable
@Preview
fun preview() {
    IntroScreenComposable()
}

@Composable
fun IntroScreenComposable() {
    Column(
        Modifier
            .fillMaxSize()
            .background(
                themeGradient()
            ), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(100.dp))

        Image(painter = painterResource(id = R.drawable.meezan_bank_logo),
            contentDescription = null,
            modifier = Modifier
                .width(160.dp)
                .height(160.dp))

        Text(text = "Deposit Cheque",
            textAlign = TextAlign.Center, color = Color.White,
            fontSize = 36.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(0.35f)
        )


        // R.drawable.meezan_bank_logo
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