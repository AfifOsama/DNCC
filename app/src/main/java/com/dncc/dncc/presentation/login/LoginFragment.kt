package com.dncc.dncc.presentation.login

import android.graphics.Color
import android.os.Bundle
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
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
import com.dncc.dncc.R
import com.dncc.dncc.common.Resource
import com.dncc.dncc.common.UserRoleEnum
import com.dncc.dncc.databinding.FragmentLoginBinding
import com.dncc.dncc.domain.entity.user.UserEntity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private val userId by lazy { auth.currentUser?.uid ?: "" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        textSpanDaftar()
        textSpanLupaSandi()
        initiateObserver()

        binding.btnMasuk.setOnClickListener {
            viewModel.login(binding.edtEmail.text.toString(), binding.edtSandi.text.toString())
        }
    }

    private fun initiateObserver() {
        viewModel.loginResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                    renderToast(it.message ?: "Maaf coba lagi")
                }
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    viewModel.getUser(userId)
                }
            }
        })

        viewModel.loginState.observe(viewLifecycleOwner, {
            if (it) {
                viewModel.getUser(userId)
            }
        })

        viewModel.getUserResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                    renderToast(it.message ?: "Maaf coba lagi")
                }
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    viewModel.saveLoginState(true)
                    val userEntity = it.data ?: UserEntity()
                    val userRole = userEntity.role
                    Log.i("LoginFragment", "initiateObserver: name:${userEntity.fullName} role:$userRole")
                    when (userRole) {
                        UserRoleEnum.VISITOR.role -> findNavController().navigate(
                            LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                        )
                        UserRoleEnum.MEMBER.role -> findNavController().navigate(
                            LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                        )
                        UserRoleEnum.MENTOR.role -> findNavController().navigate(
                            LoginFragmentDirections.actionLoginFragmentToHomeMentorFragment(userEntity)
                        )
                        UserRoleEnum.ADMIN.role -> findNavController().navigate(
                            LoginFragmentDirections.actionLoginFragmentToHomeAdminFragment()
                        )
                    }
                }
            }
        })
    }

    private val sColor = ForegroundColorSpan(Color.rgb(9, 121, 189))
    private fun textSpanDaftar() {
        val text = "Belum punya akun? daftar di sini".toSpannable()
        text[24..32] = object : ClickableSpan() {
            override fun onClick(p0: View) {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.rgb(9, 121, 189)
                ds.isUnderlineText = false
            }
        }.also { sColor }
        binding.tvDaftar.text = text
        binding.tvDaftar.movementMethod = LinkMovementMethod()

    }

    private fun textSpanLupaSandi() {
        val text = "lupa kata sandi? tekan di sini".toSpannable()
        text[23..30] = object : ClickableSpan() {
            override fun onClick(p0: View) {
                findNavController().navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.rgb(9, 121, 189)
                ds.isUnderlineText = false
            }
        }.also { sColor }

        binding.tvLupasandi.text = text
        binding.tvLupasandi.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun renderToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}