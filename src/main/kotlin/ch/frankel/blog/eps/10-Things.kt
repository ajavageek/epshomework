package ch.frankel.blog.eps

abstract class Exercise {
    open val info: Any = javaClass
}

class DataStorageManager(private val filename: String) : Exercise() {

    fun words() = read(filename)
        .flatMap { it.split("\\W|_".toRegex()) }
        .filter { it.isNotBlank() && it.length >= 2 }
        .map(String::toLowerCase)
}

class StopWordManager : Exercise() {

    private val data = read("stop_words.txt")[0].split(",")

    override val info = "My major data structure is a ${data::class}"

    fun isStopWord(word: String) = data.contains(word)
}

class WordFrequencyManager(
    private val storageManager: DataStorageManager,
    private val stopWordManager: StopWordManager
) : Exercise() {

    fun top(): Map<String, Int> {
        val data = mutableMapOf<String, Int>()
        for (word in storageManager.words()) {
            if (!stopWordManager.isStopWord(word))
                data.merge(word, 1) { value, _ -> value + 1 }
        }
        return data.toList().sortedByDescending { it.second }.take(25).toMap()
    }
}

class WordFrequencyController(private val filename: String) : Exercise() {

    fun run() = WordFrequencyManager(
        DataStorageManager(filename),
        StopWordManager()
    ).top()
}