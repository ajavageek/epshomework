package ch.frankel.blog.eps

fun run(filename: String) = filename
    .pipe(::readFile)
    .pipe(::filterChars)
    .pipe(::normalize)
    .pipe(::removeStopWords)
    .pipe(::frequencies)
    .pipe(::sorted)
    .pipe(::top25)

fun <T, V> T.pipe(function: (T) -> V) = function(this)

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
    frequencies.take(25).toMap()