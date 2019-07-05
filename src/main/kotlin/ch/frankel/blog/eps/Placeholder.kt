package ch.frankel.blog.eps

import ch.frankel.blog.eps.EventManager.*

/* For compiling purpose. */
fun run(filename: String): Map<String, Int> {
    EventManager().apply {
        DataStorage(this)
        StopWordsFilter(this)
        WordFrequencyCounter(this)
        val app = WordFrequencyApplication(this)
        publish(Event(Type.Run, filename))
        return app.result
    }
}