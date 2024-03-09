package com.pricelistingforservice.view.home.viewmodel

import androidx.lifecycle.ViewModel
import com.pricelistingforservice.view.home.model.AddCartItem
import com.pricelistingforservice.view.home.model.ServiceResponse
import com.pricelistingforservice.view.home.model.SpecificationsItem


class CommonViewModel : ViewModel() {

    lateinit var serviceResponse: ServiceResponse
    var specifications: List<SpecificationsItem> = arrayListOf()
    var addToCartItem: ArrayList<AddCartItem> = arrayListOf()
    var currentPrice = 0.00
    var mainCount = 1
}