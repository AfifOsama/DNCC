package com.dncc.dncc.presentation.pertemuan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dncc.dncc.R
import com.dncc.dncc.databinding.FragmentDetailPertemuanBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailPertemuanFragment : Fragment() {

    private var _binding: FragmentDetailPertemuanBinding? = null
    private val binding get() = _binding!!

//    private val viewModel by viewModels<>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPertemuanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateUi()
    }

    private fun initiateUi() {
        initateToolbar()
        binding.run {
            btnUbah.setOnClickListener {
                findNavController().navigate(R.id.action_detailPertemuanFragment_to_editPertemuanFragment)
            }
        }
    }

    private fun initateToolbar() {
        val title = "Detail Pertemuan"
        binding.toolbar.actionBarTitle.text = title
        binding.toolbar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}