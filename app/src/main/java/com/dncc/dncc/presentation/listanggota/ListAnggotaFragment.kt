package com.dncc.dncc.presentation.listanggota

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dncc.dncc.R
import com.dncc.dncc.common.Resource
import com.dncc.dncc.common.UserRoleEnum
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
                findNavController().navigate(
                    ListAnggotaFragmentDirections.actionListAnggotaFragmentToProfilAnggotaFragment(
                        it.userId
                    )
                )
            },
            onEditClick = {
                findNavController().navigate(
                    ListAnggotaFragmentDirections.actionListAnggotaFragmentToEditProfilAnggotaFragment(
                        it
                    )
                )
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
        initiateObserverMember()
    }

    private fun initiateUI() {
        binding.run {
            toolbar.actionBarTitle.text = getString(R.string.daftar_anggota_dncc)
            toolbar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnFilter.setOnClickListener { view: View ->
                showPopupMenu(view, R.menu.filter_popup_menu)
            }

            refresh.run {
                setOnRefreshListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        Log.i("ListAnggotaFragment", "refresh: ")
                        viewModel.getUsersByMember(UserRoleEnum.MEMBER)
                        delay(2000)
                        isRefreshing = false
                    }
                }
            }

            showUserList()
        }
    }

    private fun showPopupMenu(view: View, filterPopupMenu: Int) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.apply {
            menuInflater.inflate(filterPopupMenu, popupMenu.menu)
            setOnMenuItemClickListener { item: MenuItem? ->
                when (item?.itemId) {
                    R.id.visitor -> {
                        initiateObserverVisitor()
                        renderToast("Menampilkan anggota bedasarkan role visitor")
                    }
                    R.id.member -> {
                        initiateObserverMember()
                        renderToast("Menampilkan anggota bedasarkan role member")
                    }
                    R.id.mentor -> {
                        initiateObserverMentor()
                        renderToast("Menampilkan anggota bedasarkan role mentor")
                    }
                    R.id.admin -> {
                        initiateObserverAdmin()
                        renderToast("Menampilkan anggota bedasarkan role admin")
                    }
                }
                true
            }

            setOnDismissListener {
                it.dismiss()
            }
            show()
        }

    }

    private fun initiateObserverVisitor() {
        viewModel.getUsersVisitorResponse.observe(viewLifecycleOwner, {
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

    private fun initiateObserverMember() {
        viewModel.getUsersMemberResponse.observe(viewLifecycleOwner, {
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

    private fun initiateObserverMentor() {
        viewModel.getUsersMentorResponse.observe(viewLifecycleOwner, {
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

    private fun initiateObserverAdmin() {
        viewModel.getUsersAdminResponse.observe(viewLifecycleOwner, {
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