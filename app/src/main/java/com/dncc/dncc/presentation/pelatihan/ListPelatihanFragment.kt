package com.dncc.dncc.presentation.pelatihan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dncc.dncc.R
import com.dncc.dncc.databinding.FragmentListPelatihanBinding
import com.dncc.dncc.presentation.profil.ProfilFragmentArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListPelatihanFragment : Fragment() {
    private var _binding: FragmentListPelatihanBinding? = null
    private val binding get() = _binding!!

    private val args: ProfilFragmentArgs by navArgs()
    private val viewModel: TrainingViewModel by viewModels()
    private var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListPelatihanBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = args.userId ?: ""
        viewModel.getUser(userId)
//        viewModel.getTraining

        initiateUI()
        initiateObserver()
    }

    private fun initiateUI() {
        binding.run {
            actionBar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            actionBar.actionBarTitle.text = getString(R.string.daftar_pelatihan)
        }
    }

    private fun initiateObserver() {

    }

}