package com.dncc.dncc.presentation.pelatihan

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import com.dncc.dncc.databinding.FragmentDetailPelatihanBinding
import com.dncc.dncc.domain.entity.training.TrainingEntity
import com.dncc.dncc.presentation.pelatihan.adapter.MeetsAdapter
import com.dncc.dncc.presentation.pelatihan.adapter.UsersAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailPelatihanFragment : Fragment() {

    private var _binding: FragmentDetailPelatihanBinding? = null
    private val binding get() = _binding!!

    private val args: DetailPelatihanFragmentArgs by navArgs()
    private val viewModel: TrainingViewModel by viewModels()
    private var trainingId = ""
    private var role = ""
    private var linkWAG = ""
    private var trainingEntity = TrainingEntity()

    private val meetsAdapter by lazy {
        MeetsAdapter(
            onClick = {
                findNavController().navigate(DetailPelatihanFragmentDirections.actionDetailPelatihanFragmentToDetailPertemuanFragment())
            }
        )
    }

    private val usersAdapater by lazy {
        UsersAdapter(
            onClick = {
                findNavController().navigate(
                    DetailPelatihanFragmentDirections.actionDetailPelatihanFragmentToProfilFragment(
                        it.userId
                    )
                )
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPelatihanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trainingId = args.trainingId ?: ""
        role = args.role ?: UserRoleEnum.MEMBER.role
        viewModel.getTraining(trainingId)
        viewModel.getMeets(trainingId)
        viewModel.getTrainingParticipants(trainingId)

        initiateUI()
        initiateObserver()
    }

    private fun initiateUI() {
        binding.run {
            binding.toolbar.actionBarTitle.text = getString(R.string.detail_pelatihan_divisi)
            binding.toolbar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            refresh.run {
                setOnRefreshListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        Log.i("DetailPelatihanFragment", "refresh: ")
                        viewModel.getTraining(trainingId)
                        delay(2000)
                        isRefreshing = false
                    }
                }
            }

            anggota.setOnClickListener {
                if (binding.rvCardAnggota.visibility == View.GONE) {
                    binding.rvCardAnggota.visibility = View.VISIBLE
                } else {
                    binding.rvCardAnggota.visibility = View.GONE
                }
            }

            tvLinkWag.setOnClickListener {
                //navigate to WAG
                if (linkWAG != "") {
                    intentApp(linkWAG, "com.whatsapp")
                } else {
                    renderToast("Link WAG belum dibuat")
                }
            }

            btnUbah.setOnClickListener {
                findNavController().navigate(DetailPelatihanFragmentDirections.actionDetailPelatihanFragmentToEditPelatihanFragment(trainingEntity))
            }

            if (role == UserRoleEnum.ADMIN.role) {
                btnUbah.visibility = View.VISIBLE
            }

            showUserList()
            showMeetList()
        }
    }

    private fun initiateObserver() {
        viewModel.getTrainingResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                    renderToast("Maaf gagal menampilkan detail pelatihan")
                }
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    trainingEntity = it.data ?: TrainingEntity()
                    setContent(it.data ?: TrainingEntity())
                }
            }
        })

        viewModel.getMeetsResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                    renderToast("Maaf gagal menampilkan list pertemuan")
                }
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    meetsAdapter.setList(it.data ?: mutableListOf())
                }
            }
        })

        viewModel.getTrainingParticipants.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                    renderToast("Maaf gagal menampilkan list participants")
                }
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    usersAdapater.setList(it.data ?: mutableListOf())
                }
            }
        })
    }

    private fun setContent(trainingEntity: TrainingEntity) {
        binding.run {
            tvNamaMentor.text = trainingEntity.mentor
            tvWaktu.text = trainingEntity.schedule
            tvNumberAnggota.text = trainingEntity.participantNow.toString()
            linkWAG = trainingEntity.linkWa
        }
    }

    private fun showUserList() {
        binding.run {
            rvCardAnggota.setHasFixedSize(true)
            rvCardAnggota.layoutManager = LinearLayoutManager(requireContext())
            rvCardAnggota.adapter = usersAdapater
        }
    }

    private fun showMeetList() {
        binding.run {
            rvCardPertemuan.setHasFixedSize(true)
            rvCardPertemuan.layoutManager = LinearLayoutManager(requireContext())
            rvCardPertemuan.adapter = meetsAdapter
        }
    }

    private fun intentApp(uriP: String, appPackage: String) {
        val uri = Uri.parse(uriP)
        val likeIng = Intent(Intent.ACTION_VIEW, uri)

        likeIng.setPackage(appPackage)

        try {
            startActivity(likeIng)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    private fun renderToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}