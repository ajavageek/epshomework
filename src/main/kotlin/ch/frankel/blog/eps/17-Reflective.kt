package ch.frankel.blog.eps

fun stopWords() = read("stop_words.txt")
    .flatMap { it.split(",") }

fun extractWords(filename: String) = read(filename)
    .flatMap { it.toLowerCase().split("\\W|_".toRegex()) }
    .filter { it.isNotBlank() && it.length >= 2 }

fun frequencies(words: List<String>) = words.groupBy { it }
    .map { it.key to it.value.size }
    .sortedBy { it.second }
    .takeLast(25)
    .toMap()

fun run(filename: String): Map<String, Int> {
    val funcStopWords = ::stopWords
    val funcExtractWords = ::extractWords
    val funcFrequencies = ::frequencies
    val words = funcExtractWords.invoke(filename) - funcStopWords.invoke()
    return funcFrequencies.invoke(words)
}

