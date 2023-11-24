package com.xynotech.cv.ai.presentation.details

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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.xynotech.converso.ai.R
import com.xynotech.cv.ai.domain.CheckVerificationResponse
import com.xynotech.cv.ai.presentation.captureImage.CaptureSharedViewModel
import com.xynotech.cv.ai.presentation.captureImage.UploadImageViewModel
import com.xynotech.cv.ai.utils.GreenButton
import com.xynotech.cv.ai.utils.GreyButton
import com.xynotech.cv.ai.utils.PoweredByXynotechBlack
import com.xynotech.cv.ai.utils.buttonGrey
import com.xynotech.cv.ai.utils.font
import com.xynotech.cv.ai.utils.greenColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment: Fragment() {


    val detailsViewModel: DetailsViewModel by viewModels()

    val sharedViewModel: CaptureSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState = detailsViewModel.uiState.collectAsStateWithLifecycle()
            DetailsScreenComposable(uiState.value)

            }
    }}

    override fun onStart() {
        super.onStart()
        detailsViewModel.onStart(sharedViewModel.details)
    }

    @Composable
    private fun DetailsScreenComposable(value: CheckVerificationResponse) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Review Cheque", fontSize = 24.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.arial))
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Do not dispose the cheque until 06 months after it is processed, You can check the ongoing status in the cheque Section",
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.color_grey),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily(Font(R.font.arial)),
                    modifier = Modifier.fillMaxWidth(0.8f),
                    textAlign = TextAlign.Center
                )

                DetailsItemComposable(
                    modifier = Modifier,
                    detailType = "Name", R.drawable.person_icon,
                    value.data?.name?:""
                )

                Spacer(modifier = Modifier.height(16.dp))

                DetailsItemComposable(
                    detailType = "Amount",
                    imageRes = R.drawable.baseline_money_24,
                    valueText = value.data?.amountInDigits?:""
                )

                Spacer(modifier = Modifier.height(16.dp))

                AmountInWords(
                    detailType = "Amount in words",
                    imageRes = R.drawable.pkr_icon,
                    valueText = value.data?.amountInWords?:"",
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                DetailsItemComposable(
                    detailType = "Date",
                    imageRes = R.drawable.calender_icon,
                    valueText = value.data?.date?:""
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                    findNavController().navigate(R.id.action_detailsFragment_to_introFragment)
                }

                Spacer(modifier = Modifier.height(8.dp))

                GreenButton(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(
                            greenColor(), shape = RoundedCornerShape(10)
                        )
                        .height(40.dp), text = "CONTINUE",
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                ) {
                    findNavController().navigate(R.id.action_detailsFragment_to_signatureVerificationFragment)
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


    @Preview
    @Composable
    fun prev() {
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .background(Color.White)) {

            DetailsItemComposable(modifier = Modifier.constrainAs(createRef())
            {
            centerTo(parent)
            },
                // R.drawable.person_icon
                detailType = "Name", R.drawable.person_icon,"Nabeel Baig")

        }
    }

    @Composable
    fun DetailsItemComposable(
        modifier: Modifier = Modifier,
        detailType: String,
                              imageRes:Int, valueText:String

    , fontSize:TextUnit = 18.sp
    ) {
        Column(
            modifier
                .height(90.dp)
                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(text = detailType,
                color = Color.Black, fontFamily = font(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold, modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 26.dp))
            Card(
                Modifier
                    .fillMaxWidth(0.9f)
                    .defaultMinSize(100.dp)
                    .shadow(
                        ambientColor = colorResource(id = R.color.color_grey),
                        elevation = 12.dp
                    ), colors = CardDefaults.cardColors(containerColor = Color.White)

            ) {
                ConstraintLayout(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    val (image, text) = createRefs()
                    Image(painter = painterResource(id = imageRes),
                        contentDescription = null, modifier = Modifier
                            .size(40.dp)
                            .constrainAs(image)
                            {
                                start.linkTo(parent.start, 16.dp)
                                centerVerticallyTo(parent)
                            })

                    Text(text = valueText,
                        color = Color.Black, fontFamily = font(), fontSize = fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.constrainAs(text) {
                            centerVerticallyTo(parent)
                            start.linkTo(image.end, 24.dp)
                        })

                }
            }
        }
    }
@Composable
fun AmountInWords(
    modifier: Modifier = Modifier,
    detailType: String,
    imageRes:Int, valueText:String

    , fontSize:TextUnit = 18.sp
) {
    Column(
        modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 90.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text(text = detailType,
            color = Color.Black, fontFamily = font(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold, modifier = Modifier
                .fillMaxWidth()
                .padding(start = 26.dp))
        Card(
            Modifier
                .fillMaxWidth(0.9f)
                .defaultMinSize(minHeight = 90.dp)
                .shadow(
                    ambientColor = colorResource(id = R.color.color_grey),
                    elevation = 12.dp
                ), colors = CardDefaults.cardColors(containerColor = Color.White)

        ) {
            ConstraintLayout(
                Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 90.dp)
            ) {
                val (image, text) = createRefs()
                Image(painter = painterResource(id = imageRes),
                    contentDescription = null, modifier = Modifier
                        .size(40.dp)
                        .constrainAs(image)
                        {
                            start.linkTo(parent.start, 16.dp)
                            centerVerticallyTo(parent)
                        })

                Text(text = valueText,
                    color = Color.Black, fontFamily = font(), fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .wrapContentHeight()
                        .constrainAs(text) {
                        centerVerticallyTo(parent)
                        start.linkTo(image.end, 24.dp)
                            end.linkTo(parent.end, 24.dp)
                    })

            }
        }
    }
}
