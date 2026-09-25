package com.example.data.util

import java.security.MessageDigest

object SecurityHelper {
    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, expectedHash: String): Boolean {
        val calculated = hashPassword(password)
        return calculated.equals(expectedHash, ignoreCase = true)
    }
}
