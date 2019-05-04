package ch.frankel.blog.eps

fun run(filename: String): Map<String, Int> {
    val stopwords = stopwords(read("stop_words.txt")[0], "", listOf())
    val words = words(read(filename), stopwords, listOf())
    val wordFrequencies = count(words, mutableMapOf()).toMutableList()
    val sorted = quicksort(wordFrequencies)
    val top = top(sorted, listOf())
    return top.toMap()
}

tailrec fun stopwords(rest: String, word: String, stopwords: List<String>): List<String> = when {
    rest.isEmpty() -> stopwords + word
    rest.startsWith(",") -> stopwords(rest.substring(1), "", stopwords + word)
    else -> stopwords(rest.substring(1), word + rest[0], stopwords)
}

tailrec fun words(rest: List<String>, stopwords: List<String>, words: List<String>): List<String> {
    return if (rest.isEmpty()) words
    else {
        val split = split(rest.last(), stopwords, listOf())
        words(rest.dropLast(1), stopwords, words + split)
    }
}

tailrec fun split(rest: String, stopwords: List<String>, words: List<String>): List<String> {
    val word = rest.takeWhile { it.isLetter() }.toLowerCase()
    return when {
        rest.isEmpty() -> words
        word.isEmpty() -> split(rest.substring(1), stopwords, words)
        !stopwords.contains(word) && word.length > 1 ->
            split(rest.substring(word.length), stopwords, words + word)
        else ->
            split(rest.substring(word.length), stopwords, words)
    }
}

tailrec fun count(
    words: List<String>,
    wordFrequencies: MutableMap<String, Int>
): List<Pair<String, Int>> {
    return if (words.isEmpty()) wordFrequencies.toList()
    else {
        val word = words.last()
        wordFrequencies.merge(word, 1) { value, _ -> value + 1 }
        count(words.dropLast(1), wordFrequencies)
    }
}

tailrec fun <T> top(words: List<T>, top: List<T>): List<T> {
    return if (top.size == 25) top
    else if (top.isEmpty() && words.size <= 25) words
    else top(words - words.last(), top + words.last())
}

fun <T> quicksort(list: List<Pair<T, Int>>): List<Pair<T, Int>> =
    if (list.size <= 1) list
    else list.random().let { pivot ->
        val below = filter(list, listOf()) { it.second <= pivot.second }
        val above = filter(list, listOf()) { it.second > pivot.second }
        quicksort(below - pivot) + pivot + quicksort(above)
    }

tailrec fun <T> filter(list: List<T>, acc: List<T>, predicate: (T) -> Boolean): List<T> {
    return if (list.isEmpty()) acc
    else {
        val last = list.last()
        if (predicate(last)) filter(list - last, acc + last, predicate)
        else filter(list - last, acc, predicate)
    }
}