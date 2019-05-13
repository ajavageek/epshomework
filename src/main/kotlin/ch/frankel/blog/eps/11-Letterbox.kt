package ch.frankel.blog.eps

interface Message

abstract class PayloadMessage<T> : Message {

    abstract val payload: T
}

interface MessageDispatch {
    fun dispatch(message: Message): Any?
}

class DataStorageManager : MessageDispatch {

    class WordsMessage : Message
    class InitMessage(override val payload: String) : PayloadMessage<String>()

    private lateinit var filename: String

    private val words: List<String> by lazy {
        read(filename)
            .flatMap { it.split("\\W|_".toRegex()) }
            .filter { it.isNotBlank() && it.length >= 2 }
            .map(String::toLowerCase)
    }

    override fun dispatch(message: Message) = when (message) {
        is InitMessage -> filename = message.payload
        is WordsMessage -> words
        else -> throw Exception("Uknown message $message")
    }
}

class StopWordManager : MessageDispatch {

    class InitMessage : Message
    class IsStopWord(override val payload: String) : PayloadMessage<String>()

    private lateinit var data: List<String>

    private fun isStopWord(word: String) = data.contains(word)

    override fun dispatch(message: Message) = when (message) {
        is InitMessage -> data = read("stop_words.txt")[0].split(",")
        is IsStopWord -> isStopWord(message.payload)
        else -> throw Exception("Uknown message $message")
    }
}

class WordFrequencyManager : MessageDispatch {

    class IncrementCount(override val payload: String) : PayloadMessage<String>()
    class Sort : Message

    private val data = mutableMapOf<String, Int>()

    override fun dispatch(message: Message) = when (message) {
        is IncrementCount -> data.merge(message.payload, 1) { value, _ -> value + 1 }
        is Sort -> data.toList().sortedByDescending { it.second }.take(25).toMap()
        else -> throw Exception("Uknown message $message")
    }
}

class WordFrequencyController : MessageDispatch {

    class InitMessage(override val payload: String) : PayloadMessage<String>()
    class RunMessage : Message

    private lateinit var wordFrequencyManager: WordFrequencyManager
    private lateinit var dataStorageManager: DataStorageManager
    private lateinit var stopWordManager: StopWordManager

    override fun dispatch(message: Message) = when (message) {
        is InitMessage -> {
            dataStorageManager =
                DataStorageManager().apply { dispatch(DataStorageManager.InitMessage(message.payload)) }
            stopWordManager = StopWordManager().apply { dispatch(StopWordManager.InitMessage()) }
            wordFrequencyManager = WordFrequencyManager()
        }
        is RunMessage -> {
            for (word in dataStorageManager.dispatch(DataStorageManager.WordsMessage()) as List<String>) {
                if (!(stopWordManager.dispatch(StopWordManager.IsStopWord(word)) as Boolean))
                    wordFrequencyManager.dispatch(WordFrequencyManager.IncrementCount(word))
            }
            wordFrequencyManager.dispatch(WordFrequencyManager.Sort())
        }
        else -> throw Exception("Uknown message $message")
    }
}