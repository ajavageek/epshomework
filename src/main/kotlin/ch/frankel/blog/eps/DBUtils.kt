package ch.frankel.blog.eps

import org.jetbrains.exposed.sql.Table

object Documents : Table() {
    val id = long("id").primaryKey().autoIncrement()
    val name = varchar("name", length = 50)
}

object Words : Table() {
    val id = long("id").primaryKey().autoIncrement()
    val docId = long("docId")
    val value = varchar("value", 50)
}

object Characters : Table() {
    val id = long("id").primaryKey().autoIncrement()
    val wordId = long("wordId")
    val value = varchar("value", 1)
}