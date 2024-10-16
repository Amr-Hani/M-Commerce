package com.example.mcommerce.model.network

sealed class ApiState<T> {
    class Success<T>(val data: T) : ApiState<T>()
    class Failure<T>(val message: String) : ApiState<T>()
    class Loading<T> : ApiState<T>()
}