package ch.frankel.blog.eps

fun run(filename: String): Map<String, Int> {
    val dataStorageManager = DataStorageManager()
    val stopWordManager = StopWordManager()
    val wordFrequencyManager = WordFrequencyManager()
    val wordFrequencyController = WordFrequencyController()
    dataStorageManager.send(DataStorageManager.Init(filename, stopWordManager))
    stopWordManager.send(StopWordManager.Init(wordFrequencyManager))
    wordFrequencyController.send(WordFrequencyController.Run(dataStorageManager))
    listOf(dataStorageManager, stopWordManager, wordFrequencyManager)
        .forEach {
            createThread(it) { start() }
        }
    createThread(wordFrequencyController) {
        start()
        join()
    }
    return wordFrequencyController.getResult()
}

private fun <T: Actor> createThread(actor: T, f: Thread.() -> Unit = {}) =
    Thread(actor, actor::class.simpleName).apply { f(this) }
