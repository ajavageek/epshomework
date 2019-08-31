package ch.frankel.blog.eps

import java.util.concurrent.ConcurrentMap

fun processWords(words: List<String>, frequencies: ConcurrentMap<String, Int>) {
    val stopWords = read("stop_words.txt")
        .flatMap { it.split(",") }
    words.forEach {
        if (!stopWords.contains(it))
            frequencies.merge(it, 1) { count, value ->
                count + value
            }
    }
}