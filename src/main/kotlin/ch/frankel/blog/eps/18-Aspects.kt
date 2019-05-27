package ch.frankel.blog.eps

fun run(filename: String) = sort(frequencies(extractWords(filename)))
    .take(25)
    .toMap()

fun extractWords(filename: String) = read(filename)
    .flatMap { it.toLowerCase().split("\\W|_".toRegex()) }
    .filter { it.isNotBlank() && it.length >= 2 }
    .minus(read("stop_words.txt").flatMap { it.split(",") })

fun frequencies(words: List<String>) = words
    .groupingBy { it }
    .eachCount()

fun sort(frequencies: Map<String, Int>) = frequencies
    .map { it.key to it.value }
    .sortedByDescending { it.second }