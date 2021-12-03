package com.dncc.dncc.presentation.profil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dncc.dncc.R
import com.dncc.dncc.databinding.FragmentProfilBinding

class ProfilFragment : Fragment() {
    private var _binding:FragmentProfilBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentProfilBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title="Profil Anda"
        binding.actionBar.actionBarTitle.text=title
        binding.actionBar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


}