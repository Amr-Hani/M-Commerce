package com.example.mcommerce.model.network

sealed class ApiState {
    class OnSuccess<E>(val data:E):ApiState()
    class OnFailed(val msg:Throwable):ApiState()
    object Loading:ApiState()
}