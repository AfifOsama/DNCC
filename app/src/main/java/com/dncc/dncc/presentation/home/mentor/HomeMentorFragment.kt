package com.dncc.dncc.presentation.home.mentor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dncc.dncc.R
import com.dncc.dncc.databinding.FragmentEditPelatihanBinding
import com.dncc.dncc.databinding.FragmentHomeMentorBinding

class HomeMentorFragment : Fragment() {
    private var _binding: FragmentHomeMentorBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeMentorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}