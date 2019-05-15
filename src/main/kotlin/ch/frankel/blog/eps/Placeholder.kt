package ch.frankel.blog.eps

import com.google.common.eventbus.EventBus

/* For compiling purpose. */
fun run(filename: String): Map<String, Int> {
    EventBus().apply {
        DataStorage(this)
        StopWordsFilter(this)
        WordFrequencyCounter(this)
        val app = WordFrequencyApplication(this)
        post(RunEvent(filename))
        return app.result
    }
}