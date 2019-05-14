package ch.frankel.blog.eps

/* For compiling purpose. */
fun run(filename: String): Map<String, Int> {
    val wfApp = WordFrequencyFramework()
    WordFrequencyCounter(
        wfApp,
        DataStorage(
            wfApp,
            StopWordsFilter(wfApp)
        )
    )
    return wfApp.run(filename)
}