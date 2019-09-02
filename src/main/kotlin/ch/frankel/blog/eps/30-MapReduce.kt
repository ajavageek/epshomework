package ch.frankel.blog.eps

fun splitWords(lines: Iterable<String>): Iterable<Pair<String, Int>> {
    fun Iterable<String>.scan() = this
        .flatMap { it.toLowerCase().split("\\W|_".toRegex()) }
        .filter { it.isNotBlank() && it.length >= 2 }
    fun Iterable<String>.removeStopWords(): Iterable<String> {
        val stopWords = read("stop_words.txt")
            .flatMap { it.split(",") }
        return this - stopWords
    }
    return lines.scan()
        .removeStopWords()
        .map { it to 1 }
}

fun countWords(frequencies1: Iterable<Pair<String, Int>>, frequencies2: Iterable<Pair<String, Int>>): Iterable<Pair<String, Int>> {
   val results = mutableMapOf<String, Int>()
    frequencies1.forEach {
        results.merge(it.first, it.second) {
            count, value -> count + value
        }
    }
    frequencies2.forEach {
        results.merge(it.first, it.second) {
                count, value -> count + value
        }
    }
    return results.toList()
}