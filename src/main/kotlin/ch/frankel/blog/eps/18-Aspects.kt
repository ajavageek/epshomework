package ch.frankel.blog.eps

fun run(filename: String): Map<String, Int> {
    val (time1, words) = executeAndMeasureTimeMillis { extractWords(filename) }
    val (time2, frequencies) = executeAndMeasureTimeMillis { frequencies(words) }
    val (time3, sort) = executeAndMeasureTimeMillis {
        sort(frequencies)
            .take(25)
            .toMap()
    }
    println("Call to extractWords took $time1")
    println("Call to frequencies took $time2")
    println("Call to sort took $time3")
    return sort
}

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

private fun <T> executeAndMeasureTimeMillis(function: () -> T): Pair<Long, T> {
    val start = System.currentTimeMillis()
    val result = function()
    return (System.currentTimeMillis() - start) to result
}