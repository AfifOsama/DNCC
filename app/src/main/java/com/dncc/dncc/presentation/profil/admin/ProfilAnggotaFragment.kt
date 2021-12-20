package com.dncc.dncc.presentation.profil.admin

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
import coil.load
import com.dncc.dncc.R
import com.dncc.dncc.common.Resource
import com.dncc.dncc.databinding.FragmentProfilAnggotaBinding
import com.dncc.dncc.domain.entity.user.UserEntity
import com.dncc.dncc.presentation.profil.ProfilFragmentArgs
import com.dncc.dncc.presentation.profil.ProfileViewModel
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfilAnggotaFragment : Fragment() {

    private var _binding: FragmentProfilAnggotaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private val args: ProfilFragmentArgs by navArgs()

    private var userEntity: UserEntity = UserEntity()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilAnggotaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initiateUI()
        initiateObserver()

        val userId = args.userId ?: ""
        viewModel.getUser(userId)
    }

    private fun initiateUI() {
        binding.run {
            btnUbah.setOnClickListener {
                findNavController().navigate(
                    ProfilAnggotaFragmentDirections.actionProfilAnggotaFragmentToEditProfilAnggotaFragment(
                        userEntity
                    )
                )
            }

            toolbar.actionBarTitle.text = getString(R.string.profil_anggota)
            toolbar.btnBack.setOnClickListener {
                findNavController().popBackStack()
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
                    setUserContent(it.data ?: UserEntity())
                    userEntity = it.data ?: UserEntity()
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
                it.message?.let { error -> Log.i("ProfilAnggotaFragment", "error image $error") }
            }

            edtEmail.setText(userEntity.email)
            edtNama.setText(userEntity.fullName)
            edtProdi.setText(userEntity.major)
            edtNim.setText(userEntity.nim)
            edtNoHp.setText(userEntity.noHp)
            edtDivisi.setText(userEntity.training)
            edtRole.setText(userEntity.role)
        }
    }

    private fun renderToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}