package ch.frankel.blog.eps

class WordFrequencyFramework {

    private val loadEventHandlers = mutableListOf<(String) -> Unit>()
    private val doWorkEventHandlers = mutableListOf<() -> Unit>()
    private val endEventHandlers = mutableListOf<() -> Map<String, Int>>()

    fun run(filename: String): Map<String, Int> {
        for (handler in loadEventHandlers) {
            handler(filename)
        }
        for (handler in doWorkEventHandlers) {
            handler()
        }
        return endEventHandlers.iterator()
            .next()
            .invoke()
    }

    fun registerForLoadEvents(handler: (String) -> Unit) = loadEventHandlers.add(handler)
    fun registerForDoWorkEvents(handler: () -> Unit) = doWorkEventHandlers.add(handler)
    fun registerForEndEvents(handler: () -> Map<String, Int>) = endEventHandlers.add(handler)
}

class DataStorage(
    wfApp: WordFrequencyFramework,
    private val stopWordsFilter: StopWordsFilter
) {
    private lateinit var data: List<String>
    private val wordEventHandlers = mutableListOf<(String) -> Unit>()

    init {
        wfApp.registerForLoadEvents { load(it) }
        wfApp.registerForDoWorkEvents { produceWords() }
    }

    fun registerForWordEvents(handler: (String) -> Unit)  = wordEventHandlers.add(handler)

    private fun load(filename: String) {
        data = read(filename)
            .flatMap { it.split("\\W|_".toRegex()) }
            .filter { it.isNotBlank() && it.length >= 2 }
            .map(String::toLowerCase)
    }

    private fun produceWords() {
        for (word in data) {
            if (!(stopWordsFilter.isStopWord(word))) {
                for (handler in wordEventHandlers) {
                    handler(word)
                }
            }
        }
    }
}

class StopWordsFilter(wfApp: WordFrequencyFramework) {

    private lateinit var stopWords: List<String>

    init {
        wfApp.registerForLoadEvents { load(it) }
    }

    private fun load(ignore: String) {
        stopWords = read("stop_words.txt")[0].split(",")
    }

    fun isStopWord(word: String) = stopWords.contains(word)
}

class WordFrequencyCounter(
    wfApp: WordFrequencyFramework,
    dataStorage: DataStorage
) {
    private val wordFreqs = mutableMapOf<String, Int>()

    init {
        wfApp.registerForEndEvents { getTop25() }
        dataStorage.registerForWordEvents { it: String -> incrementCount(it) }
    }

    private fun incrementCount(word: String) = wordFreqs.merge(word, 1) { value, _ -> value + 1 }
    private fun getTop25() = wordFreqs.toList().sortedByDescending { it.second }.take(25).toMap()
}