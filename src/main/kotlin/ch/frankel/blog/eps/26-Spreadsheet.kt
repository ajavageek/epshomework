package ch.frankel.blog.eps

fun run(filename: String): Map<String, Int> {
    val allWords: Pair<List<Any>, () -> List<Any>> = Pair(listOf<String>()) {
        read(filename)
            .flatMap { it.toLowerCase().split("\\W|_".toRegex()) }
            .filter { it.isNotBlank() && it.length >= 2 }
    }
    val nonStopWords: Pair<List<Any>, (List<String>) -> List<Any>> = Pair(listOf()) { words: List<String> ->
        val stopWords = read("stop_words.txt")
            .flatMap { it.split(",") }
        words.filter { !stopWords.contains(it) }
    }
    val counts: Pair<List<Any>, (List<String>) -> List<Pair<String, Int>>> = Pair(listOf()) { words ->
        words.groupingBy { it }.eachCount().toList()
    }
    val sortedData: Pair<List<Any>, (List<Pair<String, Int>>) -> List<Any>> = Pair(listOf()) { frequencies ->
        frequencies.sortedByDescending { it.second }
    }

    val spreadsheet: Spreadsheet =
        listOf(allWords, nonStopWords, counts, sortedData)

    return (update(spreadsheet)
        .last()
        .first
        .take(25)  as List<Pair<String, Int>>)
        .toMap()
}

private fun update(cells: Spreadsheet): Spreadsheet {
    tailrec fun recurseUpdate(todo: Spreadsheet, acc: Spreadsheet): Spreadsheet {
        return if (todo.isEmpty()) acc
        else {
            val column = todo.first()
            val f = column.second
            if (f is () -> List<Any>)
                recurseUpdate(todo.takeLast(todo.size - 1), acc + (f() to f))
            else {
                val g = f as (List<Any>) -> List<Any>
                recurseUpdate(todo.takeLast(todo.size - 1), acc + (g(acc.last().first) to f))
            }
        }
    }
    return recurseUpdate(cells, arrayListOf())
}

typealias Spreadsheet = List<Pair<List<Any>, Function<List<Any>>>>