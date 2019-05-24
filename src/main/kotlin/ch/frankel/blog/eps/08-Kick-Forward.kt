package ch.frankel.blog.eps

import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

fun run(filename: String): Map<String, Int> {
    val function: KFunction2<
            String,
            KFunction2<
                    List<String>,
                    KFunction2<
                            List<String>,
                            KFunction2<
                                    List<String>,
                                    KFunction2<
                                            List<String>,
                                            KFunction2<
                                                    List<String>,
                                                    KFunction2<
                                                            List<Pair<String, Int>>,
                                                            KFunction1<List<Pair<String, Int>>, Map<String, Int>>,
                                                            Map<String, Int>>,
                                                    Map<String, Int>>,
                                            Map<String, Int>>,
                                    Map<String, Int>>,
                            Map<String, Int>>,
                    Map<String, Int>>,
            Map<String, Int>> = ::readFile
    return function(filename, ::filterChars)
}

fun readFile(
    filename: String,
    function: KFunction2<
            List<String>,
            KFunction2<
                    List<String>,
                    KFunction2<
                            List<String>,
                            KFunction2<
                                    List<String>,
                                    KFunction2<
                                            List<String>,
                                            KFunction2<
                                                    List<Pair<String, Int>>,
                                                    KFunction1<List<Pair<String, Int>>, Map<String, Int>>,
                                                    Map<String, Int>>,
                                            Map<String, Int>>,
                                    Map<String, Int>>,
                            Map<String, Int>>,
                    Map<String, Int>>,
            Map<String, Int>>
): Map<String, Int> {
    val data = read(filename)
    return function(data, ::normalize)
}

fun filterChars(
    lines: List<String>,
    function: KFunction2<
            List<String>,
            KFunction2<
                    List<String>,
                    KFunction2<
                            List<String>,
                            KFunction2<
                                    List<String>,
                                    KFunction2<
                                            List<Pair<String, Int>>,
                                            KFunction1<List<Pair<String, Int>>, Map<String, Int>>,
                                            Map<String, Int>>,
                                    Map<String, Int>>,
                            Map<String, Int>>,
                    Map<String, Int>>,
            Map<String, Int>>
): Map<String, Int> {
    val pattern = "\\W|_".toRegex()
    val filtered = lines
        .map { it.replace(pattern, " ") }
    return function(filtered, ::scan)
}

fun normalize(
    lines: List<String>,
    function: KFunction2<
            List<String>,
            KFunction2<
                    List<String>,
                    KFunction2<
                            List<String>,
                            KFunction2<
                                    List<Pair<String, Int>>,
                                    KFunction1<List<Pair<String, Int>>, Map<String, Int>>,
                                    Map<String, Int>>,
                            Map<String, Int>>,
                    Map<String, Int>>,
            Map<String, Int>>
): Map<String, Int> {
    val normalized = lines.map { it.toLowerCase() }
    return function(normalized, ::removeStopWords)
}

fun scan(
    lines: List<String>,
    function: KFunction2<
            List<String>,
            KFunction2<
                    List<String>,
                    KFunction2<
                            List<Pair<String, Int>>,
                            KFunction1<List<Pair<String, Int>>, Map<String, Int>>,
                            Map<String, Int>>,
                    Map<String, Int>>,
            Map<String, Int>>
): Map<String, Int> {
    val split = lines.flatMap { it.split(" ") }
        .filter { it.isNotBlank() && it.length >= 2 }
    return function(split, ::frequencies)
}

fun removeStopWords(
    words: List<String>,
    function: KFunction2<
            List<String>,
            KFunction2<
                    List<Pair<String, Int>>,
                    KFunction1<List<Pair<String, Int>>, Map<String, Int>>,
                    Map<String, Int>>,
            Map<String, Int>>
): Map<String, Int> {
    val stopWords = read("stop_words.txt").flatMap { it.split(",") }
    return function(words - stopWords, ::sort)
}

fun frequencies(
    words: List<String>,
    function: KFunction2<
            List<Pair<String, Int>>,
            KFunction1<List<Pair<String, Int>>, Map<String, Int>>,
            Map<String, Int>>
): Map<String, Int> {
    val frequencies = words
        .groupBy { it }
        .map { it.key to it.value.size }
    return function(frequencies, ::updateState)
}

fun sort(
    frequencies: List<Pair<String, Int>>,
    function: KFunction1<List<Pair<String, Int>>, Map<String, Int>>
): Map<String, Int> {
    val top = frequencies.sortedBy { it.second }.takeLast(25)
    return function(top)
}

fun updateState(top: List<Pair<String, Int>>) = top.toMap()