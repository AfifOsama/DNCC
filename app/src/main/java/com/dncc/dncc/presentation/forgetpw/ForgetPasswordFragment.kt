package com.dncc.dncc.presentation.forgetpw

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dncc.dncc.databinding.FragmentForgetPasswordBinding

class ForgetPasswordFragment : Fragment() {
    private var _binding: FragmentForgetPasswordBinding?=null
    private val binding get()=_binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

}