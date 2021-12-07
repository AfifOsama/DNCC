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
import com.dncc.dncc.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    renderToast("email atau password salah")
                }
                is Resource.Success -> {
                    Log.i("LoginFragment", "resource success: ${it.data}")
                    binding.progress.visibility = View.GONE
                    if(it.data == true) {
                        viewModel.saveLoginState(true)
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    } else {
                        renderToast(it.message ?: "maaf harap coba lagi")
                    }
                }
            }
        })

        viewModel.loginState.observe(viewLifecycleOwner, {
            if(it) {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
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