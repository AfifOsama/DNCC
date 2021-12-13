package com.dncc.dncc.presentation.profil.admin

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.dncc.dncc.R
import com.dncc.dncc.common.Resource
import com.dncc.dncc.databinding.FragmentEditProfilAnggotaBinding
import com.dncc.dncc.domain.entity.user.EditUserEntity
import com.dncc.dncc.domain.entity.user.UserEntity
import com.dncc.dncc.presentation.profil.EditProfilFragmentArgs
import com.dncc.dncc.presentation.profil.ProfileViewModel
import com.dncc.dncc.utils.getRealPathFromURI
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfilAnggotaFragment : Fragment() {

    private var _binding: FragmentEditProfilAnggotaBinding? = null
    private val binding get() = _binding!!

    private val args: EditProfilFragmentArgs by navArgs()
    private lateinit var userEntity: UserEntity

    private val viewModel: ProfileViewModel by viewModels()

    private var imageReport: Uri? = null
    private var pathImage: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfilAnggotaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userEntity = args.userEntity
        Log.i("EditProfilAFragment", "userEntity $userEntity")

        initiateUI()
        setContent(userEntity)
        initiateObserver()
    }

    private fun initiateUI() {
        binding.run {
            toolbar.actionBarTitle.text = getString(R.string.edit_profil_anggota)
            toolbar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnSimpan.setOnClickListener {
                val fullName = binding.edtNama.text.toString()
                val major = binding.edtProdi.text.toString()
                val nim = binding.edtNim.text.toString()
                val noHp = binding.edtNoHp.text.toString()
                val training = binding.dropdownDivisi.text.toString()
                val role = binding.dropdownRoles.text.toString()
                if (validation(fullName, major, nim, noHp, training, role)) {
                    alertDialog()
                }
            }

            frameLayout.setOnClickListener {
                getPictures()
            }

            val roles = resources.getStringArray(R.array.role)
            binding.dropdownRoles.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.list_item_dropdown,
                    roles
                )
            )

            val divisi = resources.getStringArray(R.array.divisi)
            binding.dropdownDivisi.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.list_item_dropdown,
                    divisi
                )
            )
        }
    }

    private fun initiateObserver() {
        viewModel.editUserResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                    renderToast("maaf harap coba lagi")
                }
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    renderToast("Berhasil mengubah data")
                    findNavController().popBackStack()
                }
            }
        })
    }

    private fun setContent(userEntity: UserEntity) {
        binding.run {
            val imagePath =
                FirebaseStorage.getInstance().reference.child("images").child(userEntity.userId)
            imagePath.downloadUrl.addOnSuccessListener {
                imgUser.load(it.toString()) {
                    placeholder(R.drawable.logodncc)
                    error(R.drawable.logodncc)
                }
            }.addOnFailureListener {
                it.message?.let { error -> Log.i("ProfilFragment", "error image $error") }
            }

            edtEmail.setText(userEntity.email)
            edtNama.setText(userEntity.fullName)
            edtProdi.setText(userEntity.major)
            edtNim.setText(userEntity.nim)
            edtNoHp.setText(userEntity.noHp)
//            dropdownDivisi.setText(userEntity.training)
//            dropdownRoles.setText(userEntity.role)
        }
    }

    private fun alertDialog() {
        val dialogBuilder = AlertDialog.Builder(activity)
        with(dialogBuilder) {
            setTitle("Peringatan")
            setMessage("Yakin ingin mengubah profil anggota ini?")
            setPositiveButton("Iya") { _, _ ->
                confirmEditUser()
            }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun confirmEditUser() {
        binding.run {
            val email = edtEmail.text.toString()
            val fullName = edtNama.text.toString()
            val major = edtProdi.text.toString()
            val nim = edtNim.text.toString()
            val noHp = edtNoHp.text.toString()
            val training = dropdownDivisi.text.toString()
            val role = dropdownRoles.text.toString()

            viewModel.editUser(
                EditUserEntity(
                    userEntity = UserEntity(
                        userId = userEntity.userId,
                        email = email,
                        fullName = fullName,
                        major = major,
                        nim = nim,
                        noHp = noHp,
                        training = training,
                        role = role
                    ),
                    pathImage = pathImage
                )
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
        fullName: String,
        major: String,
        nim: String,
        noHp: String,
        training: String,
        role: String,
    ): Boolean = when {
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
        TextUtils.isEmpty(training) -> {
            binding.dropdownDivisi.error = "harap isi pelatihan"
            Toast.makeText(context, "harap isi pelatihan", Toast.LENGTH_SHORT).show()
            false
        }
        TextUtils.isEmpty(role) -> {
            binding.dropdownRoles.error = "harap isi role"
            Toast.makeText(context, "harap isi role", Toast.LENGTH_SHORT).show()
            false
        }
        else -> true
    }

    private fun renderToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}