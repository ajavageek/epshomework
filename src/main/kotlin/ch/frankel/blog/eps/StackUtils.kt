package ch.frankel.blog.eps

import java.util.*


internal fun <T> Stack<T>.extend(iterable: Iterable<T>) = iterable.forEach { push(it) }

internal fun Stack<Any>.swap() {
    val a = pop()
    val b = pop()
    push(a)
    push(b)
}

internal fun <T> T.addTo(collection: MutableCollection<T>) = collection.also { it.add(this) }

internal fun <T> Collection<T>.allAddTo(collection: MutableCollection<T>)= collection.also { it.addAll(this) }

internal fun <K, V> MutableMap<K, V>.removeAny() = iterator().next().toPair().also { remove(it.first) }

internal fun <K, V> Pair<K, V>.putIn(map: MutableMap<K, V>) = map.also { map[first] = second }