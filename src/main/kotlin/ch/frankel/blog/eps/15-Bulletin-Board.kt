package ch.frankel.blog.eps

import ch.frankel.blog.eps.EventManager.Event

class EventManager {

    class Event<T>(val type: String, val payload: T? = null)

    private val subscriptions = mutableMapOf<String, List<(Event<Any>) -> Unit>>()

    fun <T> subscribe(type: String, handler: (Event<T>) -> Unit) {
        subscriptions.merge(type, listOf(handler as (Event<Any>) -> Unit)) { existing, new ->
            existing + new
        }
    }

    fun <T> publish(event: Event<T>) = subscriptions[event.type]?.let { handlers ->
        handlers.forEach { it(event as Event<Any>) }
    }
}

class DataStorage(private val eventManager: EventManager) {

    private lateinit var data: List<String>

    init {
        eventManager.subscribe<String>("load") { load(it) }
        eventManager.subscribe<Unit>("start") { produceWords(it) }
    }

    private fun load(event: Event<String>) {
        if (event.payload != null) {
            data = read(event.payload)
                .flatMap { it.split("\\W|_".toRegex()) }
                .filter { it.isNotBlank() && it.length >= 2 }
                .map(String::toLowerCase)
        }
    }

    private fun produceWords(@Suppress("UNUSED_PARAMETER") ignore: Event<Unit>) {
        for (word in data) {
            eventManager.publish(Event("word", word))
        }
        eventManager.publish(Event<Unit>("eof"))
    }
}

class StopWordsFilter(private val eventManager: EventManager) {

    private lateinit var stopWords: List<String>

    init {
        eventManager.subscribe<Unit>("load") { load(it) }
        eventManager.subscribe<String>("word") { isStopWord(it) }
    }

    private fun load(@Suppress("UNUSED_PARAMETER") ignore: Event<Unit>) {
        stopWords = read("stop_words.txt")[0].split(",")
    }

    private fun isStopWord(event: Event<String>) {
        if (!stopWords.contains(event.payload))
            eventManager.publish(Event("validWord", event.payload))
    }
}

class WordFrequencyCounter(private val eventManager: EventManager) {

    private val wordFreqs = mutableMapOf<String, Int>()

    init {
        eventManager.subscribe<String>("validWord") { incrementCount(it) }
        eventManager.subscribe<Unit>("getResult") { getTop25(it) }
    }

    private fun incrementCount(event: Event<String>) {
        if (event.payload != null) {
            wordFreqs.merge(event.payload, 1) { value, _ -> value + 1 }
        }
    }

    private fun getTop25(@Suppress("UNUSED_PARAMETER") ignore: Event<Unit>) {
        eventManager.publish(Event("result", wordFreqs.toList().sortedByDescending { it.second }.take(25).toMap()))
    }
}

class WordFrequencyApplication(private val eventManager: EventManager) {

    lateinit var result: Map<String, Int>

    init {
        eventManager.subscribe<String>("run") { run(it) }
        eventManager.subscribe<String>("eof") { stop(it) }
        eventManager.subscribe<Map<String, Int>>("result") { store(it) }
    }

    private fun stop(@Suppress("UNUSED_PARAMETER") ignore: Event<String>) {
        eventManager.publish<Unit>(Event("getResult"))
    }

    private fun store(event: Event<Map<String, Int>>) {
        if (event.payload != null) { result = event.payload }
    }

    private fun run(event: Event<String>) {
        eventManager.publish(Event("load", event.payload))
        eventManager.publish(Event("start", Unit))
    }
}
