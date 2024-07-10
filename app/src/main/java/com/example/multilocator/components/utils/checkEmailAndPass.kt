package com.example.multilocator.components.utils

import androidx.compose.runtime.mutableStateOf

fun checkEmailAndPassword(email: String, password: String): Boolean {

    val flag = mutableStateOf(false)

    flag.value = email.trim().endsWith("@gmail.com") && password.trim().length >= 6

    return flag.value
}