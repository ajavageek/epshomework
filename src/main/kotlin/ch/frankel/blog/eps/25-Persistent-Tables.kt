package ch.frankel.blog.eps

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun run(filename: String): Map<String, Int> {
    Database.connect("jdbc:h2:mem:test", "org.h2.Driver")
    return transaction {
        createDbSchema()
        loadFileIntoDatabase(filename)
        Words.slice(Words.value, Words.value.count()).selectAll()
            .groupBy(Words.value)
            .orderBy(Words.id.count(), SortOrder.DESC)
            .limit(25)
            .map {
                it[Words.value] to it[Words.value.count()]
            }.toMap()
    }
}

fun createDbSchema() {
    SchemaUtils.create(Documents, Words, Characters)
}

fun loadFileIntoDatabase(filename: String) {
    fun extractWords(filename: String) = read(filename)
        .flatMap { it.toLowerCase().split("\\W|_".toRegex()) }
        .filter { it.isNotBlank() && it.length >= 2 }
        .minus(read("stop_words.txt").flatMap { it.split(",") })

    val words = extractWords(filename)
    val docId = Documents.insert {
        it[name] = filename
    } get Documents.id
    words.forEach { word ->
        val wordId = Words.insert {
            it[this.docId] = docId
            it[this.value] = word
        } get Words.id
        word.forEach { char ->
            Characters.insert {
                it[this.wordId] = wordId
                it[this.value] = char.toString()
            }
        }
    }
}