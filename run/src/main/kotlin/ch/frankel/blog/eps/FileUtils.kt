package ch.frankel.blog.eps

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

internal fun read(filename: String): List<String> {
    val path = path(filename)
    return if (Files.exists(path))
        Files.readAllLines(path)
    else arrayListOf()
}

private fun path(filename: String): Path {
    val root = Any::class.java.getResource("/")
    val parent = Paths.get(root.toURI())
    return Paths.get(parent.toAbsolutePath().toString(), filename)
}