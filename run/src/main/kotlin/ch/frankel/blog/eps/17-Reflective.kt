package ch.frankel.blog.eps

import kotlin.collections.List
import kotlin.collections.Map

fun run(filename: String): Map<String, Int> {
    val clazz = Class.forName("ch.frankel.blog.eps.Reflective")
    val funcStopWords = clazz.getMethod("stopWords")
    val funcExtractWords = clazz.getMethod("extractWords", String::class.java)
    val funcFrequencies = clazz.getMethod("frequencies", List::class.java)
    val constructor = clazz.getConstructor()
    val instance = constructor.newInstance()
    val words = funcExtractWords.invoke(instance, filename) as List<String>
    val stopWords = funcStopWords.invoke(instance) as List<String>
    return funcFrequencies.invoke(instance, words - stopWords) as Map<String, Int>
}