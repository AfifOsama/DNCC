package com.dncc.dncc.presentation.pelatihan.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dncc.dncc.R
import com.dncc.dncc.databinding.ItemCardAnggotaBinding
import com.dncc.dncc.domain.entity.user.UserEntity
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class UsersAdapter(
    private val onClick: (UserEntity) -> Unit
) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private var dataList: List<UserEntity> = Collections.emptyList()

    fun setList(newList: List<UserEntity>) {
        this.dataList = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemCardAnggotaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userEntity: UserEntity) {
            binding.apply {
                val imagePath =
                    FirebaseStorage.getInstance().reference.child("images").child(userEntity.userId)
                imagePath.downloadUrl.addOnSuccessListener {
                    imgUser.load(it.toString()) {
                        placeholder(R.drawable.logodncc)
                        error(R.drawable.logodncc)
                    }
                }.addOnFailureListener {
                    it.message?.let { error -> Log.i("HomeFragment", "error image $error") }
                }

                tvNamaUser.text = userEntity.fullName
                tvNimUser.text = userEntity.nim
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCardAnggotaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UsersAdapter.ViewHolder, position: Int) {
        with(holder) {
            bind(dataList[position])
        }
    }

    override fun getItemCount(): Int = dataList.size

}