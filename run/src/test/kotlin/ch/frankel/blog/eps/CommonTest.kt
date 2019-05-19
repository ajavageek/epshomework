package ch.frankel.blog.eps

import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class CommonTest {

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
            ),
            arrayOf(
                "pride-and-prejudice.txt", mapOf(
                    "mr" to 786,
                    "elizabeth" to 635,
                    "very" to 488,
                    "darcy" to 418,
                    "such" to 395,
                    "mrs" to 343,
                    "much" to 329,
                    "more" to 327,
                    "bennet" to 323,
                    "bingley" to 306,
                    "jane" to 295,
                    "miss" to 283,
                    "one" to 275,
                    "know" to 239,
                    "before" to 229,
                    "herself" to 227,
                    "though" to 226,
                    "well" to 224,
                    "never" to 220,
                    "sister" to 218,
                    "soon" to 216,
                    "think" to 211,
                    "now" to 209,
                    "good" to 201,
                    "time" to 203
                )
            )
        )
    }

    @Test(dataProvider = "data")
    fun `should return the correct word frequencies for the sample`(filename: String, expected: Map<String, Int>) {
        val result = run(filename)
        expectThat(result).isEqualTo(expected)
    }
}