package ch.frankel.blog.eps

fun run(filename: String): Map<String, Int> {
    val allWords: Cell<List<Any>, () -> List<Any>> = Cell(listOf<String>()) {
        read(filename)
            .flatMap { it.toLowerCase().split("\\W|_".toRegex()) }
            .filter { it.isNotBlank() && it.length >= 2 }
    }
    val stopWords: Cell<List<Any>, () -> List<Any>> = Cell(listOf<String>()) {
        read("stop_words.txt")
            .flatMap { it.split(",") }
    }
    val nonStopWords: Cell<List<Any>, () -> List<Any>> = Cell(listOf<String>()) {
        allWords.value.filter { !stopWords.value.contains(it) }
    }
    val uniqueWords: Cell<List<Any>, () -> List<Any>> = Cell(listOf<String>()) {
        nonStopWords.value.distinct()
    }
    val counts: Cell<List<Any>, () -> List<Any>> = Cell(listOf<Int>()) {
        uniqueWords.value.map { unique ->
            allWords.value.count { it == unique }
        }
    }
    val sortedData: Cell<List<Any>, () -> List<Any>> = Cell(listOf<Pair<String, Int>>()) {
        uniqueWords.value.zip(counts.value).sortedByDescending { it.second as Int }
    }

    val spreadsheet: Spreadsheet =
        listOf(allWords, stopWords, nonStopWords, uniqueWords, counts, sortedData)

    update(spreadsheet)

    return (sortedData.value.take(25) as List<Pair<String, Int>>).toMap()
}

private fun update(cells: Spreadsheet) {
    cells.forEach {
        it.formula?.let { f ->
            it.value = f()
        }
    }
}

typealias Spreadsheet = List<Cell<List<Any>, out (() -> List<Any>)?>>
data class Cell<T, V>(var value: T, var formula: V)