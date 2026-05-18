package com.example.claritypay.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.example.claritypay.domain.models.PricingPlan
import com.example.claritypay.domain.usecases.GetPricingPlansUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlansViewModel(
    private val getPricingPlansUseCase: GetPricingPlansUseCase
) : ViewModel() {

    private val _plans = MutableStateFlow<List<PricingPlan>>(emptyList())
    val plans: StateFlow<List<PricingPlan>> = _plans.asStateFlow()

    init {
        _plans.value = getPricingPlansUseCase()
    }
}