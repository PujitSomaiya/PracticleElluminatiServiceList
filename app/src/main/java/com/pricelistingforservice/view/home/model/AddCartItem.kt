package com.pricelistingforservice.view.home.model

data class AddCartItem(
    var serviceResponse: ServiceResponse,
    var price : Double = 0.00,
    var isRepeat : Boolean = false
)
