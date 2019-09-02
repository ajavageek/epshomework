package ch.frankel.blog.eps

import kotlin.collections.*

/* For compiling purpose. */
fun run(filename: String): Map<String, Int>  = read(filename)
        .asSequence()
        .chunked(200)
        .map(::splitWords)
        .reduce { acc, pair -> countWords(acc, pair) }
        .sortedBy { it.second }
        .takeLast(25)
        .toMap()