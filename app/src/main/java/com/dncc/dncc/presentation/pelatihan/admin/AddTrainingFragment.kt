package com.dncc.dncc.presentation.pelatihan.admin

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dncc.dncc.R
import com.dncc.dncc.common.Resource
import com.dncc.dncc.databinding.FragmentCreatePelatihanBinding
import com.dncc.dncc.domain.entity.training.TrainingEntity
import com.dncc.dncc.presentation.pelatihan.TrainingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTrainingFragment : Fragment() {

    private var _binding: FragmentCreatePelatihanBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TrainingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePelatihanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initiateUI()
        initiateObserver()
    }

    private fun initiateUI() {
        binding.run {
            toolbar.actionBarTitle.text = getString(R.string.tambah_pelatihan)
            toolbar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            
            btnBatal.setOnClickListener {
                findNavController().popBackStack()
            }

            btnTambah.setOnClickListener {
                val training = dropdownNamaPelatihan.text.toString()
                val desc = edtDeskripsiPelatihan.text.toString()
                val mentor = edtNamaMentor.text.toString()
                val wa = edtLinkWag.text.toString()
                val participantMax = edtParticipantMax.text.toString().toInt()
                val day = edtDay.text.toString()
                val time = edtClock.text.toString()

                if (validation(training, desc, mentor, wa, participantMax, day, time)) {
                    alertDialog()
                }
            }

            val divisi = resources.getStringArray(R.array.add_training)
            binding.dropdownNamaPelatihan.setAdapter(ArrayAdapter(requireContext(), R.layout.list_item_dropdown, divisi))
        }
    }


    private fun initiateObserver() {
        viewModel.addTrainingResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                    renderToast("maaf harap coba lagi")
                }
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    if (it.data == true) {
                        renderToast("Berhasil menambahkan pelatihan")
                        findNavController().popBackStack()
                    } else {
                        renderToast("Gagal menambahkan pelatihan")
                    }
                }
            }
        })
    }

    private fun alertDialog() {
        val dialogBuilder = AlertDialog.Builder(activity)
        with(dialogBuilder) {
            setTitle("Peringatan")
            setMessage("Yakin ingin menambah pelatihan ini?")
            setPositiveButton("Iya") { _, _ ->
                addTraining()
            }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun addTraining() {
        binding.run {
            val training = dropdownNamaPelatihan.text.toString()
            val desc = edtDeskripsiPelatihan.text.toString()
            val mentor = edtNamaMentor.text.toString()
            val wa = edtLinkWag.text.toString()
            val participantMax = edtParticipantMax.text.toString().toInt()
            val day = edtDay.text.toString()
            val time = edtClock.text.toString()

            viewModel.addTraining(TrainingEntity(
                linkWa = wa,
                mentor = mentor,
                schedule = "$day, $time",
                desc = desc,
                trainingName = training,
                participantMax = participantMax,
            ))
        }
    }

    private fun validation(
        training: String,
        desc: String,
        mentor: String,
        wa: String,
        participantMax: Int,
        day: String,
        time: String,
    ): Boolean = when {
        TextUtils.isEmpty(training) -> {
            binding.dropdownNamaPelatihan.error = "harap lengkapi divisi dahulu ya!"
            renderToast("harap lengkapi divisi dahulu ya!")
            false
        }
        TextUtils.isEmpty(desc) -> {
            binding.edtDeskripsiPelatihan.error = "harap isi deskripsi pelatihan"
            renderToast("harap isi deskripsi pelatihan")
            false
        }
        TextUtils.isEmpty(mentor) -> {
            binding.edtNamaMentor.error = "harap isi nama mentor"
            renderToast("harap isi nama mentor")
            false
        }
        TextUtils.isEmpty(wa) -> {
            binding.edtLinkWag.error = "harap isi link WhatsApp"
            renderToast("harap isi link WhatsApp")
            false
        }
        TextUtils.isEmpty(participantMax.toString()) -> {
            binding.edtParticipantMax.error = "harap isi maksimal pendaftar"
            renderToast("harap isi maksimal pendaftar")
            false
        }
        TextUtils.isEmpty(day) -> {
            binding.edtDay.error = "harap isi hari pelatihan"
            renderToast("harap hari pelatihan")
            false
        }
        TextUtils.isEmpty(time) -> {
            binding.edtClock.error = "harap isi jam pelatihan"
            renderToast("harap jam pelatihan")
            false
        }
        else -> true
    }

    private fun renderToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}