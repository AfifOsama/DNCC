package com.dncc.dncc.presentation.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.dncc.dncc.R
import com.dncc.dncc.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private var _binding:FragmentRegisterBinding?=null
    private val binding get()=_binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateUI()
    }

    private fun initiateUI() {
        binding.btnUploadImg.setOnClickListener{
         }
        binding.btnDaftar.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            Toast.makeText(activity, "Akun Anda berhasil terdaftar, silahkan login", Toast.LENGTH_LONG ).show()
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
}