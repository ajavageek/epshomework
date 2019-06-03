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
    listOf(dataStorageManager, stopWordManager, wordFrequencyManager)
        .forEach { executorService.submit(it) }
    return executorService.submit(wordFrequencyController).get()
}