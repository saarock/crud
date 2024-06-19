package com.example.com.example.todo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class User(
    val id: String = UUID.randomUUID().toString(),
    val userType :String? = null,
    val empId: String? = null,
    val empName: String? =null,
    val empEmail: String? = null,
    val empPassword: String? = null,
):Parcelable {
}