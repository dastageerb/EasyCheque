package com.xynotech.cv.ai.presentation.captureImage.capture

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.xynotech.converso.ai.R
import com.xynotech.converso.ai.databinding.FragmentCapturingBinding
import com.xynotech.cv.ai.presentation.captureImage.CaptureSharedViewModel
import com.xynotech.cv.ai.presentation.captureImage.capture.objectoverlay.BoxData
import com.xynotech.cv.ai.presentation.captureImage.capture.objectoverlay.DetectedObjects
import com.xynotech.cv.ai.utils.hide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@AndroidEntryPoint
@ExperimentalGetImage
class CapturingFragment : Fragment() {

    private var _binding: FragmentCapturingBinding? = null

    private val binding get() = _binding!!

    val sharedViewModel: CaptureSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCapturingBinding.inflate(inflater,container,false)
        return _binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var imageCapture: ImageCapture
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var preview: Preview

    private var detector: ObjectDetector? = null
    private var scanner:BarcodeScanner? = null

    var allowCapture = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (requireContext().hasCameraPermission()) {
            startCamera()
        } else {
            multiPermissionCallback.launch(arrayOf(Manifest.permission.CAMERA))
        }

        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableClassification()
            .build()

        detector = ObjectDetection.getClient(options)


        val barcodeOptions = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_DATA_MATRIX
            )
            .build()
        scanner = BarcodeScanning.getClient(barcodeOptions)



        val animation: Animation =
            AlphaAnimation(1f, 0.1f)
        animation.setDuration(150)
        animation.setInterpolator(LinearInterpolator())
        animation.setRepeatCount(Animation.INFINITE)
        animation.setRepeatMode(Animation.REVERSE)
        binding.qrIndicator?.startAnimation(animation)

        binding.fragmentCaptureTakePictureButton.setOnClickListener {
            if (!allowCapture) {
                Toast.makeText(requireContext(), "Please adjust cheques inside frame", Toast.LENGTH_SHORT).show()
            }
            else if (sharedViewModel.scannedQRResult == null) {
                captureViewFinder()
            }
            else {
                lifecycleScope.launch {
                    if (sharedViewModel.capturedBitmap == null) {
                        sharedViewModel.capturedBitmap = getViewFinderImage()
                    }
                }
                findNavController().navigate(R.id.action_capturingFragment_to_processFragment)
            }
        }
    }
    fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder()
                .build()
            imageCapture = ImageCapture.Builder()
                .build()


            imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()


            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            imageAnalysis.setAnalyzer(cameraExecutor) {
                if (sharedViewModel.scannedQRResult == null) {
                    viewLifecycleOwner.lifecycleScope.launch() {
                        getViewFinderImage()?.let { it1 ->
                            scanQRCode(it1) }
                    }
                }

                if (sharedViewModel.scannedQRResult != null) {
                    viewLifecycleOwner.lifecycleScope.launch() {
                        getViewFinderImage()?.let { it1 ->
                            scanCheque(it1)
                        }
                    }
                }

                it.close()
            }

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalysis
                )

                preview.setSurfaceProvider(binding.fragmentCapturingViewFinder.surfaceProvider)
            } catch (exc: Exception) {
                // Handle errors
                Log.d(TAG, "startCamera: "+exc.message)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }


    private fun scanCheque(imageProxy: Bitmap) {
        if (_binding == null) return
        val image = InputImage.fromBitmap(imageProxy, 0)
        detector?.process(image)?.addOnSuccessListener { list ->
            Log.d(TAG, "scanCheque: " + list)

                try {
                    if (list.isNotEmpty()) {
                        list.get(0).boundingBox?.let {
                            allowCapture = true
                            val detectedRect = it
                            binding.imageView.background = null

                            binding.imageView.setImageResource(R.drawable.cheque_scan_green)
                            val resources: Resources = resources
                            val marginDp = 20.0f
                            val marginPx = resources.displayMetrics.density * marginDp

                            val adjustedLeft = detectedRect.left.coerceAtLeast(marginPx.toInt()) // Ensure at least margin space from left edge
                            val adjustedTop = detectedRect.top.coerceAtLeast(marginPx.toInt()) // Ensure at least margin space from top edge
                            val adjustedRight = detectedRect.right.coerceAtMost(imageProxy.width - marginPx.toInt() - 1) // Stay within right edge (minus margin and 1 to avoid exceeding bounds)
                            val adjustedBottom = detectedRect.bottom.coerceAtMost(imageProxy.height - marginPx.toInt() - 1) // Stay within bottom edge (minus margin and 1 to avoid exceeding bounds)

                            val adjustedRect = Rect(adjustedLeft, adjustedTop, adjustedRight, adjustedBottom)

                            if (adjustedRect.width() > 0 && adjustedRect.height() > 0) {
                                val croppedBitmap = Bitmap.createBitmap(imageProxy, adjustedRect.left, adjustedRect.top, adjustedRect.width(), adjustedRect.height())
                            sharedViewModel.capturedBitmap = croppedBitmap
                            } else {
                                sharedViewModel.capturedBitmap = imageProxy
                            }
                        }
                    } else {
                        allowCapture = false
                        sharedViewModel.capturedBitmap = null
                        binding.imageView.setImageResource(R.drawable.cheque_scan_white)
                    }
                }catch (e:Exception) {

                }
        }?.addOnFailureListener {
            binding.rectBox?.removeFrame()
            Log.d(TAG, "scanCheque: " + it.message)
        }
    }


    private fun updateOverlay(detectedObjects: DetectedObjects) {
        if (detectedObjects.objects.isEmpty()) {
            binding.objectOverLay?.set(emptyList())
            return
        }

        binding.objectOverLay?.setSize(detectedObjects.imageWidth, detectedObjects.imageHeight)

        val list = mutableListOf<BoxData>()

        for (obj in detectedObjects.objects) {
            val box = obj.boundingBox
            val label = obj.labels.joinToString { label ->
                val confidence: Int = label.confidence.times(100).toInt()
                "${label.text} $confidence%"
            }
            val text = if (label.isNotEmpty()) label else "unknown"
            list.add(BoxData(text, box))
        }

        binding.objectOverLay?.set(list)
    }

    private fun scanQRCode(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        scanner?.process(image)
            ?.addOnSuccessListener { barcodes ->
                try {
                    if (barcodes.isNotEmpty()) {
                        if (sharedViewModel.scannedQRResult == null) {
                            binding.qrIndicator?.background = null
                            binding.qrIndicator?.clearAnimation()
                            //binding.imageView.setImageResource(R.drawable.cheque_scan_green)
                            Toast.makeText(requireContext(),"Qr code scanned", Toast.LENGTH_SHORT).show()
                            sharedViewModel.scannedQRResult = barcodes[0].rawValue
                            binding.qrIndicator?.hide()
                        }
                    }

                } catch (e:Exception) {

                }
            }?.addOnFailureListener() {

            }
    }


    private fun scanAndNavigate(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        scanner?.process(image)?.addOnSuccessListener { barcodes ->
                try {
//                    if (sharedViewModel.scannedQRResult == null) {
//                        Toast.makeText(requireContext(),"Qr code scanned", Toast.LENGTH_SHORT).show()
//                    }
                    Log.d(TAG, "scanQRCode: "+ barcodes[0].rawValue)
                    sharedViewModel.scannedQRResult = barcodes[0].rawValue
                    navigate()
                } catch (e:Exception) {

                }
            }
        lifecycleScope.launch(Dispatchers.Main) {
            delay(200)
            if (sharedViewModel.scannedQRResult == null) {
                Toast.makeText(requireContext(), "Please Recapture And focus", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun captureViewFinder() {
        if (sharedViewModel.scannedQRResult != null) {
            captureAndNavigateIfScanned()
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                val originBitmap = getViewFinderImage()
                sharedViewModel.capturedBitmap =  originBitmap
                scanImage(originBitmap)
            }
        }


    }

    private fun scanImage(originalBitmap: Bitmap?) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (originalBitmap != null) {
                val targetWidth: Int = originalBitmap.height * 16 / 9
                val targetHeight: Int = originalBitmap.height
                val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)
                scanAndNavigate(resizedBitmap)
            }
        }
    }

    suspend fun getViewFinderImage() = withContext(Dispatchers.Main) {
        binding.fragmentCapturingViewFinder.bitmap
    }

    fun navigate() {
        if (findNavController().currentDestination?.id == com.xynotech.converso.ai.R.id.capturingFragment) {
                findNavController().navigate(com.xynotech.converso.ai.R.id.action_capturingFragment_to_processFragment)
        }
    }

    fun captureAndNavigateIfScanned() {
        viewLifecycleOwner.lifecycleScope.launch {
           // sharedViewModel.capturedBitmap = getViewFinderImage()
        }
        navigate()
    }


    private fun Context.hasCameraPermission():Boolean {
        return  (ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }


    private val multiPermissionCallback =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { map ->
            map.entries.forEach()
            { entry ->
                when (entry.key) {
                    Manifest.permission.CAMERA -> if (entry.value) {
                        startCamera()
                    } else {
                        permissionDenied("App Needs Permission to capture image")
                    }
                }
            }
        }

    private fun permissionDenied(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply()
            {
                data = Uri.fromParts("package", "com.xynotech.converso.ai", null)
            })
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val TAG = "CapturingFragment"
    }
}