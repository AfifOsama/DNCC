package com.dncc.dncc.presentation.pertemuan

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dncc.dncc.databinding.FragmentEditPertemuanBinding

class EditPertemuanFragment : Fragment() {
    private var _binding: FragmentEditPertemuanBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPertemuanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateUI()
    }

    private fun initiateUI() {
        initiateToolbar()
        binding.run {
            btnSimpan.setOnClickListener {
                alertDialog()
            }
        }
    }

    private fun alertDialog() {
        val dialogBuilder = AlertDialog.Builder(activity)
        with(dialogBuilder) {
            setTitle("Peringatan")
            setMessage("Yakin ingin mengubah pertemuan ini?")
            setPositiveButton("Iya") { _, _ ->
                Toast.makeText(activity, "Data pertemuan telah berhasil diubah", Toast.LENGTH_LONG)
                    .show()
            }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun initiateToolbar() {
        val title = "Ubah Pertemuan"
        binding.toolbar.actionBarTitle.text = title
        binding.toolbar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}