package com.xynotech.cv.ai.presentation.captureImage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xynotech.converso.ai.databinding.FragmentUploadBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UploadFragment : Fragment() {

    var _binding: FragmentUploadBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUploadBinding.inflate(inflater,container,false)
        return _binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}