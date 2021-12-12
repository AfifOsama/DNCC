package com.dncc.dncc.presentation.pelatihan

import android.app.AlertDialog
import android.os.Bundle
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
import com.dncc.dncc.databinding.FragmentListPelatihanBinding
import com.dncc.dncc.presentation.profil.ProfilFragmentArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListPelatihanFragment : Fragment() {
    private var _binding: FragmentListPelatihanBinding? = null
    private val binding get() = _binding!!

    private val args: ProfilFragmentArgs by navArgs()
    private val viewModel: TrainingViewModel by viewModels()
    private var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListPelatihanBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = args.userId ?: ""
        viewModel.getUser(userId)
//        viewModel.getTraining

        initiateUI()
        initiateObserver()
    }

    private fun initiateUI() {
        binding.run {
            toolbar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            toolbar.actionBarTitle.text = getString(R.string.daftar_pelatihan)

            binding.run {
                refresh.run {
                    setOnRefreshListener {
                        CoroutineScope(Dispatchers.Main).launch {
                            Log.i("ListPelatihanFragment", "refresh: ")
//                        viewModel.getUser(userId)
                            delay(2000)
                            isRefreshing = false
                        }
                    }
                }
            }
        }
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
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun initiateObserver() {

    }

}