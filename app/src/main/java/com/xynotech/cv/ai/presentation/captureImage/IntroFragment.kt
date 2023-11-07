package com.xynotech.cv.ai.presentation.captureImage

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.xynotech.converso.ai.R
import com.xynotech.converso.ai.databinding.FragmentIntroBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroFragment : Fragment() {

    var _binding: FragmentIntroBinding? = null
    val binding get() = _binding!!


    val sharedViewModel:CaptureSharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIntroBinding.inflate(inflater,container,false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding.fragmentIntroButtonCapture.setOnClickListener() {
            sharedViewModel.scannedQRResult = null
            findNavController().navigate(R.id.action_introFragment_to_capturingFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}