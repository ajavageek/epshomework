package ch.frankel.blog.eps

import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class GoodOldTimesTest {

    @DataProvider
    fun data(): Array<Array<Any>> {
        return arrayOf(
            arrayOf(
                "test.txt", mapOf(
                    "acquaintance" to 1,
                    "suppose" to 1,
                    "sure" to 1,
                    "know" to 1
                )
            ),
            arrayOf(
                "input.txt", mapOf(
                    "white" to 1,
                    "tigers" to 1,
                    "live" to 2,
                    "mostly" to 2,
                    "india" to 1,
                    "wild" to 1,
                    "lions" to 1,
                    "africa" to 1
                )
            )
        )
    }

    @Test(dataProvider = "data")
    fun `should return the correct word frequencies for the sample`(filename: String, expected: Map<String, Int>) {
        val millis = System.currentTimeMillis()
        run(filename, millis)
        val wordsFrequency = read("word_freqs_$millis").map {
            val split = it.split(",")
            split[0] to split[1].toInt()
        }.toMap()
        expectThat(wordsFrequency).isEqualTo(expected)
    }
}