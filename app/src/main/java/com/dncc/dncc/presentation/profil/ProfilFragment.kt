package com.dncc.dncc.presentation.profil

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.dncc.dncc.R
import com.dncc.dncc.common.Resource
import com.dncc.dncc.databinding.FragmentProfilBinding
import com.dncc.dncc.domain.entity.user.UserEntity
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfilFragment : Fragment() {
    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private val args: ProfilFragmentArgs by navArgs()

    private var userEntity: UserEntity = UserEntity()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = args.userId ?: ""
        viewModel.getUser(userId)

        initiateObserver()

        val title = "Profil Anda"
        binding.actionBar.actionBarTitle.text = title

        binding.actionBar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnLogout.setOnClickListener {
            logoutWithAlertDialog()
        }

        binding.btnUbah.setOnClickListener {
            findNavController().navigate(
                ProfilFragmentDirections.actionProfilFragmentToEditProfilFragment(
                    userEntity
                )
            )
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
                    setUserContent(it.data ?: UserEntity())
                    userEntity = it.data ?: UserEntity()
                }
            }
        })

        viewModel.editUserResponse.observe(viewLifecycleOwner, {
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
                }
            }
        })
    }

    private fun setUserContent(userEntity: UserEntity) {
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
        }
    }

    private fun renderToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun logoutWithAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(activity)
        with(dialogBuilder) {
            setTitle("Peringatan")
            setMessage("Yakin ingin logout?")
            setPositiveButton("Iya") { _, _ ->
                viewModel.logout()
                findNavController().navigate(ProfilFragmentDirections.actionProfilFragmentToLoginFragment())
            }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

}