package com.dncc.dncc.ui.home.user

import android.content.res.Resources
import android.os.Bundle
import android.text.SpannableString
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dncc.dncc.R
import com.dncc.dncc.databinding.ActionBarBinding
import com.dncc.dncc.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding:FragmentHomeBinding?=null
    private val binding get()=_binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textSpanActionBarRole()

    }

    private val bold=R.style.nunitoBold_12
    private fun textSpanActionBarRole() {
        val role="Mobile"

        val spannable=SpannableString("Divisi $role")
        spannable.setSpan(
            bold,
            7,9,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )

    }

}