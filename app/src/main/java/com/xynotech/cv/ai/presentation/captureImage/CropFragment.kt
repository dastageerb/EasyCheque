package com.xynotech.cv.ai.presentation.captureImage

import android.R.attr
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.MultiFormatReader
import com.xynotech.converso.ai.databinding.FragmentCropBinding
import com.xynotech.cv.ai.utils.NetworkResource
import com.xynotech.cv.ai.utils.hide
import com.xynotech.cv.ai.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CropFragment : Fragment() {

    var _binding: FragmentCropBinding? = null
    val binding get() = _binding!!

    val sharedViewModel: CaptureSharedViewModel by activityViewModels()

    val uploadViewModel: UploadImageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCropBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.capturedBitmap?.let { bitmap ->
            sharedViewModel.scannedQRResult?.let {
                uploadViewModel.uploadImage(bitmap, it)
                sharedViewModel.scannedQRResult = null
            }

            viewLifecycleOwner.lifecycleScope.launch {
                uploadViewModel.state.collect {
                    when(it) {
                        is NetworkResource.Success -> {
                            binding.processingCard.hide()
                            binding.errorCard.hide()
                            binding.successTextView.text = it.data.toString()
                            binding.successCard.show()
                        }

                        is NetworkResource.Error -> {
                            binding.errorTextView.text = it.msg
                            binding.processingCard.hide()
                            binding.errorCard.show()
                            binding.successCard.hide()
                        }

                        is NetworkResource.Loading -> {
                            binding.processingCard.show()
                            binding.errorCard.hide()
                            binding.successCard.hide()
                        }
                    }


                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        sharedViewModel.capturedBitmap = null
        _binding = null
    }

}