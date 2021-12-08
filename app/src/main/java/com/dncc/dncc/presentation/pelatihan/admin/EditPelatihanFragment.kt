package com.dncc.dncc.presentation.pelatihan.admin

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dncc.dncc.R
import com.dncc.dncc.databinding.FragmentEditPelatihanBinding

class EditPelatihanFragment : Fragment() {
    private var _binding: FragmentEditPelatihanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPelatihanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateUI()
    }

    private fun initiateUI() {
        initateToolbar()
        binding.run {
            btnSimpan.setOnClickListener {
                alertDialog()
            }
        }
        scheduleTimeText()
        dropdownDivisi()
    }

    private fun scheduleTimeText() {
        val day = binding.edtDay.text.toString()
        val clock = binding.edtClock.text.toString()
        val time = "$day, $clock"
    }

    private fun dropdownDivisi() {
        val divisi = resources.getStringArray(R.array.divisi)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.list_item_dropdown, divisi)
        binding.dropdownNamaPelatihan.setAdapter(arrayAdapter)
    }

    private fun alertDialog() {
        val dialogBuilder = AlertDialog.Builder(activity)
        with(dialogBuilder) {
            setTitle("Peringatan")
            setMessage("Yakin ingin merubah pelatihan ini?")
            setPositiveButton("Iya") { _, _ ->
                Toast.makeText(activity, "Pelatihan berhasil diubah", Toast.LENGTH_LONG)
                    .show()
            }
            setNegativeButton("Tidak") { _, _ ->

            }
            show()
        }
    }

    private fun initateToolbar() {
        val title = "Tambar Pelatihan"
        binding.actionBar.actionBarTitle.text = title
        binding.actionBar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


}