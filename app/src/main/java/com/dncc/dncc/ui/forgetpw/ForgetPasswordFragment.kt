package com.dncc.dncc.ui.forgetpw

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dncc.dncc.R
import com.dncc.dncc.databinding.FragmentForgetPasswordBinding
import com.dncc.dncc.databinding.FragmentLoginBinding

class ForgetPasswordFragment : Fragment() {
    private var _binding: FragmentForgetPasswordBinding?=null
    private val binding get()=_binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}