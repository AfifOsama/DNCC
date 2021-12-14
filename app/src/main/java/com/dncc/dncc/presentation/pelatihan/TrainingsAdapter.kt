package com.dncc.dncc.presentation.pelatihan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dncc.dncc.common.UserRoleEnum
import com.dncc.dncc.databinding.ItemCardPelatihanBinding
import com.dncc.dncc.domain.entity.training.TrainingEntity
import com.dncc.dncc.domain.entity.user.UserEntity
import java.util.*

class TrainingsAdapter(
    private val onClick: (TrainingEntity) -> Unit,
    private val onDelete: (TrainingEntity) -> Unit,
    private val onRegister: (TrainingEntity, UserEntity) -> Unit,
    private val toast: (String) -> Unit
) : RecyclerView.Adapter<TrainingsAdapter.ViewHolder>() {

    private var userEntity: UserEntity = UserEntity()
    private var dataList: List<TrainingEntity> = Collections.emptyList()

    fun setUser(userEntity: UserEntity) {
        this.userEntity = userEntity
        notifyDataSetChanged()
    }

    fun setList(newList: List<TrainingEntity>) {
        this.dataList = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemCardPelatihanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trainingEntity: TrainingEntity) {
            binding.apply {
                tvTrainingName.text = trainingEntity.trainingName
                tvParticipantNow.text = trainingEntity.participantNow.toString()
                tvParticipantMax.text = trainingEntity.participantMax.toString()
                tvSchedule.text = trainingEntity.schedule

                //check if role admin show delete button, if member show register button
                if (userEntity.role == UserRoleEnum.ADMIN.role) {
                    btnDelete.visibility = View.VISIBLE
                    btnDaftar.visibility = View.GONE
                } else {
                    btnDelete.visibility = View.GONE
                    btnDaftar.visibility = View.VISIBLE
                }

                itemView.setOnClickListener {
                    //check if user is registered in this training, if yes invoke, if no show toast
                    if (userEntity.trainingId == trainingEntity.trainingId || userEntity.role == UserRoleEnum.ADMIN.role) {
                        onClick.invoke(trainingEntity)
                    } else {
                        toast.invoke("Kamu belum terdaftar pada kelas ini")
                    }
                }

                btnDaftar.setOnClickListener {
                    //if user is visitor, user cant register training
                    if (userEntity.role == UserRoleEnum.VISITOR.role) {
                        toast("Status akun kamu saat ini tidak bisa mendaftar pelatihan")
                    } else if (userEntity.role == UserRoleEnum.MEMBER.role) {
                        onRegister(trainingEntity, userEntity)
                    }
                }

                btnDelete.setOnClickListener {
                    onDelete.invoke(trainingEntity)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCardPelatihanBinding.inflate(
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