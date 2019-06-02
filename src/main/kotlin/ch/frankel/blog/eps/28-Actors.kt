package ch.frankel.blog.eps

import ch.frankel.blog.eps.DataStorageManager.SendWordFrequencies
import ch.frankel.blog.eps.Message.Die
import ch.frankel.blog.eps.WordFrequencyController.Result
import ch.frankel.blog.eps.WordFrequencyManager.Top25
import ch.frankel.blog.eps.WordFrequencyManager.Word
import java.util.*

abstract class Actor : Runnable {

    private val queue = ArrayDeque<Message>()
    private var stop = false

    final override fun run() {
        println("[${Thread.currentThread().name}] Starting")
        while (!stop) {
            val message: Message? = queue.poll()
            if (message == null)
                println("[${Thread.currentThread().name}] Empty queue. Passing")
            else {
                println("[${Thread.currentThread().name}] Dispatching $message")
                dispatch(message)
                if (message == Die) stop = true
            }
        }
        println("[${Thread.currentThread().name}] Stopping")
    }

    abstract fun dispatch(message: Message)

    fun send(message: Message) = queue.add(message)
}

class DataStorageManager : Actor() {

    class Init(val filename: String, val stopWordManager: StopWordManager) : Message()
    class SendWordFrequencies(val receiver: WordFrequencyController) : Message()

    private lateinit var lines: List<String>
    private lateinit var stopWordManager: StopWordManager

    override fun dispatch(message: Message) {
        when (message) {
            is Init -> initialize(message)
            is SendWordFrequencies -> processWords(message)
            else -> stopWordManager.send(message)
        }
    }

    private fun initialize(message: Init) {
        lines = read(message.filename)
        stopWordManager = message.stopWordManager
    }

    private fun processWords(message: SendWordFrequencies) {
        val words = lines
            .flatMap { it.toLowerCase().split("\\W|_".toRegex()) }
            .filter { it.isNotBlank() && it.length >= 2 }
        for (word in words) {
            stopWordManager.send(StopWordManager.Filter(word))
        }
        stopWordManager.send(Top25(message.receiver))
    }
}

class StopWordManager : Actor() {

    private lateinit var stopWords: List<String>
    private lateinit var wordFrequencyManager: WordFrequencyManager

    class Init(val wordFrequencyManager: WordFrequencyManager) : Message()
    class Filter(val word: String) : Message()

    override fun dispatch(message: Message) {
        when (message) {
            is Init -> initialize(message)
            is Filter -> filter(message)
            else -> wordFrequencyManager.send(message)
        }
    }

    private fun filter(message: Filter) {
        if (!stopWords.contains(message.word)) {
            wordFrequencyManager.send(Word(message.word))
        }
    }

    private fun initialize(message: Init) {
        stopWords = read("stop_words.txt")
            .flatMap { it.split(",") }
        wordFrequencyManager = message.wordFrequencyManager
    }
}

class WordFrequencyManager : Actor() {

    class Word(val word: String) : Message()
    class Top25(val receiver: WordFrequencyController) : Message()

    private val wordFrequencies = mutableMapOf<String, Int>()

    override fun dispatch(message: Message) {
        when (message) {
            is Word -> incrementCount(message)
            is Top25 -> top25(message)
        }
    }

    private fun top25(message: Top25) {
        val result = Result(wordFrequencies
            .toList()
            .sortedByDescending { it.second })
        message.receiver.send(result)
    }

    private fun incrementCount(word: Word) {
        wordFrequencies.merge(word.word, 1) { count, _ -> count + 1 }
    }
}

class WordFrequencyController : Actor() {

    class Result(val frequencies: List<Pair<String, Int>>) : Message()
    class Run(val dataStorageManager: DataStorageManager) : Message()

    private lateinit var dataStorageManager: DataStorageManager
    private lateinit var result: Map<String, Int>

    override fun dispatch(message: Message) {
        println("[${Thread.currentThread().name}] Dispatching $message")
        when (message) {
            is Run -> run(message.dataStorageManager)
            is Result -> fill(message)
        }
    }

    private fun run(dataStorageManager: DataStorageManager) {
        this.dataStorageManager = dataStorageManager
        dataStorageManager.send(SendWordFrequencies(this))
    }

    private fun fill(message: Result) {
        result = message.frequencies.take(25).toMap()
        dataStorageManager.send(Die)
        send(Die)
    }

    fun getResult(): Map<String, Int> {
        return result
    }
}

open class Message {
    object Die : Message()
}