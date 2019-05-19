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
    val clazz = Class.forName("ch.frankel.blog.eps._17_ReflectiveKt")
    val funcStopWords = clazz.getMethod("stopWords")
    val funcExtractWords = clazz.getMethod("extractWords", String::class.java)
    val funcFrequencies = clazz.getMethod("frequencies", List::class.java)
    val words = funcExtractWords.invoke(null, filename) as List<String>
    val stopWords = funcStopWords.invoke(null) as List<String>
    return funcFrequencies.invoke(null, words - stopWords) as Map<String, Int>
}