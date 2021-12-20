package com.dncc.dncc.presentation.home.user

import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dncc.dncc.R
import com.dncc.dncc.common.Resource
import com.dncc.dncc.common.TrainingEnum
import com.dncc.dncc.data.source.local.DataPhotoKegiatan
import com.dncc.dncc.databinding.FragmentHomeBinding
import com.dncc.dncc.domain.entity.user.UserEntity
import com.dncc.dncc.presentation.home.HomeViewModel
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

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private val userId by lazy { auth.currentUser?.uid ?: "" }
    private var userEntity = UserEntity()

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

        viewModel.getUser(userId)

        initiateObserver()
        textSpanTelahhadir()
        imgKegiatan()

        initiateUI()
    }

    private fun initiateUI() {
        binding.run {
            btnPelatihan.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToListPelatihanFragment(
                        userId
                    )
                )
            }
            btnPertemuan.setOnClickListener {
                if (userEntity.trainingId != "") {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailPelatihanFragment(userEntity.trainingId, userEntity.role))
                } else {
                    renderToast("Kamu belum terdaftar di pelatihan")
                }
            }
            headerHome.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToProfilFragment(userId)
                findNavController().navigate(action)
            }
            refresh.run {
                setOnRefreshListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        Log.i("HomeFragment", "refresh: ")
                        viewModel.getUser(userId)
                        delay(2000)
                        isRefreshing = false
                    }
                }
            }
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
                    userEntity = it.data ?: UserEntity()
                    setUserView(userEntity)
                    binding.btnPertemuan.isClickable = true
                }
            }
        })
    }

    private fun setUserView(userEntity: UserEntity) {
        binding.run {
            progressImg.visibility = View.VISIBLE
            val imagePath = FirebaseStorage.getInstance().reference.child("images").child(userId)
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

            val trainingText: Spannable
            if (userEntity.training == TrainingEnum.EMPTY.trainingName) {
                trainingText = "Kamu belum mengikuti pelatihan apapun".toSpannable()
            } else {
                trainingText = "Pelatihan ${userEntity.role}".toSpannable()
                val spanRole = 7 + userEntity.role.length
                trainingText[7..spanRole] = bold
            }

            tvTraining.text = trainingText
        }
    }

    private fun imgKegiatan() {
        binding.run {
            rvImgKegiatan.setHasFixedSize(true)
            list.addAll(listPhotos)
            rvImgKegiatan.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            val photoKegiatanAdapter = PhotoKegiatanAdapter(list)
            rvImgKegiatan.adapter = photoKegiatanAdapter
            photoKegiatanAdapter.setOnItemClickCallback(object :
                PhotoKegiatanAdapter.OnItemClickCallBack {
                override fun onItemClicked(data: DataPhotoKegiatan) {
                    showImgFullscreen(data.photo)
                }
            })
        }

    }

    private fun showImgFullscreen(data: Int) {
        val dialog = Dialog(requireContext())
        with(dialog) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            setContentView(R.layout.preview_img)
            val imgPreview = findViewById<ImageView>(R.id.img_preview)
            imgPreview.setBackgroundResource(data)
            show()
            window?.setLayout(1080, 680)
        }

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