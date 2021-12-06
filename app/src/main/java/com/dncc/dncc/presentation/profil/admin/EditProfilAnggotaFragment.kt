package com.dncc.dncc.presentation.profil.admin

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
import com.dncc.dncc.databinding.FragmentEditProfilAnggotaBinding

class EditProfilAnggotaFragment : Fragment() {
    private var _binding: FragmentEditProfilAnggotaBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfilAnggotaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateUI()
    }

    private fun initiateUI() {
        initateToolbar()
        dropDownRole()
        dropDownDivisi()
        binding.btnSimpan.setOnClickListener {
            alertDialog()
        }
    }

    private fun alertDialog() {
        val dialogBuilder = AlertDialog.Builder(activity)
        with(dialogBuilder) {
            setTitle("Peringatan")
            setMessage("Yakin ingin mengubah profil anggota ini?")
            setPositiveButton("Iya") { _, _ ->
                Toast.makeText(activity, "Profil anggota telah berhasil diubah", Toast.LENGTH_LONG)
                    .show()
            }
            setNegativeButton("Tidak") { _, _ ->

            }
            show()
        }
    }

    private fun dropDownRole() {
        val roles = resources.getStringArray(R.array.role)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.list_item_dropdown, roles)
        binding.dropdownRoles.setAdapter(arrayAdapter)
    }

    private fun dropDownDivisi() {
        val divisi = resources.getStringArray(R.array.divisi)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.list_item_dropdown, divisi)
        binding.dropdownDivisi.setAdapter(arrayAdapter)
    }

    private fun initateToolbar() {
        val title = "Edit Profil Anggota"
        binding.actionBar.actionBarTitle.text = title
        binding.actionBar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


}