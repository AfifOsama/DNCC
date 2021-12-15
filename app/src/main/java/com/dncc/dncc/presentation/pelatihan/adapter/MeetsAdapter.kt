package com.dncc.dncc.presentation.pelatihan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dncc.dncc.databinding.ItemCardPertemuanBinding
import com.dncc.dncc.domain.entity.training.MeetEntity
import java.util.*

class MeetsAdapter(
    private val onClick: (MeetEntity) -> Unit
) : RecyclerView.Adapter<MeetsAdapter.ViewHolder>() {

    private var dataList: List<MeetEntity> = Collections.emptyList()

    fun setList(newList: List<MeetEntity>) {
        this.dataList = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemCardPertemuanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(meetEntity: MeetEntity) {
            binding.apply {
                tvPertemuanKe.text = meetEntity.meetName
                tvMeetDesc.text = meetEntity.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCardPertemuanBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MeetsAdapter.ViewHolder, position: Int) {
        with(holder) {
            bind(dataList[position])
        }
    }

    override fun getItemCount(): Int = dataList.size

}