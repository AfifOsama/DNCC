package com.dncc.dncc.presentation.pertemuan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dncc.dncc.databinding.FragmentDetailPertemuanBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailPertemuanFragment : Fragment() {
    private var _binding: FragmentDetailPertemuanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPertemuanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initateUi()
    }

    private fun initateUi() {
        initateToolbar()
        binding.run {
            refresh.run {
                setOnRefreshListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        Log.i("DetailPertemuanFragment", "refresh: ")
//                        viewModel.getUser(userId)
                        delay(2000)
                        isRefreshing = false
                    }
                }
            }
        }
    }

    private fun initateToolbar() {
        val title = "Detail Pertemuan"
        binding.actionBar.actionBarTitle.text = title
        binding.actionBar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}