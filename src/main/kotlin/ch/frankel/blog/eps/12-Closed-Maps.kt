package ch.frankel.blog.eps

internal fun extractWords(obj: MutableMap<String, Any>, filename: String) {
    obj["data"] = read(filename)
        .flatMap { it.split("\\W|_".toRegex()) }
        .filter { it.isNotBlank() && it.length >= 2 }
        .map(String::toLowerCase)
}

internal fun loadStopWords(obj: MutableMap<String, Any>) {
    obj["stop_words"] = read("stop_words.txt")[0].split(",")
}

internal fun incrementCount(obj: MutableMap<String, Any>, word: String) {
    (obj["freqs"] as MutableMap<String, Int>).apply {
        merge(word, 1) { value, _ -> value + 1 }
    }
}

fun run(filename: String): Map<*, *> {
    val dataStorageObj = mutableMapOf<String, Any>()
    dataStorageObj["init"] = { extractWords(dataStorageObj, filename) }
    dataStorageObj["words"] = { dataStorageObj["data"] }
    (dataStorageObj["init"] as () -> Unit)()

    val stopWordsObj = mutableMapOf<String, Any>()
    stopWordsObj["init"] = { loadStopWords(stopWordsObj) }
    stopWordsObj["is_stop_word"] = { it: String -> (stopWordsObj["stop_words"] as List<*>).contains(it) }
    (stopWordsObj["init"] as () -> Unit)()

    val wordFreqsObj = mutableMapOf<String, Any>(
        "freqs" to mutableMapOf<String, Int>()
    )
    wordFreqsObj["increment_count"] = { it: String -> incrementCount(wordFreqsObj, it) }
    wordFreqsObj["sorted"] = {
        (wordFreqsObj["freqs"] as MutableMap<String, Int>)
            .toList()
            .sortedByDescending { it.second }
            .take(25)
            .toMap()
    }

    for (word in (dataStorageObj["words"] as () -> List<String>)()) {
        if (!(stopWordsObj["is_stop_word"] as (String) -> Boolean)(word))
            (wordFreqsObj["increment_count"] as (String) -> Unit)(word)
    }
    return (wordFreqsObj["sorted"] as () -> Map<*, *>)()
}