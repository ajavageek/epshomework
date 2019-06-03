package ch.frankel.blog.eps

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

fun run(filename: String): Map<String, Int> {
    val dataStorageManager = DataStorageManager()
    val stopWordManager = StopWordManager()
    val wordFrequencyManager = WordFrequencyManager()
    val wordFrequencyController = WordFrequencyController()
    dataStorageManager.send(DataStorageManager.Init(filename, stopWordManager))
    stopWordManager.send(StopWordManager.Init(wordFrequencyManager))
    wordFrequencyController.send(WordFrequencyController.Run(dataStorageManager))
    with (Executors.newFixedThreadPool(4)) {
        CompletableFuture.runAsync(dataStorageManager, this)
        CompletableFuture.runAsync(stopWordManager, this)
        CompletableFuture.runAsync(wordFrequencyManager, this)
        return CompletableFuture.supplyAsync(wordFrequencyController, this).get()
    }
}