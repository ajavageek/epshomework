package ch.frankel.blog.eps

fun run(filename: String) = (read(filename)
    .flatMap { it.toLowerCase().split("\\W|_".toRegex()) }
    .filter { it.isNotBlank() && it.length >= 2 }
        - read("stop_words.txt").flatMap { it.split(",") })
    .groupBy { it }
    .map { it.key to it.value.size }
    .sortedBy { it.second }
    .takeLast(25)
    .toMap()