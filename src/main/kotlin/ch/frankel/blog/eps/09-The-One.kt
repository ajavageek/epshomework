package ch.frankel.blog.eps

class TheOne(private var value: Any) {

    val map: Map<String, Int>
        get() = (value as List<Pair<String, Int>>).toMap()

    fun <T, V : Any> bind(function: (T) -> V) = apply {
        value = function(value as T)
    }
}

fun run(filename: String) = TheOne(filename)
    .bind(::readFile)
    .bind(::filterChars)
    .bind(::normalize)
    .bind(::removeStopWords)
    .bind(::frequencies)
    .bind(::sorted)
    .bind(::top25)
    .map

fun readFile(filename: String) = read(filename)

fun filterChars(lines: List<String>) = lines
    .flatMap { it.split("\\W|_".toRegex()) }
    .filter { it.isNotBlank() && it.length >= 2 }

fun normalize(lines: List<String>) = lines.map(String::toLowerCase)

fun removeStopWords(words: List<String>): List<String> {
    val stopWords = read("stop_words.txt")
        .flatMap { it.split(",") }
    return words - stopWords
}

fun frequencies(words: List<String>) = words.groupBy { it }
    .map { it.key to it.value.size }

fun sorted(frequencies: List<Pair<String, Int>>) =
    frequencies.sortedByDescending { it.second }

fun top25(frequencies: List<Pair<String, Int>>) =
    frequencies.take(25)