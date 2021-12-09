package com.dncc.dncc.presentation.home.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dncc.dncc.R
import com.dncc.dncc.databinding.FragmentHomeAdminBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeAdminFragment : Fragment() {
    private var _binding: FragmentHomeAdminBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateUI()
    }

    private fun initiateUI() {
        binding.run {
            cardMenu1.setOnClickListener {
                findNavController().navigate(R.id.action_homeAdminFragment_to_listAnggotaFragment)
            }
            cardMenu2.setOnClickListener {
                findNavController().navigate(R.id.action_homeAdminFragment_to_tambahPelatihanFragment)
            }
            cardMenu3.setOnClickListener {
                findNavController().navigate(R.id.action_homeAdminFragment_to_listPelatihanFragment)
            }
            headerHome.setOnClickListener {
                findNavController().navigate(R.id.action_homeAdminFragment_to_profilFragment)
            }
            refresh.run {
                setOnRefreshListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        Log.i("HomeAdminFragment", "refresh: ")
//                        viewModel.getUser(userId)
                        delay(2000)
                        isRefreshing = false
                    }
                }
            }
        }

    }

}