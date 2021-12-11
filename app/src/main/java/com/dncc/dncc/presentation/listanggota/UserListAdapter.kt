package com.dncc.dncc.presentation.listanggota

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dncc.dncc.R
import com.dncc.dncc.databinding.ItemCardAdminAnggotaBinding
import com.dncc.dncc.domain.entity.user.UserEntity
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class UserListAdapter(
    private val onClick: (UserEntity) -> Unit,
    private val onEditClick: (UserEntity) -> Unit
) :
    RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    private var dataList: List<UserEntity> = Collections.emptyList()

    fun setList(newList: List<UserEntity>) {
        this.dataList = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemCardAdminAnggotaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userEntity: UserEntity) {
            binding.apply {
                val imagePath =
                    FirebaseStorage.getInstance().reference.child("images").child(userEntity.userId)
                imagePath.downloadUrl.addOnSuccessListener {
                    imgAnggota.load(it.toString()) {
                        placeholder(R.drawable.logodncc)
                        error(R.drawable.logodncc)
                    }
                }.addOnFailureListener {
                    it.message?.let { error -> Log.i("HomeFragment", "error image $error") }
                }

                tvNamaUser.text = userEntity.fullName
                tvNimUser.text = userEntity.nim
                tvDivisi.text = userEntity.training

                btnUbah.setOnClickListener {
                    onEditClick.invoke(userEntity)
                }

                itemView.setOnClickListener {
                    onClick.invoke(userEntity)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCardAdminAnggotaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            bind(dataList[position])
        }
    }

    override fun getItemCount(): Int = dataList.size
}