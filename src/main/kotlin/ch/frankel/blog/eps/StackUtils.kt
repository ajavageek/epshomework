package ch.frankel.blog.eps

import java.util.*


internal fun <T> Stack<T>.extend(iterable: Iterable<T>) = iterable.forEach { push(it) }

internal fun Stack<Any>.swap() {
    val a = pop()
    val b = pop()
    push(a)
    push(b)
}
