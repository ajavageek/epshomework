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
    val nonStopWords: Cell<List<Any>, (List<String>) -> List<Any>> = Cell(listOf()) { words: List<String> ->
        words.filter { !stopWords.value.contains(it) }
    }
    val counts: Cell<List<Any>, (List<String>) -> List<Pair<String, Int>>> = Cell(listOf()) { words ->
        words.groupingBy { it }.eachCount().toList()
    }
    val sortedData: Cell<List<Any>, (List<Pair<String, Int>>) -> List<Any>> = Cell(listOf()) { frequencies ->
        frequencies.sortedByDescending { it.second }
    }

    val spreadsheet: Spreadsheet =
        listOf(stopWords, allWords, nonStopWords, counts, sortedData)

    update(spreadsheet)

    return (sortedData.value.take(25) as List<Pair<String, Int>>).toMap()
}

private fun update(cells: Spreadsheet) {
    cells.forEachIndexed { i, cell ->
        val f = cell.formula
        if (f is () -> List<Any>) cell.value = f()
        else {
            val g = f as (List<Any>) -> List<Any>
            cell.value = g(cells[i - 1].value)
        }
    }
}

typealias Spreadsheet = List<Cell<List<Any>, out Function<List<Any>>>>
data class Cell<T, V>(var value: T, var formula: V)