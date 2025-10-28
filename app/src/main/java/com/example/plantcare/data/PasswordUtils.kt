package com.example.plantcare.data

import java.security.MessageDigest

object PasswordUtils {
    fun hashPassword(password: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
    }
}