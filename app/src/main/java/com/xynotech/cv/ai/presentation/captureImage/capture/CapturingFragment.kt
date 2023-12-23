package com.xynotech.cv.ai.presentation.captureImage.capture

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.xynotech.converso.ai.R
import com.xynotech.converso.ai.databinding.FragmentCapturingBinding
import com.xynotech.cv.ai.presentation.captureImage.CaptureSharedViewModel
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (requireContext().hasCameraPermission()) {
            startCamera()
        } else {
            multiPermissionCallback.launch(arrayOf(Manifest.permission.CAMERA))
        }


        val animation: Animation =
            AlphaAnimation(1f, 0.1f)
        animation.setDuration(150)
        animation.setInterpolator(LinearInterpolator())
        animation.setRepeatCount(Animation.INFINITE)
        animation.setRepeatMode(Animation.REVERSE)
        binding.imageView.startAnimation(animation)

        binding.fragmentCaptureTakePictureButton.setOnClickListener {
            if (sharedViewModel.scannedQRResult == null) {
                captureViewFinder()
            }
            else {
                lifecycleScope.launch {
                    sharedViewModel.capturedBitmap = getViewFinderImage()
                }
                findNavController().navigate(com.xynotech.converso.ai.R.id.action_capturingFragment_to_processFragment)
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.scanState.collect {
                    Toast.makeText(requireContext(), "Scanned", Toast.LENGTH_SHORT).show()
                }
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
                        getViewFinderImage()?.let { it1 -> scanQRCode(it1) }
                    }
                }

            it.close()
            //sharedViewModel.scanWithMlKit(it)
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


//    private fun takePhoto() {
//        val imageCapture = imageCapture ?: return
//        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
//            .format(System.currentTimeMillis())
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
//            }
//        }
//        val outputOptions = ImageCapture.OutputFileOptions
//            .Builder(requireActivity().contentResolver,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                contentValues)
//            .build()
//        imageCapture.takePicture(
//            outputOptions,
//            ContextCompat.getMainExecutor(requireContext()),
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onError(exc: ImageCaptureException) {
//                }
//                override fun onImageSaved(output: ImageCapture.OutputFileResults){
//                    output.savedUri?.let {
//
//                        val bitmap = convertUriToBitmap(it)
//
//                        if (bitmap != null) {
//                          //  scanQRCode(bitmap, n)
//                        }
//                    }
//                }
//            }
//        )
//    }


//    fun convertUriToBitmap(uri: Uri): Bitmap? {
//        val resolver = requireContext().contentResolver
//        val inputStream = resolver.openInputStream(uri) ?: return null
//
//        val bitmap = BitmapFactory.decodeStream(inputStream)
//
//        // Compress the bitmap to 50% quality.
//        val compressedBitmap = Bitmap.createBitmap(bitmap)
//        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 45, ByteArrayOutputStream())
//
//        inputStream.close()
//
//        return compressedBitmap
//    }

//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
//        cameraProviderFuture.addListener({
//            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//            val preview = Preview.Builder()
//                .build()
//
//            imageCapture = ImageCapture.Builder()
//                .build()
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//            try {
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(this,cameraSelector, preview,imageCapture)
//                preview.setSurfaceProvider(binding.fragmentCapturingViewFinder.surfaceProvider)
//            } catch(exc: Exception) {
//                Log.e(TAG, "Use case binding failed", exc)
//            }
//        }, ContextCompat.getMainExecutor(requireContext()))
//    }


    private fun scanQRCode(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val scanner = BarcodeScanning.getClient()
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                try {
                    if (barcodes.isNotEmpty()) {
                        if (sharedViewModel.scannedQRResult == null) {
                            binding.imageView.background = null
                            binding.imageView.clearAnimation()
                            binding.imageView.setImageResource(R.drawable.cheque_scan_green)
                            Toast.makeText(requireContext(),"Qr code scanned", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "scanQRCode: "+ barcodes[0].rawValue)
                            sharedViewModel.scannedQRResult = barcodes[0].rawValue
                        }
                    }
                } catch (e:Exception) {

                }
            }
    }

    private fun scanAndNavigate(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val scanner = BarcodeScanning.getClient()
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
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
            sharedViewModel.capturedBitmap = getViewFinderImage()
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