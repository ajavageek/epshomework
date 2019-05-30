package ch.frankel.blog.eps

fun run(filename: String): Map<String, Int> {
    val allCells: Cell<List<Any>, (() -> List<Any>)?> = Cell(listOf<String>(), null)
    val stopWords: Cell<List<Any>, (() -> List<Any>)?> = Cell(listOf<String>(), null)
    val nonStopWords: Cell<List<Any>, () -> List<Any>> = Cell(listOf<String>()) {
        allCells.value.filter { !stopWords.value.contains(it) }
    }
    val uniqueWords: Cell<List<Any>, () -> List<Any>> = Cell(listOf<String>()) {
        nonStopWords.value.distinct()
    }
    val counts: Cell<List<Any>, () -> List<Any>> = Cell(listOf<Int>()) {
        uniqueWords.value.map { unique ->
            allCells.value.count { it == unique }
        }
    }
    val sortedData: Cell<List<Any>, () -> List<Any>> = Cell(listOf<Pair<String, Int>>()) {
        uniqueWords.value.zip(counts.value).sortedByDescending { it.second as Int }
    }

    val spreadsheet: Spreadsheet =
        listOf(allCells, stopWords, nonStopWords, uniqueWords, counts, sortedData)

    allCells.value = read(filename)
        .flatMap { it.toLowerCase().split("\\W|_".toRegex()) }
        .filter { it.isNotBlank() && it.length >= 2 }

    stopWords.value = read("stop_words.txt")
        .flatMap { it.split(",") }

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