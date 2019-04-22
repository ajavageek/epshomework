package ch.frankel.blog.eps

import loggersoft.kotlin.streams.openBinaryStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.math.floor
import kotlin.math.log10

internal fun read(filename: String): List<String> {
    val path = path(filename)
    return if (Files.exists(path))
        Files.readAllLines(path)
    else arrayListOf()
}

internal fun updateCount(filename: String, word: String) {
    val path = path(filename)
    if (Files.notExists(path)) {
        Files.write(
            path,
            ByteArray(0),
            StandardOpenOption.WRITE,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        )
    }
    path.toFile().openBinaryStream(false).use {
        it.position = it.size
        it.writeLine("$word,1")
    }
}

internal fun updateCount(filename: String, word: String, count: Int) {
    val path = path(filename)
    path.toFile().openBinaryStream(false).use {
        var line = "€"
        while (line.isNotEmpty()) {
            line = it.readLine()
            if (line.matches("$word,\\d*".toRegex()))
                break
        }
        if (hasMoreCharacters(count)) {
            val position = it.position
            val rest = it.readString()
            it.position = position - (line.length + 1)
            it.writeLine("$word,$count")
            it.writeLine(rest.dropLast(1))
        } else {
            it.position = it.position - (line.length + 1)
            it.writeLine("$word,$count")
        }
    }
}

internal fun Char.isAlphaNumeric() =
    Character.isDigit(this) ||
            Character.isLetter(this)

private fun hasMoreCharacters(count: Int): Boolean {
    val log10 = log10(count.toDouble())
    return floor(log10) == log10
}

private fun path(filename: String): Path {
    val root = Any::class.java.getResource("/")
    val parent = Paths.get(root.toURI())
    return Paths.get(parent.toAbsolutePath().toString(), filename)
}