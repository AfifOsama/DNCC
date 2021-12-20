package com.dncc.dncc.presentation.pelatihan

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.dncc.dncc.R
import com.dncc.dncc.common.Resource
import com.dncc.dncc.common.UserRoleEnum
import com.dncc.dncc.databinding.FragmentListPelatihanBinding
import com.dncc.dncc.domain.entity.user.UserEntity
import com.dncc.dncc.presentation.pelatihan.adapter.TrainingsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListPelatihanFragment : Fragment() {

    private var _binding: FragmentListPelatihanBinding? = null
    private val binding get() = _binding!!

    private val args: ListPelatihanFragmentArgs by navArgs()
    private val viewModel: TrainingViewModel by viewModels()
    private var userId = ""
    private var role = ""

    private val adapter by lazy {
        TrainingsAdapter(
            onClick = {
                findNavController().navigate(
                    ListPelatihanFragmentDirections.actionListPelatihanFragmentToDetailPelatihanFragment(
                        trainingId = it.trainingId,
                        role = role
                    )
                )
            },
            onDelete = {
                with(AlertDialog.Builder(activity)) {
                    setTitle("Peringatan")
                    setMessage("Yakin ingin menghapus pelatihan divisi ini?")
                    setPositiveButton("Iya") { _, _ ->
                        viewModel.deleteTraining(it.trainingId)
                    }
                    setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                    show()
                }
            },
            onRegister = { trainingEntity, userEntity ->
                with(AlertDialog.Builder(activity)) {
                    setTitle("Peringatan")
                    setMessage("Yakin ingin mendaftar pelatihan ${trainingEntity.trainingName}?")
                    setPositiveButton("Iya") { _, _ ->
                        viewModel.registerTraining(trainingEntity.trainingId, userEntity)
                    }
                    setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                    show()
                }
            },
            toast = {
                renderToast(it)
            }
        )
    }

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
        viewModel.getTrainings()

        initiateUI()
        initiateObserver()
    }

    private fun initiateUI() {
        binding.run {
            toolbar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            toolbar.actionBarTitle.text = getString(R.string.daftar_pelatihan)

            binding.run {
                refresh.run {
                    setOnRefreshListener {
                        CoroutineScope(Dispatchers.Main).launch {
                            Log.i("ListPelatihanFragment", "refresh: ")
                            viewModel.getUser(userId)
                            viewModel.getTrainings()
                            delay(2000)
                            isRefreshing = false
                        }
                    }
                }
            }

            showTrainingList()
        }
    }

    private fun initiateObserver() {
        viewModel.getUserResponse.observe(viewLifecycleOwner, {
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
                    adapter.setUser(it.data ?: UserEntity())
                    role = it.data?.role ?: UserRoleEnum.MEMBER.role
                }
            }
        })

        viewModel.getTrainingsResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    adapter.setList(it.data ?: mutableListOf())
                }
            }
        })

        viewModel.deleteTrainingsResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    if (it.data == true) {
                        renderToast("Berhasil menghapus pelatihan")
                    } else {
                        renderToast("Gagal menghapus pelatihan")
                    }
                }
            }
        })

        viewModel.registerTrainingResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    if (it.data == true) {
                        renderToast("Berhasil mendaftar pelatihan")
                    } else {
                        renderToast("Gagal mendaftar pelatihan")
                    }
                }
            }
        })
    }

    private fun showTrainingList() {
        binding.run {
            rvCardDivisi.setHasFixedSize(true)
            rvCardDivisi.layoutManager = LinearLayoutManager(requireContext())
            rvCardDivisi.adapter = adapter
        }
    }

    private fun renderToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}