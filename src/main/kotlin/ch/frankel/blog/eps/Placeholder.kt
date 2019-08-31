package ch.frankel.blog.eps

import com.hazelcast.core.Hazelcast
import com.hazelcast.core.IExecutorService

/* For compiling purpose. */
fun run(filename: String): Map<String, Int> {
    val hazelcastInstance = Hazelcast.newHazelcastInstance()
    val freqSpace = hazelcastInstance.getMap<String, Int>("map")
    val executorService: IExecutorService = hazelcastInstance.getExecutorService("executorService")
    val words = read(filename)
        .flatMap { it.toLowerCase().split("\\W|_".toRegex()) }
        .filter { it.isNotBlank() && it.length >= 2 }
    val callables = words.chunked(words.size / 4)
        .map { ProcessWords(it) }
    executorService.invokeAll(callables)
    val result = freqSpace
        .toList()
        .sortedByDescending { it.second }
        .take(25)
        .toMap()
    hazelcastInstance.shutdown()
    return result
}