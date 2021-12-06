package com.dncc.dncc.presentation.home.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dncc.dncc.R
import com.dncc.dncc.databinding.FragmentHomeAdminBinding

class HomeAdminFragment : Fragment() {
    private var _binding: FragmentHomeAdminBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initateUI()
    }

    private fun initateUI() {
        binding.cardMenu1.setOnClickListener {
            findNavController().navigate(R.id.action_homeAdminFragment_to_listAnggotaFragment)
        }
        binding.cardMenu2.setOnClickListener {
            findNavController().navigate(R.id.action_homeAdminFragment_to_tambahPelatihanFragment)
        }
        binding.headerHome.setOnClickListener {
            findNavController().navigate(R.id.action_homeAdminFragment_to_profilFragment)
        }
    }

}