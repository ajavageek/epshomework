package ch.frankel.blog.eps

fun run(filename: String): Map<String, Int> {
    val wordFrequencies = mutableListOf<WordFrequency>()
    val rawStopWords = read("stop_words.txt")
        .iterator()
        .next()
    val stopWords = mutableListOf<String>()
    var startChar: Int? = 0
    for (i in 0 until rawStopWords.length) {
        if (rawStopWords[i] == ',') {
            val word = rawStopWords.substring(startChar ?: 0, i)
            stopWords.add(word)
            startChar = i + 1
        } else if (i == rawStopWords.length - 1) {
            val word = rawStopWords.substring(startChar ?: 0, rawStopWords.length)
            stopWords.add(word)
        }
    }
    for (line in read(filename)) {
        startChar = null
        var i = 0
        for (c in line) {
            if (startChar == null) {
                if (c.isLetter()) {
                    startChar = i
                }
            } else {
                if (!c.isLetter() || i == line.length - 1) {
                    var found = false
                    if (i == line.length - 1) i++
                    val word = line.substring(startChar, i).toLowerCase()
                    if (!stopWords.contains(word) && word.length >= 2) {
                        for (pair in wordFrequencies) {
                            if (word == pair.first) {
                                pair.second += 1
                                found = true
                                break
                            }
                        }
                        if (!found)
                            wordFrequencies.add(WordFrequency(word, 1))
                    }
                    startChar = null
                }
            }
            i++
        }
    }
    if (wordFrequencies.size > 1) {
        for (a in 0 until wordFrequencies.size) {
            for (b in 1 until wordFrequencies.size - a) {
                if (wordFrequencies[b - 1].second < wordFrequencies[b].second) {
                    val swap = wordFrequencies[b - 1]
                    wordFrequencies[b - 1] = wordFrequencies[b]
                    wordFrequencies[b] = swap
                }
            }
        }
    }
    val top = mutableMapOf<String, Int>()
    for (i in 0 until wordFrequencies.size) {
        top[wordFrequencies[i].first] = wordFrequencies[i].second
        if (i > 23) {
            break
        }
    }
    return top
}

data class WordFrequency(var first: String, var second: Int)