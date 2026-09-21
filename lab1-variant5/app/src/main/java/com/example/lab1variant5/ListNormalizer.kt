package com.example.lab1variant5

import kotlin.math.round

class ListNormalizer {

    fun createList(): List<Int> {
        return List(10) { (10..100).random() }
    }

    fun normalize(numbers: List<Int>): List<Double> {
        val maximum = numbers.maxOrNull() ?: 1

        return numbers.map {
            round((it.toDouble() / maximum) * 1000) / 1000
        }
    }

    fun listToString(numbers: List<*>): String {
        return numbers.joinToString(", ")
    }
}