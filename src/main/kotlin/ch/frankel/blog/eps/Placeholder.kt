package ch.frankel.blog.eps

fun run(filename: String): Map<String, Int> {
    val stopWordManager = StopWordManager()
    val dataStorageManager = DataStorageManager()
    val wordFrequencyController = WordFrequencyController()
    dataStorageManager.send(DataStorageManager.Init(filename, stopWordManager))
    stopWordManager.send(StopWordManager.Init(WordFrequencyManager()))
    wordFrequencyController.send(WordFrequencyController.Run(dataStorageManager))
    while (wordFrequencyController.isRunning) {
        Thread.sleep(200)
    }
    return wordFrequencyController.result
}