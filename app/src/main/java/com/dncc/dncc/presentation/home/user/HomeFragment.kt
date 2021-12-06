package com.dncc.dncc.presentation.home.user

import android.graphics.Typeface
import android.os.Bundle
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dncc.dncc.R
import com.dncc.dncc.data.source.local.DataPhotoKegiatan
import com.dncc.dncc.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val list = ArrayList<DataPhotoKegiatan>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textSpanActionBarRole()
        textSpanTelahhadir()
        imgKegiatan()
        binding.btnPelatihan.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_listPelatihanFragment)
        }
        binding.btnPertemuan.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_detailPelatihanFragment)
        }
        binding.headerHome.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profilFragment)
        }

    }

    private fun imgKegiatan() {
        binding.rvImgKegiatan.setHasFixedSize(true)
        list.addAll(listPhotos)
        binding.rvImgKegiatan.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvImgKegiatan.adapter = PhotoKegiatanAdapter(list)
    }

    private fun textSpanTelahhadir() {
        var hadirDari = "4"
        var hadirSampai = "10"
        val text = "Kamu telah hadir $hadirDari dari $hadirSampai pertemuan".toSpannable()
        val spanHadirDari = 17 + hadirDari.length

        text[16..spanHadirDari] = bold

        binding.tvTelahHadir.text = text
    }

    private val bold = StyleSpan(Typeface.BOLD)
    private fun textSpanActionBarRole() {
        var role = "Mobile"
        val text = "Divisi $role".toSpannable()
        val spanRole = 7 + role.length

        text[7..spanRole] = bold

        binding.tvRole.text = text
    }

    private val listPhotos: ArrayList<DataPhotoKegiatan>
        get() {
            val name = resources.getStringArray(R.array.data_name_kegiatan)
            val photo = resources.obtainTypedArray(R.array.data_photo_kegiatan)
            val listPhoto = ArrayList<DataPhotoKegiatan>()
            for (i in name.indices) {
                val photos = DataPhotoKegiatan(photo.getResourceId(i, -1))
                listPhoto.add(photos)
            }
            photo.recycle()
            return listPhoto
        }
}