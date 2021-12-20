package com.dncc.dncc.presentation.pelatihan.admin

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dncc.dncc.R
import com.dncc.dncc.common.Resource
import com.dncc.dncc.databinding.FragmentEditPelatihanBinding
import com.dncc.dncc.domain.entity.training.TrainingEntity
import com.dncc.dncc.presentation.pelatihan.TrainingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPelatihanFragment : Fragment() {

    private var _binding: FragmentEditPelatihanBinding? = null
    private val binding get() = _binding!!

    private val args: EditPelatihanFragmentArgs by navArgs()
    private val viewModel by viewModels<TrainingViewModel>()
    private var trainingEntity = TrainingEntity()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPelatihanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trainingEntity = args.trainingEntity

        initiateUI()
        initiateObserver()
    }

    private fun initiateUI() {
        binding.run {
            toolbar.actionBarTitle.text = getString(R.string.edit_pelatihan)
            toolbar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnSave.setOnClickListener {
                val desc = edtDeskripsiPelatihan.text.toString()
                val mentor = edtNamaMentor.text.toString()
                val linkWa = edtLinkWag.text.toString()
                val max = edtParticipantMax.text.toString()
                val day = edtDay.text.toString()
                val clock = edtClock.text.toString()

                if (validation(desc, mentor, linkWa, max, day, clock)) {
                    alertDialog()
                }
            }

            dropdownNamaPelatihan.setText(trainingEntity.trainingName)
            edtDeskripsiPelatihan.setText(trainingEntity.desc)
            edtNamaMentor.setText(trainingEntity.mentor)
            edtLinkWag.setText(trainingEntity.linkWa)
            edtParticipantMax.setText(trainingEntity.participantMax.toString())
            val schedule = trainingEntity.schedule
            val temp = schedule.split(", ").toTypedArray()
            edtDay.setText(temp[0])
            edtClock.setText(temp[1])
        }
    }

    private fun initiateObserver() {
        viewModel.editTrainingResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                    renderToast("Maaf gagal menampilkan detail pelatihan")
                }
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    renderToast("Berhasil mengubah pelatihan")
                    findNavController().popBackStack()
                }
            }
        })
    }

    private fun alertDialog() {
        val dialogBuilder = AlertDialog.Builder(activity)
        with(dialogBuilder) {
            setTitle("Peringatan")
            setMessage("Yakin ingin merubah pelatihan ini?")
            setPositiveButton("Iya") { _, _ ->
                confirmEditTraining()
            }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun confirmEditTraining() {
        binding.run {
            val desc = edtDeskripsiPelatihan.text.toString()
            val mentor = edtNamaMentor.text.toString()
            val linkWa = edtLinkWag.text.toString()
            val max = edtParticipantMax.text.toString()
            val day = edtDay.text.toString()
            val clock = edtClock.text.toString()

            viewModel.editTraining(TrainingEntity(
                trainingId = trainingEntity.trainingId,
                desc = desc,
                linkWa = linkWa,
                mentor = mentor,
                schedule = "$day, $clock",
                participantMax = max.toInt()
            ))
        }
    }

    private fun validation(
        desc: String,
        mentor: String,
        linkWa: String,
        participantMax: String,
        day: String,
        time: String,
    ): Boolean = when {
        TextUtils.isEmpty(desc) -> {
            binding.edtDeskripsiPelatihan.error = "harap isi deskripsi pelatihan"
            Toast.makeText(context, "harap isi deskripsi pelatihan", Toast.LENGTH_SHORT).show()
            false
        }
        TextUtils.isEmpty(mentor) -> {
            binding.edtNamaMentor.error = "harap isi nama mentor"
            Toast.makeText(context, "harap isi nama mentor", Toast.LENGTH_SHORT).show()
            false
        }
        TextUtils.isEmpty(linkWa) -> {
            binding.edtLinkWag.error = "harap isi link wa group"
            Toast.makeText(context, "harap isi link wa group", Toast.LENGTH_SHORT).show()
            false
        }
        TextUtils.isEmpty(participantMax) -> {
            binding.edtParticipantMax.error = "harap isi maksimal participant"
            Toast.makeText(context, "harap isi maksimal participant", Toast.LENGTH_SHORT).show()
            false
        }
        TextUtils.isEmpty(day) -> {
            binding.edtDay.error = "harap isi hari pelatihan"
            Toast.makeText(context, "harap isi hari pelatihan", Toast.LENGTH_SHORT).show()
            false
        }
        TextUtils.isEmpty(time) -> {
            binding.edtClock.error = "harap isi waktu role"
            Toast.makeText(context, "harap isi waktu role", Toast.LENGTH_SHORT).show()
            false
        }
        else -> true
    }

    private fun renderToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}