package ch.frankel.blog.eps

import ch.frankel.blog.eps.EventManager.Event
import ch.frankel.blog.eps.EventManager.Type.*

typealias EventConsumer = (Event<out Any>) -> Unit

class EventManager {

    enum class Type {
        Load, Start, Word, EOF, ValidWord, GetResult, Result, Run
    }

    class Event<T>(val type: Type, val payload: T? = null)

    private val subscriptions = mutableMapOf<Type, List<EventConsumer>>()

    fun <T> subscribe(type: Type, handler: (Event<T>) -> Unit) {
        subscriptions.merge(type, listOf(handler as EventConsumer)) { existing, new ->
            existing + new
        }
    }

    fun publish(event: Event<out Any>) = subscriptions[event.type]?.let { handlers ->
        handlers.forEach { it(event) }
    }
}

class DataStorage(private val eventManager: EventManager) {

    private lateinit var data: List<String>

    init {
        eventManager.subscribe<String>(Load) { load(it) }
        eventManager.subscribe<Unit>(Start) { produceWords() }
    }

    private fun load(event: Event<String>) {
        if (event.payload != null) {
            data = read(event.payload)
                .flatMap { it.split("\\W|_".toRegex()) }
                .filter { it.isNotBlank() && it.length >= 2 }
                .map(String::toLowerCase)
        }
    }

    private fun produceWords() {
        for (word in data) {
            eventManager.publish(Event(Word, word))
        }
        eventManager.publish(Event(EOF))
    }
}

class StopWordsFilter(private val eventManager: EventManager) {

    private lateinit var stopWords: List<String>

    init {
        eventManager.subscribe<Unit>(Load) { load(it) }
        eventManager.subscribe<String>(Word) { isStopWord(it) }
    }

    private fun load(@Suppress("UNUSED_PARAMETER") ignore: Event<Unit>) {
        stopWords = read("stop_words.txt")[0].split(",")
    }

    private fun isStopWord(event: Event<String>) {
        if (!stopWords.contains(event.payload))
            eventManager.publish(Event(ValidWord, event.payload))
    }
}

class WordFrequencyCounter(private val eventManager: EventManager) {

    private val wordFreqs = mutableMapOf<String, Int>()

    init {
        eventManager.subscribe<String>(ValidWord) { incrementCount(it) }
        eventManager.subscribe<Unit>(GetResult) { getTop25() }
    }

    private fun incrementCount(event: Event<String>) {
        if (event.payload != null) wordFreqs.merge(event.payload, 1) { value, _ -> value + 1 }
    }

    private fun getTop25() {
        eventManager.publish(
            Event(
                Result,
                wordFreqs.toList().sortedByDescending { it.second }.take(25).toMap()
            )
        )
    }
}

class WordFrequencyApplication(private val eventManager: EventManager) {

    lateinit var result: Map<String, Int>

    init {
        eventManager.subscribe<String>(Run) { run(it) }
        eventManager.subscribe<Unit>(EOF) { stop() }
        eventManager.subscribe<Map<String, Int>>(Result) { store(it) }
    }

    private fun stop() {
        eventManager.publish(Event(GetResult))
    }

    private fun store(event: Event<Map<String, Int>>) {
        if (event.payload != null) result = event.payload
    }

    private fun run(event: Event<String>) {
        eventManager.publish(Event(Load, event.payload))
        eventManager.publish(Event(Start))
    }
}
