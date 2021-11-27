package com.dncc.dncc.ui.login

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dncc.dncc.R
import com.dncc.dncc.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var _binding:FragmentLoginBinding?=null
    private val binding get()=_binding!!
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

    }

    private val sColor=ForegroundColorSpan(Color.rgb(9,121,189))
    private fun textSpanDaftar() {
        val spannable=SpannableString("Belum punya akun? daftar di sini")
        val clickableSpan=object :ClickableSpan(){
            override fun onClick(p0: View) {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color=Color.rgb(9,121,189)
                ds.isUnderlineText=false
            }
        }
        spannable.setSpan(
            sColor,
            24, 32,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        spannable.setSpan(
            clickableSpan,
            24, 32,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        binding.tvDaftar.text=spannable
        binding.tvDaftar.movementMethod=LinkMovementMethod.getInstance()

    }

    private fun textSpanLupaSandi() {
        val spannableSandi=SpannableString("lupa kata sandi? tekan di sini")
        val clickableSpanSandi=object :ClickableSpan(){
            override fun onClick(p0: View) {
                findNavController().navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color=Color.rgb(9,121,189)
                ds.isUnderlineText=false
            }
        }
        spannableSandi.setSpan(
            sColor,23,30,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableSandi.setSpan(
            clickableSpanSandi,23,30,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        binding.tvLupasandi.text=spannableSandi
        binding.tvLupasandi.movementMethod=LinkMovementMethod.getInstance()
    }


}