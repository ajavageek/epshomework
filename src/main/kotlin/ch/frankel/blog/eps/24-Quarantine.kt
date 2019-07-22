package ch.frankel.blog.eps

class Quarantine(private val filename: String, extractWords: (String) -> List<String>, extractStopWords: () -> List<String>) {

    private var functions = listOf<(Any) -> Any>()

    init {
        bind({ it: String ->
            extractWords.invoke(it) to extractStopWords.invoke()
        } as (Any) -> Any)
    }

    fun bind(function: Function1<*, Any>): Quarantine {
        functions = functions + (function as (Any) -> Any)
        return this
    }

    fun execute(): Any {
        tailrec fun internalExecute(functions: List<Function1<*, Any>>, result: Any): Any =
            if (functions.isEmpty()) result
            else {
                val function = functions.first()
                internalExecute(functions - function, (function as (Any) -> Any)(result))
            }
        return internalExecute(functions, filename)
    }
}

fun extractWords(): (String) -> List<String> = {
    read(it)
        .flatMap { it.toLowerCase().split("\\W|_".toRegex()) }
        .filter { it.isNotBlank() && it.length >= 2 }
}

fun extractStopWords(): () -> List<String> = { read("stop_words.txt") }

fun removeStopWords(): (Pair<List<String>, List<String>>) -> List<String> = { pair ->
    pair.first - (pair.second.flatMap { it.split(",") })
}

fun frequencies(): (List<String>) -> Map<String, Int> = { words ->
    words.groupingBy { it }
        .eachCount()
}

fun sorted(): (Map<String, Int>) -> List<Pair<String, Int>> = { frequencies ->
    frequencies.map { it.key to it.value }
        .sortedByDescending { it.second }
}

fun top(): (List<Pair<String, Int>>) -> Map<String, Int> = {
    it.take(25).toMap()
}

fun run(filename: String) = Quarantine(filename, extractWords(), extractStopWords())
    .bind(removeStopWords())
    .bind(frequencies())
    .bind(sorted())
    .bind(top())
    .execute() as Map<String, Int>