package com.dncc.dncc.presentation.pertemuan

import android.R.attr
import android.os.Bundle
import android.os.Environment
import android.util.Log
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
import com.dncc.dncc.common.UserRoleEnum
import com.dncc.dncc.databinding.FragmentDetailPertemuanBinding
import com.dncc.dncc.domain.entity.training.MeetEntity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import android.R.attr.path
import java.nio.file.Files


@AndroidEntryPoint
class DetailPertemuanFragment : Fragment() {

    private var _binding: FragmentDetailPertemuanBinding? = null
    private val binding get() = _binding!!

    private val args: DetailPertemuanFragmentArgs by navArgs()
    private val viewModel by viewModels<MeetViewModel>()

    private var trainingId = ""
    private var trainingName = ""
    private var meetId = ""
    private var role = UserRoleEnum.MEMBER.role
    private var meetEntity = MeetEntity()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPertemuanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        meetId = args.meetId ?: ""
        trainingId = args.trainingId ?: ""
        trainingName = args.trainingName ?: ""
        role = args.role ?: UserRoleEnum.MEMBER.role

        viewModel.getMeet(trainingId, meetId)

        initiateUI()
        initiateObserver()
    }

    private fun initiateUI() {
        binding.run {
            toolbar.actionBarTitle.text = getString(R.string.detail_pertemuan)
            toolbar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            if (role == UserRoleEnum.ADMIN.role || role == UserRoleEnum.MENTOR.role) {
                btnUbah.visibility = View.VISIBLE
            }

            btnUbah.setOnClickListener {
                findNavController().navigate(
                    DetailPertemuanFragmentDirections.actionDetailPertemuanFragmentToEditPertemuanFragment(
                        meetEntity,
                        trainingName
                    )
                )
            }

            btnDownload.setOnClickListener {
                progress.visibility = View.VISIBLE

                val storageReference =
                    Firebase.storage.reference.child(trainingName).child(meetEntity.fileName)
                val ONE_MEGABYTE: Long = 1024 * 1024
                storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                    val path = meetEntity.fileName
                    val folderFile = File("Download\\dncc\\")
                    folderFile.mkdirs()

                    val myFile = File(folderFile, meetEntity.fileName)
                    myFile.createNewFile()
                    Log.i(
                        "DetailPertemuanFragment",
                        "file path $path"
                    )
                    progress.visibility = View.GONE
                }.addOnFailureListener { exception ->
                    Log.i(
                        "DetailPertemuanFragment",
                        "firebase local item file not created created $exception"
                    )
                    progress.visibility = View.GONE
                }
            }
        }
    }

    private fun initiateObserver() {
        viewModel.getMeetResponse.observe(viewLifecycleOwner, {
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
                    setContent(it.data ?: MeetEntity())
                    meetEntity = it.data ?: MeetEntity()
                }
            }
        })
    }

    private fun setContent(meetEntity: MeetEntity) {
        binding.run {
            tvPertemuanKe.text = "Detail ${meetEntity.meetName}"
            tvDescription.text = meetEntity.description

            if (meetEntity.fileName != "") {
                tvFileName.text = meetEntity.fileName
                btnDownload.visibility = View.VISIBLE
            } else {
                tvFileName.text = getString(R.string.belum_ada_file_diupload)
            }
        }
    }

    private fun renderToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}