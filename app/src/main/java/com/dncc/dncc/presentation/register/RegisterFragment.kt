package com.dncc.dncc.presentation.register

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dncc.dncc.common.Resource
import com.dncc.dncc.databinding.FragmentRegisterBinding
import com.dncc.dncc.domain.entity.register.RegisterEntity
import com.dncc.dncc.utils.getRealPathFromURI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var imageReport: Uri? = null
    private lateinit var pathImage: String

    private val viewModel: RegisterViewModel by viewModels()

    private var uploadStatus = false
    private var storeUser = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initiateObserver()
        initiateUI()
    }

    private fun initiateObserver() {
        viewModel.registerResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                    renderToast(it.message ?: "maaf harap coba lagi")
                }
                is Resource.Success -> {
                    uploadImageAndStoreData(it.data ?: "")
                }
            }
        })

        viewModel.uploadImageResponse.observe(viewLifecycleOwner, {
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
                    uploadStatus = true
                    checkAlreadyStored()
                }
            }
        })

        viewModel.registerFirestoreResponse.observe(viewLifecycleOwner, {
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
                    storeUser = true
                    checkAlreadyStored()
                }
            }
        })
    }

    private fun initiateUI() {
        binding.frameLayout.setOnClickListener {
            getPictures()
        }

        binding.btnDaftar.setOnClickListener {
            binding.run {
                val email = edtEmail.text.toString()
                val password = edtSandi.text.toString()
                val fullName = edtNama.text.toString()
                val major = edtProdi.text.toString()
                val nim = edtNim.text.toString()
                val noHp = edtNoHp.text.toString()

                if (validation(imageReport, email, password, fullName, major, nim, noHp)) {
                    viewModel.register(
                        RegisterEntity(
                            pathImage,
                            email,
                            password,
                            fullName,
                            major,
                            nim,
                            noHp
                        )
                    )
                }
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun checkAlreadyStored() {
        if (uploadStatus && storeUser) {
            renderToast("berhasil mendaftarkan akun")
            findNavController().popBackStack()
        }
    }

    private fun uploadImageAndStoreData(userId: String) {
        binding.run {
            val email = edtEmail.text.toString()
            val password = edtSandi.text.toString()
            val fullName = edtNama.text.toString()
            val major = edtProdi.text.toString()
            val nim = edtNim.text.toString()
            val noHp = edtNoHp.text.toString()

            viewModel.uploadImage(pathImage, userId)
            viewModel.registerFirestore(
                RegisterEntity(
                    pathImage,
                    email,
                    password,
                    fullName,
                    major,
                    nim,
                    noHp
                ), userId
            )
        }
    }

    private fun getPictures() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                val permission =
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(requireActivity(), permission, 200)
            } else {
                openGallery()
            }
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent, null)
    }

    //minimum req appcompat:1.3.1
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                imageReport = it?.data?.data
                if (imageReport != null) {
                    binding.imgUser.setImageURI(imageReport)
                    pathImage = it?.data?.data?.getRealPathFromURI(requireContext()) ?: ""
                } else {
                    renderToast("Tidak ada gambar diambil")
                }
            } else {
                renderToast("Terjadi kesalahan mohon pilih gambar lagi")
            }
        }

    private fun validation(
        imageReport: Uri?,
        email: String,
        password: String,
        fullName: String,
        major: String,
        nim: String,
        noHp: String
    ): Boolean = when {
        imageReport == null -> {
            Toast.makeText(context, "Foto tidak boleh kosong", Toast.LENGTH_SHORT).show()
            false
        }
        TextUtils.isEmpty(email) -> {
            binding.edtEmail.error = "harap isi email anda"
            Toast.makeText(context, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            false
        }
        TextUtils.isEmpty(password) -> {
            binding.edtSandi.error = "harap isi password anda"
            Toast.makeText(context, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            false
        }
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
            Toast.makeText(context, "Salah memasukan format email anda", Toast.LENGTH_SHORT)
                .show()
            false
        }
        TextUtils.isEmpty(fullName) -> {
            binding.edtNama.error = "harap isi nama lengkap anda"
            Toast.makeText(context, "harap isi nama lengkap anda", Toast.LENGTH_SHORT).show()
            false
        }
        TextUtils.isEmpty(major) -> {
            binding.edtProdi.error = "harap isi prodi anda"
            Toast.makeText(context, "harap isi prodi anda", Toast.LENGTH_SHORT).show()
            false
        }
        TextUtils.isEmpty(nim) -> {
            binding.edtNim.error = "harap isi nim anda"
            Toast.makeText(context, "harap isi nim anda", Toast.LENGTH_SHORT).show()
            false
        }
        TextUtils.isEmpty(noHp) -> {
            binding.edtNoHp.error = "harap isi no HP anda"
            Toast.makeText(context, "harap isi no HP anda", Toast.LENGTH_SHORT).show()
            false
        }
        else -> true
    }

    private fun renderToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}