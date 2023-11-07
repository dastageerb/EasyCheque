package com.xynotech.cv.ai.presentation.captureImage.capture

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.xynotech.converso.ai.databinding.FragmentCapturingBinding
import com.xynotech.cv.ai.presentation.captureImage.CaptureSharedViewModel
import com.xynotech.cv.ai.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
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

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (requireContext().hasCameraPermission()) {
            startCamera()
        } else {
            multiPermissionCallback.launch(arrayOf(Manifest.permission.CAMERA))
        }

        binding.fragmentCaptureTakePictureButton.setOnClickListener {
//            takePhoto()
            captureViewFinder()
        }




        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.scanState.collect {
                   // val f: Rect = it
               //     Bitmap.createBitmap(bmp, f.left, f.top, f.width(), f.height())

                    Toast.makeText(requireContext(), "Scanned", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


//    @SuppressLint("RestrictedApi")
//    fun startCamera() {
//
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
//
//        cameraProviderFuture.addListener(Runnable {
//            cameraProvider = cameraProviderFuture.get()
//
//            val resolution = Size(1600, 1200)
//            preview = Preview.Builder()
//                .build()
//            imageCapture = ImageCapture.Builder()
//                .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
//                .setMaxResolution(resolution)
//                .build()
//
//
//            imageAnalysis = ImageAnalysis.Builder()
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .build()
//
//
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//
//
//            try {
//                cameraProvider.unbindAll()
//
//                cameraProvider.bindToLifecycle(
//                    viewLifecycleOwner,
//                    cameraSelector,
//                    preview,
//                    imageCapture,
//                    imageAnalysis
//                )
//
//                preview.setSurfaceProvider(binding.fragmentCapturingViewFinder.surfaceProvider)
//            } catch (exc: Exception) {
//                // Handle errors
//            }
//        }, ContextCompat.getMainExecutor(requireContext()))
//    }


    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(requireActivity().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    output.savedUri?.let {

                        val bitmap = convertUriToBitmap(it)

                        if (bitmap != null) {
                            scanQRCode(bitmap)
                        }
                    }
                }
            }
        )
    }


    fun convertUriToBitmap(uri: Uri): Bitmap? {
        val resolver = requireContext().contentResolver
        val inputStream = resolver.openInputStream(uri) ?: return null

        val bitmap = BitmapFactory.decodeStream(inputStream)

        // Compress the bitmap to 50% quality.
        val compressedBitmap = Bitmap.createBitmap(bitmap)
        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, ByteArrayOutputStream())

        inputStream.close()

        return compressedBitmap
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()

            imageCapture = ImageCapture.Builder()
                .build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this,cameraSelector, preview,imageCapture)
                preview.setSurfaceProvider(binding.fragmentCapturingViewFinder.surfaceProvider)
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }


    fun scanQRCode(bitmap: Bitmap) {
        Log.d(TAG, "running: "+bitmap)
        val image = InputImage.fromBitmap(bitmap, 0)
        val scanner = BarcodeScanning.getClient()

        val result = scanner.process(image)
            .addOnSuccessListener { barcodes ->
                try {
                    Toast.makeText(requireContext(),""+ barcodes[0].rawValue, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "scanQRCode: "+ barcodes[0].rawValue)

                } catch (e:Exception) {

                }
            }
            .addOnFailureListener {


                Log.d(TAG, "scanQRCode: "+it.message)

                // Task failed with an exception
                // ...
            }
        Log.d(TAG, "scanQRCode: "+result)
    }

    private fun captureViewFinder() {
        lifecycleScope.launch {
            val originalBitmap = withContext(Dispatchers.Main) {
                return@withContext binding.fragmentCapturingViewFinder.bitmap
            }
            if (originalBitmap != null) {

                val targetWidth: Int = originalBitmap.height * 16 / 9
                val targetHeight: Int = originalBitmap.height
                val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)

                binding.imageView.show()
                binding.imageView.setImageBitmap(resizedBitmap)

                scanQRCode(resizedBitmap)


            }

            sharedViewModel.capturedBitmap = originalBitmap
            //findNavController().navigate(R.id.action_capturingFragment_to_cropFragment)
        }
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
        private const val TAG = "CapturingFragment"
    }
}