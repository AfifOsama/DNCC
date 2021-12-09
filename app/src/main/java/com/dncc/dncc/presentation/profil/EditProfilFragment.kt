package com.dncc.dncc.presentation.profil

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dncc.dncc.databinding.FragmentEditProfilBinding

class EditProfilFragment : Fragment() {
    private var _binding: FragmentEditProfilBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateUI()
    }

    private fun initiateUI() {
        initiateToolbar()
        binding.btnSimpan.setOnClickListener { alertDialog() }
    }

    private fun alertDialog() {
        val dialogBuilder = AlertDialog.Builder(activity)
        with(dialogBuilder) {
            setTitle("Peringatan")
            setMessage("Yakin ingin mengubah profil Anda?")
            setPositiveButton("Iya") { _, _ ->
                Toast.makeText(activity, "Profil Anda telah berhasil diubah", Toast.LENGTH_LONG)
                    .show()
            }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun initiateToolbar() {
        val title = "Edit Profil"
        binding.actionBar.actionBarTitle.text = title
        binding.actionBar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}