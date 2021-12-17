package com.dncc.dncc.presentation.pertemuan

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dncc.dncc.R
import com.dncc.dncc.common.Resource
import com.dncc.dncc.databinding.FragmentEditPertemuanBinding
import com.dncc.dncc.domain.entity.training.MeetEntity
import com.dncc.dncc.utils.getRealPathFromURI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPertemuanFragment : Fragment() {

    private var _binding: FragmentEditPertemuanBinding? = null
    private val binding get() = _binding!!

    private val args: EditPertemuanFragmentArgs by navArgs()
    private val viewModel by viewModels<MeetViewModel>()

    private var meetEntity = MeetEntity()
    private var trainingName = ""

    private var fileReport: Uri? = null
    private var filePath: String = ""
    private var fileName: String = ""

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
        trainingName = args.trainingName ?: ""
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

            if (meetEntity.filePath != "") {
                tvUploadFile.text = meetEntity.filePath
            }

            btnSimpan.setOnClickListener {
                alertDialog()
            }

            tvUploadFile.setOnClickListener {
                getFile()
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
                    renderToast("Berhasil mengubah detail pertemuan")
                    findNavController().popBackStack()
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
                        filePath = fileName,
                        meetName = binding.edtNamaPertemuan.text.toString()
                    ),
                    filePath = filePath,
                    trainingName = trainingName
                )
            }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun getFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                val permission =
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(requireActivity(), permission, 200)
            } else {
                openFile()
            }
        } else {
            openFile()
        }
    }

    private fun openFile() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "*/*"
        resultLauncher.launch(intent, null)
    }

    //minimum req appcompat:1.3.1
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                fileReport = it?.data?.data
                if (fileReport != null) {
                    fileName = fileReport?.lastPathSegment ?: ""
                    binding.tvUploadFile.text = fileName
                    filePath = it?.data?.data?.getRealPathFromURI(requireContext()) ?: ""
                } else {
                    renderToast("Tidak ada gambar diambil")
                }
            } else {
                renderToast("Terjadi kesalahan mohon pilih gambar lagi")
            }
        }

    private fun renderToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}