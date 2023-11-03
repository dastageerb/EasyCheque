package com.xynotech.cv.ai.presentation.captureImage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.xynotech.converso.ai.R
import com.xynotech.converso.ai.databinding.FragmentCapturingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class CapturingFragment : Fragment() {

    private var _binding: FragmentCapturingBinding? = null

    private lateinit var gestureDetector: GestureDetector

    private val binding get() = _binding!!

    val sharedViewModel:CaptureSharedViewModel by activityViewModels()

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

        binding.fragmentCaptureTakePictureButton.setOnClickListener {
            captureViewFinder()
        }
    }

    fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder().build()
            imageCapture = ImageCapture.Builder().build()

            imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(
                ContextCompat.getMainExecutor(requireContext()),
                { imageProxy ->
                    // Implement your image analysis logic here

                    sharedViewModel.scanQR(requireActivity(),imageProxy)

                    imageProxy.close()
                })

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

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
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun captureViewFinder() {
        lifecycleScope.launch {
            val bitmap = withContext(Dispatchers.Main) {
                return@withContext binding.fragmentCapturingViewFinder.bitmap
            }
            sharedViewModel.capturedBitmap = bitmap
            findNavController().navigate(R.id.action_capturingFragment_to_cropFragment)
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


//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
//        cameraProviderFuture.addListener({
//            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//            val preview = Preview.Builder()
//                .build()
//                .also {
//                    it.setSurfaceProvider(binding.fragmentCapturingViewFinder.surfaceProvider)
//                }
//            imageCapture = ImageCapture.Builder()
//                .build()
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//            try {
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(this,cameraSelector, preview,imageCapture)
//            } catch(exc: Exception) {
//                Log.e(TAG, "Use case binding failed", exc)
//            }
//
//        }, ContextCompat.getMainExecutor(requireContext()))
//    }


    companion object {
        private const val TAG = "CapturingFragment"
    }
}