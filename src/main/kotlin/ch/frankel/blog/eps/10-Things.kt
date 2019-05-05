package ch.frankel.blog.eps

abstract class Exercise {
    open val info: Any = javaClass
}

class DataStorageManager(filename: String) : Exercise() {

    private val data = read(filename)
        .flatMap { it.split("\\W|_".toRegex()) }
        .filter { it.isNotBlank() && it.length >= 2 }
        .map(String::toLowerCase)

    override val info = "My major data structure is a ${data::class}"

    fun words() = data
}

class StopWordManager : Exercise() {

    private val data = read("stop_words.txt")[0].split(",")

    override val info = "My major data structure is a ${data::class}"

    fun isStopWord(word: String) = data.contains(word)
}

class WordFrequencyManager : Exercise() {

    private var data = mutableMapOf<String, Int>()

    override val info = "My major data structure is a ${data::class}"

    fun incrementCount(word: String) {
        data.merge(word, 1) { value, _ -> value + 1 }
    }

    fun top() = data.toList().sortedByDescending { it.second }.take(25).toMap()
}

class WordFrequencyController(filename: String) : Exercise() {

    private val storageManager = DataStorageManager(filename)
    private val stopWordManager = StopWordManager()
    private val wordFrequencyManager = WordFrequencyManager()

    fun run(): Map<String, Int> {
        for (word in storageManager.words()) {
            if (!stopWordManager.isStopWord(word))
                wordFrequencyManager.incrementCount(word)
        }
        return wordFrequencyManager.top()
    }
}