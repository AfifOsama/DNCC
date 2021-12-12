package com.dncc.dncc.presentation.pelatihan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dncc.dncc.databinding.FragmentDetailPelatihanBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailPelatihanFragment : Fragment() {
    private var _binding: FragmentDetailPelatihanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPelatihanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateUI()
    }

    private fun initiateUI() {
        initiateToolbar()
        binding.run {
            anggota.setOnClickListener {
                if (binding.rvCardAnggota.visibility == View.GONE) {
                    binding.rvCardAnggota.visibility = View.VISIBLE
                } else {
                    binding.rvCardAnggota.visibility = View.GONE
                }
            }
            refresh.run {
                setOnRefreshListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        Log.i("DetailPelatihanFragment", "refresh: ")
//                        viewModel.getUser(userId)
                        delay(2000)
                        isRefreshing = false
                    }
                }
            }
        }


    }

    private fun initiateToolbar() {
        val title = "Detail Pelatihan Divisi"
        binding.toolbar.actionBarTitle.text = title
        binding.toolbar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}