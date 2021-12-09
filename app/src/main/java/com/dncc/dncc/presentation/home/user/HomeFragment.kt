package com.dncc.dncc.presentation.home.user

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.dncc.dncc.R
import com.dncc.dncc.common.Resource
import com.dncc.dncc.common.TrainingEnum
import com.dncc.dncc.data.source.local.DataPhotoKegiatan
import com.dncc.dncc.databinding.FragmentHomeBinding
import com.dncc.dncc.domain.entity.user.UserEntity
import com.dncc.dncc.presentation.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val list = ArrayList<DataPhotoKegiatan>()

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private val userId by lazy { auth.currentUser?.uid ?: "" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        initiateObserver()
        initiateUI()
    }

    private fun initiateObserver() {
        userViewModel.getUserResponse.observe(viewLifecycleOwner, {
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
                    setUserView(it.data ?: UserEntity())
                }
            }
        })
    }

    private fun initiateUI() {
        binding.run {
            btnPelatihan.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToListPelatihanFragment())
            }
            btnPertemuan.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_detailPelatihanFragment)
            }
            headerHome.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfilFragment())
            }

            refresh.run {
                setOnRefreshListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        Log.i("HomeFragment", "refresh: ")
                        userViewModel.getUser(userId)
                        delay(2000)
                        isRefreshing = false
                    }
                }
            }

            textSpanTelahhadir()
            imgKegiatan()
        }
    }

    private fun setUserView(userEntity: UserEntity) {
        binding.run {
            val imagePath = FirebaseStorage.getInstance().reference.child("images").child(userId)
            imagePath.downloadUrl.addOnSuccessListener {
                imgUser.load(it.toString()) {
                    placeholder(R.drawable.logodncc)
                    error(R.drawable.logodncc)
                }
            }.addOnFailureListener {
                it.message?.let { error -> Log.i("HomeFragment", "error image $error") }
            }

            tvUser.text = userEntity.fullName
            tvNim.text = userEntity.nim

            val trainingText: Spannable
            if (userEntity.training == TrainingEnum.EMPTY.trainingName) {
                trainingText = "Kamu belum mengikuti pelatihan apapun".toSpannable()
            } else {
                trainingText = "Pelatihan ${userEntity.role}".toSpannable()
                val spanRole = 7 + userEntity.role.length
                trainingText[7..spanRole] = bold
            }

            binding.tvTraining.text = trainingText
        }
    }

    private fun imgKegiatan() {
        binding.rvImgKegiatan.setHasFixedSize(true)
        list.addAll(listPhotos)
        binding.rvImgKegiatan.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvImgKegiatan.adapter = PhotoKegiatanAdapter(list)
    }

    private val bold = StyleSpan(Typeface.BOLD)

    private fun textSpanTelahhadir() {
        val hadirDari = "-"
        val hadirSampai = "10"
        val text = "Kamu telah hadir $hadirDari dari $hadirSampai pertemuan".toSpannable()
        val spanHadirDari = 17 + hadirDari.length

        text[16..spanHadirDari] = bold

        binding.tvTelahHadir.text = text
    }

    private val listPhotos: ArrayList<DataPhotoKegiatan>
        get() {
            val name = resources.getStringArray(R.array.data_name_kegiatan)
            val photo = resources.obtainTypedArray(R.array.data_photo_kegiatan)
            val listPhoto = ArrayList<DataPhotoKegiatan>()
            for (i in name.indices) {
                val photos = DataPhotoKegiatan(photo.getResourceId(i, -1))
                listPhoto.add(photos)
            }
            photo.recycle()
            return listPhoto
        }

    private fun renderToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}