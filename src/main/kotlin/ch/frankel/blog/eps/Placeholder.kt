package ch.frankel.blog.eps

import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

/* For compiling purpose. */
fun run(filename: String): Map<String, Int> {
    val freqSpace = LinkedBlockingQueue<Map<String, Int>>()
    val wordSpace = read(filename)
        .flatMap { it.toLowerCase().split("\\W|_".toRegex()) }
        .filter { it.isNotBlank() && it.length >= 2 }
        .toBlockingQueue()
    val count = 4
    val executorService = Executors.newFixedThreadPool(count)
    val callables = IntRange(1, 4).map { _ ->
        { processWords(wordSpace, freqSpace) }
    }.map { Executors.callable(it) }
    executorService.invokeAll(callables)
    val frequencies = mutableMapOf<String, Int>()
    while (freqSpace.isNotEmpty()) {
        val partial = freqSpace.poll(1, TimeUnit.SECONDS)
        partial?.entries?.forEach {
            frequencies.merge(it.key, it.value) {
                count, value -> count + value
            }
        }
    }
    return frequencies
        .toList()
        .sortedByDescending { it.second }
        .take(25)
        .toMap()
}

private fun <E> List<E>.toBlockingQueue() = LinkedBlockingDeque<E>(this)