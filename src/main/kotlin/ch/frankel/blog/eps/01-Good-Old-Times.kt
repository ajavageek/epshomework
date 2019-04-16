package ch.frankel.blog.eps

fun run(filename: String): Map<String, Int> {
    val data = mutableListOf<Any?>()
    data.add(read("stop_words.txt")[0].split(",")) // data[0] holds the stop words
    data.add("")                      // data[1] is line (max 80 characters)
    data.add(null)                    // data[2] is index of the start_char of word
    data.add(0)                       // data[3] is index on characters, i = 0
    data.add(false)                   // data[4] is flag indicating if word was found
    data.add("")                      // data[5] is the word
    data.add("")                      // data[6] is word,NNNN
    data.add(0)                       // data[7] is frequency
    val f = read(filename).iterator()
    val millis = System.currentTimeMillis()
    while (f.hasNext()) {
        data[1] = f.next()
        data[1] = (data[1] as String) + '\n'
        data[2] = null
        data[3] = 0
        for (c in (data[1] as String).toCharArray()) {
            if (data[2] == null) {
                if (c.isAlphaNumeric()) data[2] = data[3]
            } else if (!c.isAlphaNumeric()) {
                data[4] = false
                data[5] = (data[1] as String).substring(data[2] as Int, data[3] as Int).toLowerCase()
                if ((data[5] as String).length >= 2 && !(data[0] as List<*>).contains(data[5])) {
                    val g = read("word_freqs_$millis").iterator()
                    while (g.hasNext()) {
                        data[6] = g.next()
                        data[7] = (data[6] as String).split(',')[1].toInt()
                        data[6] = (data[6] as String).split(',')[0].trim()
                        if (data[5] == data[6]) {
                            data[7] = (data[7] as Int) + 1
                            data[4] = true
                            break
                        }
                    }
                    if (!(data[4] as Boolean))
                        updateCount("word_freqs_$millis", data[5] as String)
                    else
                        updateCount("word_freqs_$millis", data[5] as String, data[7] as Int)
                }
                data[2] = null
            }
            data[3] = (data[3] as Int) + 1
        }
    }
    data.clear()
    repeat(25) { data.add(arrayOf<Any>()) }
    data.add("")  // data[25] is word,freq from file
    data.add(0)   // data[26] is freq
    val h = read("word_freqs_$millis").iterator()
    while (h.hasNext()) {
        data[25] = h.next()
        data[26] = (data[25] as String).split(",")[1].toInt()
        data[25] = (data[25] as String).split(",")[0]
        for (i in 0..24) {
            if ((data[i] as Array<*>).isEmpty() || ((data[i] as Array<*>)[1] as Int) < data[26] as Int) {
                data.add(i, arrayOf(data[25], data[26]))
                data.removeAt(25)
                break
            }
        }
    }
    return data
        .asSequence()
        .take(25)
        .map { it as Array<*> }
        .filter { it.isNotEmpty() }
        .map { it[0] as String to it[1] as Int }
        .sortedBy { it.second }
        .toMap()
}
