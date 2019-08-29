package ch.frankel.blog.eps

import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit

fun processWords(words: BlockingQueue<String>, frequencies: BlockingQueue<Map<String, Int>>) {
    val stopWords = read("stop_words.txt")
        .flatMap { it.split(",") }
    val wordFreq = mutableMapOf<String, Int>()
    while (words.isNotEmpty()) {
        val word = words.poll(1, TimeUnit.SECONDS)
        if (word != null && !stopWords.contains(word))
            wordFreq.merge(word, 1) {
                count, value -> count + value
            }
    }
    frequencies.put(wordFreq)
}