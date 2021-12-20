package com.dncc.dncc.presentation.pertemuan

import android.app.AlertDialog
import android.os.Bundle
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
import com.dncc.dncc.databinding.FragmentEditPertemuanBinding
import com.dncc.dncc.domain.entity.training.MeetEntity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPertemuanFragment : Fragment() {

    private var _binding: FragmentEditPertemuanBinding? = null
    private val binding get() = _binding!!

    private val args: EditPertemuanFragmentArgs by navArgs()
    private val viewModel by viewModels<MeetViewModel>()

    private var meetEntity = MeetEntity()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPertemuanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        meetEntity = args.meetEntity
        initiateUI()
        initiateObserver()
    }

    private fun initiateUI() {
        binding.run {
            toolbar.actionBarTitle.text = getString(R.string.ubah_pertemuan)
            toolbar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            edtNamaPertemuan.setText(meetEntity.meetName)
            edtDesc.setText(meetEntity.description)

            btnSimpan.setOnClickListener {
                alertDialog()
            }

            btnBatal.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun initiateObserver() {
        viewModel.editMeetResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                    renderToast("Maaf gagal mengubah detail pertemuan")
                }
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    if (it.data == true) {
                        renderToast("Berhasil mengubah detail pertemuan")
                        findNavController().popBackStack()
                    } else {
                        renderToast("Gagal mengubah detail pertemuan")
                    }
                }
            }
        })
    }

    private fun alertDialog() {
        val dialogBuilder = AlertDialog.Builder(activity)
        with(dialogBuilder) {
            setTitle("Peringatan")
            setMessage("Yakin ingin mengubah pertemuan ini?")
            setPositiveButton("Iya") { _, _ ->
                viewModel.editMeet(
                    meetEntity = MeetEntity(
                        trainingId = meetEntity.trainingId,
                        meetId = meetEntity.meetId,
                        description = binding.edtDesc.text.toString(),
                        fileDownloadLink = binding.edtFile.text.toString(),
                        meetName = binding.edtNamaPertemuan.text.toString()
                    ),
                )
            }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun renderToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}