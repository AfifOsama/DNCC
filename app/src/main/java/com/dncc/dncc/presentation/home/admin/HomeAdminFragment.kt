package com.dncc.dncc.presentation.home.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.dncc.dncc.R
import com.dncc.dncc.common.Resource
import com.dncc.dncc.databinding.FragmentHomeAdminBinding
import com.dncc.dncc.domain.entity.user.UserEntity
import com.dncc.dncc.presentation.home.user.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeAdminFragment : Fragment() {

    private var _binding: FragmentHomeAdminBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private val userId by lazy { auth.currentUser?.uid ?: "" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        viewModel.getUser(userId)

        initiateUI()
        initiateObserver()
    }

    private fun initiateUI() {
        binding.run {
            cardMenu1.setOnClickListener {
                findNavController().navigate(R.id.action_homeAdminFragment_to_listAnggotaFragment)
            }
            cardMenu2.setOnClickListener {
                findNavController().navigate(R.id.action_homeAdminFragment_to_tambahPelatihanFragment)
            }
            cardMenu3.setOnClickListener {
                findNavController().navigate(R.id.action_homeAdminFragment_to_listPelatihanFragment)
            }
            headerHome.setOnClickListener {
                findNavController().navigate(HomeAdminFragmentDirections.actionHomeAdminFragmentToProfilFragment(userId))
            }
            refresh.run {
                setOnRefreshListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        Log.i("HomeAdminFragment", "refresh: ")
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
                    setUserView(it.data ?: UserEntity())
                }
            }
        })
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
            tvRole.text = userEntity.role
        }
    }

    private fun renderToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}