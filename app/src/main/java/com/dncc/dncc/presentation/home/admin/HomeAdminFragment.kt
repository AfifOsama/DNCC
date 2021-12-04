package com.dncc.dncc.presentation.home.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dncc.dncc.R
import com.dncc.dncc.databinding.FragmentHomeAdminBinding

class HomeAdminFragment : Fragment() {
    private var _binding:FragmentHomeAdminBinding?=null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentHomeAdminBinding.inflate(inflater,container,false)
        return binding.root
    }

}