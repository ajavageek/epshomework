package ch.frankel.blog.eps

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe

class LoadEvent(val filename: String)
object EOFEvent
class WordEvent(val word: String)
class ValidWordEvent(val word: String)
class ResultEvent(val result: Map<String, Int>)
object GetResultEvent
class RunEvent(val arg: String)
object StartEvent

class DataStorage(private val eventBus: EventBus) {

    private lateinit var data: List<String>

    init {
        eventBus.register(this)
    }

    @Subscribe
    private fun load(event: LoadEvent) {
        data = read(event.filename)
            .flatMap { it.split("\\W|_".toRegex()) }
            .filter { it.isNotBlank() && it.length >= 2 }
            .map(String::toLowerCase)
    }

    @Subscribe
    private fun produceWords(event: StartEvent) {
        for (word in data) {
            eventBus.post(WordEvent(word))
        }
        eventBus.post(EOFEvent)
    }
}

class StopWordsFilter(private val eventBus: EventBus) {

    private lateinit var stopWords: List<String>

    init {
        eventBus.register(this)
    }

    @Subscribe
    private fun load(event: LoadEvent) {
        stopWords = read("stop_words.txt")[0].split(",")
    }

    @Subscribe
    private fun isStopWord(event: WordEvent) {
        if (!stopWords.contains(event.word))
            eventBus.post(ValidWordEvent(event.word))
    }
}

class WordFrequencyCounter(private val eventBus: EventBus) {

    private val wordFreqs = mutableMapOf<String, Int>()

    init {
        eventBus.register(this)
    }

    @Subscribe
    private fun incrementCount(event: ValidWordEvent) {
        wordFreqs.merge(event.word, 1) { value, _ -> value + 1 }
    }

    @Subscribe
    private fun getTop25(event: GetResultEvent) {
        eventBus.post(
            ResultEvent(
                wordFreqs.toList().sortedByDescending { it.second }.take(25).toMap()
            )
        )
    }
}

class WordFrequencyApplication(private val eventBus: EventBus) {

    lateinit var result: Map<String, Int>

    init {
        eventBus.register(this)
    }

    @Subscribe
    private fun stop(event: EOFEvent) {
        eventBus.post(GetResultEvent)
    }

    @Subscribe
    private fun store(event: ResultEvent) {
        result = event.result
    }

    @Subscribe
    private fun run(event: RunEvent) {
        eventBus.post(LoadEvent(event.arg))
        eventBus.post(StartEvent)
    }
}