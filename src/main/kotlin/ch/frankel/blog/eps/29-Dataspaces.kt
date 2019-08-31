package ch.frankel.blog.eps

import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.TimeUnit

fun processWords(words: BlockingQueue<String>, frequencies: ConcurrentMap<String, Int>) {
    val stopWords = read("stop_words.txt")
        .flatMap { it.split(",") }
    while (words.isNotEmpty()) {
        val word = words.poll(1, TimeUnit.SECONDS)
        if (word != null && !stopWords.contains(word))
            frequencies.merge(word, 1) {
                count, value -> count + value
            }
    }
}