package com.dncc.dncc.presentation.home.mentor

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
import com.bumptech.glide.Glide
import com.dncc.dncc.R
import com.dncc.dncc.common.Resource
import com.dncc.dncc.common.UserRoleEnum
import com.dncc.dncc.databinding.FragmentHomeMentorBinding
import com.dncc.dncc.domain.entity.training.TrainingEntity
import com.dncc.dncc.domain.entity.user.UserEntity
import com.dncc.dncc.presentation.pelatihan.DetailPelatihanFragmentDirections
import com.dncc.dncc.presentation.pelatihan.TrainingViewModel
import com.dncc.dncc.presentation.pelatihan.adapter.MeetsAdapter
import com.dncc.dncc.presentation.pelatihan.adapter.UsersAdapter
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeMentorFragment : Fragment() {

    private var _binding: FragmentHomeMentorBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<HomeMentorFragmentArgs>()

    private val viewModel: TrainingViewModel by viewModels()

    private var userId = ""
    private var trainingId = ""
    private var linkWAG = ""
    private var trainingEntity = TrainingEntity()

    private val meetsAdapter by lazy {
        MeetsAdapter(
            onClick = {
                findNavController().navigate(
                    HomeMentorFragmentDirections.actionHomeMentorFragmentToDetailPertemuanFragment(
                        it.meetId, trainingId, UserRoleEnum.MENTOR.role
                    )
                )
            }
        )
    }

    private val usersAdapater by lazy {
        UsersAdapter(
            onClick = {
                findNavController().navigate(
                    HomeMentorFragmentDirections.actionHomeMentorFragmentToProfilFragment(
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
        _binding = FragmentHomeMentorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = args.userEntity.userId
        trainingId = args.userEntity.trainingId

        viewModel.getUser(userId)
        viewModel.getTraining(trainingId)
        viewModel.getMeets(trainingId)
        viewModel.getTrainingParticipants(trainingId)

        initiateUI()
        initiateObserver()
    }

    private fun initiateUI() {
        binding.run {
            refresh.run {
                setOnRefreshListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        Log.i("HomeMentorFragment", "refresh: ")
                        viewModel.getUser(userId)
                        viewModel.getTraining(trainingId)
                        viewModel.getMeets(trainingId)
                        viewModel.getTrainingParticipants(trainingId)
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

            profileSection.setOnClickListener {
                findNavController().navigate(
                    HomeMentorFragmentDirections.actionHomeMentorFragmentToProfilFragment(
                        userId
                    )
                )
            }

            btnUbah.setOnClickListener {
                findNavController().navigate(HomeMentorFragmentDirections.actionHomeMentorFragmentToEditPelatihanFragment(trainingEntity))
            }

            showUserList()
            showMeetList()
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
                    renderToast("Maaf gagal memuat user")
                }
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    setUserContent(it.data ?: UserEntity())
                }
            }
        })

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
                    binding.btnUbah.isClickable = true
                    trainingEntity = it.data ?: TrainingEntity()
                    setTrainingContent(trainingEntity)
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
                    binding.tvNumberAnggota.text = it.data?.size.toString()
                }
            }
        })
    }

    private fun setUserContent(userEntity: UserEntity) {
        binding.run {
            val imagePath = FirebaseStorage.getInstance().reference.child("images").child(userId)
            progressImg.visibility = View.VISIBLE
            imagePath.downloadUrl.addOnSuccessListener {
                progressImg.visibility = View.GONE
                Glide.with(requireContext())
                    .load(it)
                    .error(R.drawable.logodncc)
                    .into(imgUser)
            }.addOnFailureListener {
                it.message?.let { error -> Log.i("HomeFragment", "error image $error") }
            }

            tvUser.text = userEntity.fullName
            tvNim.text = userEntity.nim
            tvRole.text = userEntity.role

            profileSection.isClickable = true
        }
    }

    private fun setTrainingContent(trainingEntity: TrainingEntity) {
        binding.run {
            tvPelatihanDescription.text = trainingEntity.desc
            linkWAG = trainingEntity.linkWa
            tvNamaMentor.text = trainingEntity.mentor
            tvWaktu.text = trainingEntity.schedule
            tvNumberAnggota.text = trainingEntity.participantNow.toString()
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
            Log.e("HomeMentorFragment", "intentApp: ${e.message}")
            renderToast("Link WAG belum dibuat atau salah")
        }
    }

    private fun renderToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}