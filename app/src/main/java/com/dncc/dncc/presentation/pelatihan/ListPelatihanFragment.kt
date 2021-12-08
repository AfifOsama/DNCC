package com.dncc.dncc.presentation.pelatihan

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dncc.dncc.databinding.FragmentListPelatihanBinding

class ListPelatihanFragment : Fragment() {
    private var _binding: FragmentListPelatihanBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListPelatihanBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateUI()
    }

    private fun initiateUI() {
        initiateToolbar()
    }

    private fun alertDialog() {
        val dialogBuilder = AlertDialog.Builder(activity)
        with(dialogBuilder) {
            setTitle("Peringatan")
            setMessage("Yakin ingin menghapus pelatihan divisi ini?")
            setPositiveButton("Iya") { _, _ ->
                Toast.makeText(activity, "Pelatihan divisi berhasil dihapus", Toast.LENGTH_LONG)
                    .show()
            }
            setNegativeButton("Tidak") { _, _ ->

            }
            show()
        }
    }

    private fun initiateToolbar() {
        val title = "Daftar Pelatihan"
        binding.actionBar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.actionBar.actionBarTitle.text = title
    }

}