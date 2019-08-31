package ch.frankel.blog.eps

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/* For compiling purpose. */
fun run(filename: String): Map<String, Int> {
    val freqSpace = ConcurrentHashMap<String, Int>()
    val words = read(filename)
        .flatMap { it.toLowerCase().split("\\W|_".toRegex()) }
        .filter { it.isNotBlank() && it.length >= 2 }
    val count = 4
    val callables = words.chunked(words.size / count)
        .map { Runnable { processWords(it, freqSpace) }}
        .map { Executors.callable(it) }
    val executorService = Executors.newFixedThreadPool(count)
    executorService.invokeAll(callables)
    return freqSpace
        .toList()
        .sortedByDescending { it.second }
        .take(25)
        .toMap()
}