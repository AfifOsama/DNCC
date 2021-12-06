package com.dncc.dncc.presentation.home.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.dncc.dncc.R
import com.dncc.dncc.data.source.local.DataPhotoKegiatan

class PhotoKegiatanAdapter(private val list: ArrayList<DataPhotoKegiatan>) :
    RecyclerView.Adapter<PhotoKegiatanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgView: ImageView = view
            .findViewById(R.id.img_kegiatan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_image_kegiatan,
                parent,
                false
            )
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (photo) = list[position]

        holder.imgView.setImageResource(photo)

    }

    override fun getItemCount(): Int {
        return list.size
    }
}