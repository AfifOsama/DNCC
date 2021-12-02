package com.dncc.dncc.presentation.pelatihan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dncc.dncc.R
import com.dncc.dncc.databinding.FragmentDetailPelatihanBinding

class DetailPelatihanFragment : Fragment() {
    private var _binding:FragmentDetailPelatihanBinding?=null
    private val binding get()= _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentDetailPelatihanBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateUI()
    }

    private fun initiateUI() {
        binding.anggota.setOnClickListener {
            if (binding.rvCardAnggota.visibility==View.GONE){
                binding.rvCardAnggota.visibility=View.VISIBLE
            }else{
                binding.rvCardAnggota.visibility=View.GONE
            }
        }
        binding.actionBar.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_detailPelatihanFragment_to_homeFragment)
        }
    }

}