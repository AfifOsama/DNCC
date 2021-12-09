package com.dncc.dncc.presentation.pelatihan

import androidx.lifecycle.ViewModel
import com.dncc.dncc.domain.use_case.training.GetTrainingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val getTrainingsUseCase: GetTrainingsUseCase
) : ViewModel() {


}