package com.example.newapp.network


data class NetworkStates<out T>(var status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): NetworkStates<T> = NetworkStates(status = Status.SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String): NetworkStates<T> =
            NetworkStates(status = Status.ERROR, data = data, message = message)

        fun <T> loading(data: T?): NetworkStates<T> = NetworkStates(status = Status.LOADING, data = data, message = null)
    }
}