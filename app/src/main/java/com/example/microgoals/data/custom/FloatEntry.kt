package com.example.microgoals.data.custom


data class FloatEntry(val x: Float, val y: Float)

fun convertToFloatEntryList(data: List<Pair<Float, Float>>): List<FloatEntry> {
    return data.map { (x, y) -> FloatEntry(x, y) }
}
