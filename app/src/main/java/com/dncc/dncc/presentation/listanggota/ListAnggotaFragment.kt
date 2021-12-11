package com.dncc.dncc.presentation.listanggota

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dncc.dncc.R
import com.dncc.dncc.common.Resource
import com.dncc.dncc.databinding.FragmentListAnggotaBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListAnggotaFragment : Fragment() {

    private var _binding: FragmentListAnggotaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListUserViewModel by viewModels()
    private val adapter by lazy {
        UserListAdapter(
            onClick = {
                renderToast("on click ${it.fullName}")
            },
            onEditClick = {
                renderToast("on edit click ${it.fullName}")
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListAnggotaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initiateUI()
        initiateObserver()
    }

    private fun initiateUI() {
        binding.run {
            actionBar.actionBarTitle.text = getString(R.string.daftar_anggota_dncc)
            actionBar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            refresh.run {
                setOnRefreshListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        Log.i("ListAnggotaFragment", "refresh: ")
                        viewModel.getUsers()
                        delay(2000)
                        isRefreshing = false
                    }
                }
            }

            showUserList()
        }
    }

    private fun initiateObserver() {
        viewModel.getUsersResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                    renderToast(it.message ?: "maaf harap coba lagi")
                }
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    adapter.setList(it.data ?: mutableListOf())
                }
            }
        })
    }

    private fun showUserList() {
        binding.run {
            rvCardAdminAnggota.setHasFixedSize(true)
            rvCardAdminAnggota.layoutManager = LinearLayoutManager(requireContext())
            rvCardAdminAnggota.adapter = adapter
        }
    }

    private fun renderToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}