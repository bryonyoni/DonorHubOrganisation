package com.bry.donorhuborganisation.Models

import java.lang.Long
import java.security.MessageDigest
import java.util.*

class Block(var data: String, var previousHash: String, var donation_id: String) {
    var timestamp = Calendar.getInstance().timeInMillis

    fun calculateHash(): String{
        var string = previousHash+timestamp.toString()+data
        return hashString(string, "SHA-256")
    }

    fun checkIfDataGeneratesSameHash(unverified_data: String): Boolean{
        var string = previousHash+timestamp.toString()+unverified_data
        return hashString(string, "SHA-256").equals(calculateHash())
    }

    private fun hashString(input: String, algorithm: String): String {
        return MessageDigest
                .getInstance(algorithm)
                .digest(input.toByteArray())
                .fold("", { str, it -> str + "%02x".format(it) })
    }


}