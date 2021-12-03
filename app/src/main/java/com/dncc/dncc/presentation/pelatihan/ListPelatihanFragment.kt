package com.dncc.dncc.presentation.pelatihan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dncc.dncc.R
import com.dncc.dncc.databinding.FragmentListPelatihanBinding

class ListPelatihanFragment : Fragment() {
    private var _binding:FragmentListPelatihanBinding?=null
    private val binding get()=_binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentListPelatihanBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateUI()
    }

    private fun initiateUI() {
        val title = "Daftar Pelatihan"
        binding.actionBar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.actionBar.actionBarTitle.text=title
    }

}