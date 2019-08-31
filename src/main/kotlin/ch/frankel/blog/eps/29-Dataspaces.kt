package ch.frankel.blog.eps

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.HazelcastInstanceAware
import java.io.Serializable
import java.util.concurrent.Callable

class ProcessWords(val words: List<String>) : Callable<Unit>, Serializable, HazelcastInstanceAware {

    private lateinit var hazelcastInstance: HazelcastInstance

    override fun setHazelcastInstance(hazelcastInstance: HazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance
    }

    override fun call() {
        val frequencies = hazelcastInstance.getMap<String, Int>("map")
        val stopWords = read("stop_words.txt")
            .flatMap { it.split(",") }
        words.forEach {
            if (!stopWords.contains(it))
                frequencies.merge(it, 1) { count, value ->
                    count + value
                }
        }
    }
}