package com.dncc.dncc.presentation.profil.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dncc.dncc.R
import com.dncc.dncc.databinding.FragmentProfilAnggotaBinding

class ProfilAnggotaFragment : Fragment() {
    private var _binding: FragmentProfilAnggotaBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilAnggotaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initateUI()
    }

    private fun initateUI() {
        initateToolbar()
        binding.btnUbah.setOnClickListener {
            findNavController().navigate(R.id.action_profilAnggotaFragment_to_editProfilAnggotaFragment)
        }
    }

    private fun initateToolbar() {
        val title = "Profil Anggota"
        binding.actionBar.actionBarTitle.text = title
        binding.actionBar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}