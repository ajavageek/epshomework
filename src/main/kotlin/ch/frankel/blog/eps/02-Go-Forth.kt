package ch.frankel.blog.eps

import java.util.*

private val stack = Stack<Any>()
private val heap = mutableMapOf<Any, Any>()

fun run(filename: String): Map<*, *> {
    stack.push(filename)
    readFile()
    filterChars()
    scan()
    removeStopWords()
    frequencies()
    sort()
    heap["result"] = mutableMapOf<Any, Any>()
    while (stack.isNotEmpty()) {
        (heap["result"] as MutableMap<Any, Any>)[(stack.peek() as Pair<Any, *>).first] =
            (stack.pop() as Pair<*, Any>).second
    }
    return heap["result"] as Map<*, *>
}

fun readFile() {
    val f = read(stack.pop() as String)
    stack.push(f)
}

fun filterChars() {
    stack.push(
        (stack.pop() as List<*>)
            .map { "\\W|_".toRegex().replace(it as String, " ") }
            .map(String::toLowerCase))
}

fun scan() {
    stack.extend((stack.pop() as List<*>)
        .flatMap { (it as String).split("\\s".toRegex()) })
}

fun removeStopWords() {
    val f = read("stop_words.txt")
        .flatMap { it.split(",") }
    stack.push(f)
    heap["stop_words"] = stack.pop()
    heap["words"] = mutableListOf<String>()
    while (stack.isNotEmpty()) {
        if ((heap["stop_words"] as List<*>).contains(stack.peek()) || (stack.peek() as String).length < 2) stack.pop()
        else (heap["words"] as MutableList<Any>).add(stack.pop())
    }
    stack.extend(heap["words"] as List<Any>)
    heap.remove("stop_words")
    heap.remove("words")
}

fun frequencies() {
    heap["word_freqs"] = mutableMapOf<String, Int>()
    while (stack.isNotEmpty()) {
        if ((heap["word_freqs"] as Map<*, *>).containsKey(stack.peek())) {
            stack.push((heap["word_freqs"] as Map<*, *>)[stack.peek()])
            stack.push(1)
            stack.push(stack.pop() as Int + stack.pop() as Int)
        } else stack.push(1)
        stack.swap()
        (heap["word_freqs"] as MutableMap<Any, Any>)[stack.pop()] = stack.pop()
    }
    stack.push(heap["word_freqs"])
    heap.remove("word_freqs")
}

fun sort() {
    stack.extend((stack.pop() as Map<*, *>).entries
        .map { it.key to it.value }
        .sortedBy { it.second as Int }
        .takeLast(25))
}