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
    stack.push(mutableMapOf<Any, Any>())
    while (stack.size > 1) {
        stack.swap()
        stack.push((stack.pop() as Pair<Any, Any>).putIn(stack.pop() as MutableMap<Any, Any>))
    }
    return stack.pop() as Map<*, *>
}

fun readFile() {
    val f = read(stack.pop() as String)
    stack.extend(f)
}

fun filterChars() {
    stack.push(mutableListOf<String>())
    while (stack.size > 1) {
        stack.swap()
        stack.push(
            "\\W|_".toRegex()
                .replace((stack.pop() as String), " ")
                .toLowerCase()
                .addTo(stack.pop() as MutableList<Any>)
        )
    }
    stack.extend(stack.pop() as MutableList<Any>)
}

fun scan() {
    stack.push(mutableListOf<String>())
    while (stack.size > 1) {
        stack.swap()
        stack.push(
            (stack.pop() as String)
                .split("\\s".toRegex())
                .allAddTo(stack.pop() as MutableList<Any>)
        )
    }
    stack.extend(stack.pop() as MutableList<Any>)
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
    while ((stack.peek() as Map<*, *>).isNotEmpty()) {
        stack.push((stack.peek() as MutableMap<*, *>).removeAny())
        stack.swap()
    }
    stack.pop()
    stack.push(mutableListOf<Any>())
    while (stack.size > 1) {
        stack.swap()
        stack.pop().addTo(stack.peek() as MutableList<Any>)
    }
    (stack.peek() as MutableList<Pair<Any, Int>>).sortBy { -it.second }
    stack.extend(stack.pop() as List<Any>)
    while (stack.size > 25) {
        stack.pop()
    }
}