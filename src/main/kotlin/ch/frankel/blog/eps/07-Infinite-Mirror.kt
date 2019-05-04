package ch.frankel.blog.eps

fun run(filename: String): Map<String, Int> {
    val stopwords = stopwords(read("stop_words.txt")[0], "", listOf())
    val words = words(read(filename), stopwords, listOf())
    return count(words, mutableMapOf())
        .sortedBy { it.second }
        .takeLast(25)
        .toMap()
}

tailrec fun stopwords(rest: String, word: String, stopwords: List<String>): List<String> = when {
    rest.isEmpty() -> stopwords + word
    rest.startsWith(",") -> stopwords(rest.substring(1), "", stopwords + word)
    else -> stopwords(rest.substring(1), word + rest[0], stopwords)
}

tailrec fun words(rest: List<String>, stopwords: List<String>, words: List<String>): List<String> {
    return if (rest.isEmpty()) words
    else {
        val line = rest.last()
        val split = line
            .split("\\W|_".toRegex())
            .map(String::toLowerCase)
            .filter { it.length > 1 && !stopwords.contains(it) }
        words(rest.dropLast(1), stopwords, words + split)
    }
}

tailrec fun count(
    words: List<String>,
    wordFrequencies: MutableMap<String, Int>
): List<Pair<String, Int>> {
    return if (words.isEmpty()) wordFrequencies.toList()
    else {
        val word = words.last()
        wordFrequencies.merge(word, 1) { value, _ -> value + 1 }
        count(words.dropLast(1), wordFrequencies)
    }
}