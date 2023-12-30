package com.xynotech.cv.ai.presentation.verifiedChequeScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
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
import androidx.navigation.fragment.findNavController
import com.xynotech.converso.ai.R
import com.xynotech.cv.ai.utils.GreenButton
import com.xynotech.cv.ai.utils.GreyButton
import com.xynotech.cv.ai.utils.ImageWithTextView
import com.xynotech.cv.ai.utils.PoweredByXynotechBlack
import com.xynotech.cv.ai.utils.buttonGrey
import com.xynotech.cv.ai.utils.greenColor


class VerifiedChequeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                CheckVerificationScreen()
            }
        }
    }


    @Composable
    fun CheckVerificationScreen() {
        Box(Modifier.fillMaxSize()) {
            Text(
                text = "Cheque Submitted",
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily(Font(R.font.arialbd)),
                modifier = Modifier
                    .padding(top = 24.dp)
                    .align(Alignment.TopCenter)
            )


            /// bank_cheque_verified
            ImageWithTextView(
                modifier = Modifier.align(Alignment.Center),
                image = R.drawable.bank_cheque_verified,
                text = "Cheque Verified"
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

                    findNavController().navigate(R.id.action_verifiedChequeFragment_to_introFragment)
                }

                PoweredByXynotechBlack(
                    modifier = Modifier
                        .width(100.dp)
                        .height(60.dp)
                )
            }
        }
    }

}