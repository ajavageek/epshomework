package ch.frankel.blog.eps

import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.primaryConstructor

class Reflective {

    fun stopWords() = read("stop_words.txt")
        .flatMap { it.split(",") }

    fun extractWords(filename: String) = read(filename)
        .flatMap { it.toLowerCase().split("\\W|_".toRegex()) }
        .filter { it.isNotBlank() && it.length >= 2 }

    fun frequencies(words: List<String>) = words.groupBy { it }
        .map { it.key to it.value.size }
        .sortedBy { it.second }
        .takeLast(25)
        .toMap()
}

fun run(filename: String): Map<String, Int> {
    val clazz = Class.forName("ch.frankel.blog.eps.Reflective").kotlin
    val funcStopWords = clazz.declaredMemberFunctions.single { it.name == "stopWords" }
    val funcExtractWords = clazz.declaredMemberFunctions.single { it.name == "extractWords" }
    val funcFrequencies = clazz.declaredMemberFunctions.single { it.name == "frequencies" }
    val constructor = clazz.primaryConstructor
    val instance = constructor?.call()
    val words = funcExtractWords.call(instance, filename) as List<String>
    val stopWords = funcStopWords.call(instance) as List<String>
    return funcFrequencies.call(instance, words - stopWords) as Map<String, Int>
}

