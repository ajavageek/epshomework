package ch.frankel.blog.eps

import java.util.concurrent.Executors

fun run(filename: String): Map<String, Int> {
    val dataStorageManager = DataStorageManager()
    val stopWordManager = StopWordManager()
    val wordFrequencyManager = WordFrequencyManager()
    val wordFrequencyController = WordFrequencyController()
    dataStorageManager.send(DataStorageManager.Init(filename, stopWordManager))
    stopWordManager.send(StopWordManager.Init(wordFrequencyManager))
    wordFrequencyController.send(WordFrequencyController.Run(dataStorageManager))
    val executorService = Executors.newFixedThreadPool(4)
    listOf(dataStorageManager, stopWordManager, wordFrequencyManager, wordFrequencyController)
        .map { executorService.submit(it) }[3]
        .get()
    return wordFrequencyController.getResult()
}