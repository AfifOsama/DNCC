package com.dncc.dncc.presentation.profil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dncc.dncc.databinding.FragmentEditProfilBinding

class EditProfilFragment : Fragment() {
    private var _binding:FragmentEditProfilBinding?=null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentEditProfilBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateUI()
    }

    private fun initiateUI() {
        initiateToolbar()
    }

    private fun initiateToolbar() {
        val title="Edit Profil"
        binding.actionBar.actionBarTitle.text=title
        binding.actionBar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}