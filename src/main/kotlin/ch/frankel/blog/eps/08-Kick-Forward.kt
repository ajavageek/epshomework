package ch.frankel.blog.eps

import kotlin.reflect.KCallable

fun run(filename: String): Map<String, Int> {
    val function: KCallable<Map<String, Int>> = ::readFile
    return function.call(filename, ::filterChars)
}

fun readFile(filename: String, function: KCallable<Map<String, Int>>): Map<String, Int> {
    val data = read(filename)
    return function.call(data, ::normalize)
}

fun filterChars(lines: List<String>, function: KCallable<Map<String, Int>>): Map<String, Int> {
    val pattern = "\\W|_".toRegex()
    val filtered = lines
        .map { it.replace(pattern, " ") }
    return function.call(filtered, ::scan)
}

fun normalize(lines: List<String>, function: KCallable<Map<String, Int>>): Map<String, Int> {
    val normalized = lines.map { it.toLowerCase() }
    return function.call(normalized, ::removeStopWords)
}

fun scan(lines: List<String>, function: KCallable<Map<String, Int>>): Map<String, Int> {
    val split = lines.flatMap { it.split(" ") }
        .filter { it.isNotBlank() && it.length >= 2 }
    return function.call(split, ::frequencies)
}

fun removeStopWords(words: List<String>, function: KCallable<Map<String, Int>>): Map<String, Int> {
    val stopWords = read("stop_words.txt").flatMap { it.split(",") }
    return function.call(words - stopWords, ::sort)
}

fun frequencies(words: List<String>, function: KCallable<Map<String, Int>>): Map<String, Int> {
    val frequencies = words
        .groupBy { it }
        .map { it.key to it.value.size }
    return function.call(frequencies, ::updateState)
}

fun sort(frequencies: List<Pair<String, Int>>, function: KCallable<Map<String, Int>>): Map<String, Int> {
    val top = frequencies.sortedBy { it.second }.takeLast(25)
    return function.call(top)
}

fun updateState(top: List<Pair<String, Int>>) = top.toMap()