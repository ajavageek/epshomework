package ch.frankel.blog.eps

import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

typealias WordFrequency = Pair<String, Int>
typealias WordFrequencies = List<WordFrequency>
typealias Lines = List<String>
typealias Words = List<String>
typealias MapFunction = KFunction1<WordFrequencies, Map<String, Int>>
typealias SortFunction = KFunction2<WordFrequencies, MapFunction, Map<String, Int>>
typealias FrequencyFunction = KFunction2<Words, SortFunction, Map<String, Int>>
typealias RemoveFunction = KFunction2<Lines, FrequencyFunction, Map<String, Int>>
typealias ScanFunction = KFunction2<Lines, RemoveFunction, Map<String, Int>>
typealias NormalizeFunction = KFunction2<Lines, ScanFunction, Map<String, Int>>
typealias ReadFunction = KFunction2<Lines, NormalizeFunction, Map<String, Int>>

fun run(filename: String): Map<String, Int> {
    val function = ::readFile
    return function(filename, ::filterChars)
}

fun readFile(filename: String, function: ReadFunction): Map<String, Int> {
    val data = read(filename)
    return function(data, ::normalize)
}

fun filterChars(lines: List<String>, function: NormalizeFunction): Map<String, Int> {
    val pattern = "\\W|_".toRegex()
    val filtered = lines
        .map { it.replace(pattern, " ") }
    return function(filtered, ::scan)
}

fun normalize(lines: List<String>, function: ScanFunction): Map<String, Int> {
    val normalized = lines.map { it.toLowerCase() }
    return function(normalized, ::removeStopWords)
}

fun scan(lines: Lines, function: RemoveFunction): Map<String, Int> {
    val split = lines.flatMap { it.split(" ") }
        .filter { it.isNotBlank() && it.length >= 2 }
    return function(split, ::frequencies)
}

fun removeStopWords(words: Words, function: FrequencyFunction): Map<String, Int> {
    val stopWords = read("stop_words.txt").flatMap { it.split(",") }
    return function(words - stopWords, ::sort)
}

fun frequencies(words: Words, function: SortFunction): Map<String, Int> {
    val frequencies = words
        .groupBy { it }
        .map { it.key to it.value.size }
    return function(frequencies, ::updateState)
}

fun sort(frequencies: WordFrequencies, function: MapFunction): Map<String, Int> {
    val top = frequencies.sortedBy { it.second }.takeLast(25)
    return function(top)
}

fun updateState(top: WordFrequencies) = top.toMap()