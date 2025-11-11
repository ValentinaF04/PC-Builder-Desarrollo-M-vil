package com.example.pcbuilder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Int =0,
    val name: String,
    val email: String,
    val password: String,
    val isAdmin: Boolean = false
)