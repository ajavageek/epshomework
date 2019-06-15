package ch.frankel.blog.eps

private const val DATA = "data"
private const val STOP_WORDS = "stop_words"
private const val FREQUENCIES = "freqs"
private const val INIT = "init"
private const val WORDS = "words"
private const val IS_STOP_WORD = "is_stop_word"
private const val INCREMENT_COUNT = "increment_count"
private const val SORTED = "sorted"

typealias Callable<T, R> = () -> Map<T, R>
typealias Supplier<T> = () -> T
typealias Function<T, R> = (T) -> R
typealias Consumer<T> = (T) -> Unit

internal fun Map<String, Any>.extractWords(filename: String) = this +
        (DATA to read(filename)
        .flatMap { it.split("\\W|_".toRegex()) }
        .filter { it.isNotBlank() && it.length >= 2 }
        .map(String::toLowerCase))

internal fun Map<String, Any>.loadStopWords() = this +
        (STOP_WORDS to read("stop_words.txt")[0].split(","))

internal fun MutableMap<String, Any>.incrementCount(word: String) {
    (this[FREQUENCIES] as MutableMap<String, Int>).apply {
        merge(word, 1) { value, _ -> value + 1 }
    }
}

fun run(filename: String): Map<*, *> {
    val dataStorageObj = mapOf<String, Any>()
        .run { this + (INIT to { extractWords(filename) }) }
        .run { (this[INIT] as Callable<String, Any>)() }
        .run { this + (WORDS to { this[DATA] })}

    val stopWordsObj = mapOf<String, Any>()
        .run { this + (INIT to { loadStopWords() })}
        .run { (this[INIT] as Callable<String, Any>)() }
        .run { this + (IS_STOP_WORD to { it: String -> (this[STOP_WORDS] as List<*>).contains(it) }) }

    val wordFreqsObj = mutableMapOf<String, Any>(
        FREQUENCIES to mutableMapOf<String, Int>()
    )
    wordFreqsObj[INCREMENT_COUNT] = { it: String -> wordFreqsObj.incrementCount(it) }
    wordFreqsObj[SORTED] = {
        (wordFreqsObj[FREQUENCIES] as MutableMap<*, Int>)
            .toList()
            .sortedByDescending { it.second }
            .take(25)
            .toMap()
    }

    for (word in (dataStorageObj[WORDS] as Supplier<List<String>>)()) {
        if (!(stopWordsObj[IS_STOP_WORD] as Function<String, Boolean>)(word))
            (wordFreqsObj[INCREMENT_COUNT] as Consumer<String>)(word)
    }
    return (wordFreqsObj[SORTED] as Supplier<Map<*, *>>)()
}