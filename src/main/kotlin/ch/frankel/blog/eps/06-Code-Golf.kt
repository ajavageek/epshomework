package ch.frankel.blog.eps

fun run(filename: String) = (read(filename)
    .flatMap { it.toLowerCase().split("\\W|_".toRegex()) }
    .filter { it.isNotBlank() && it.length >= 2 }
        - read("stop_words.txt").flatMap { it.split(",") })
    .fold(mutableMapOf<String, Int>()) { map, word ->
        map.merge(word, 1) { existing, new -> existing + new }
        map
    }.toList()
    .sortedByDescending { it.second }
    .take(25)
    .toMap()