package ch.frankel.blog.eps

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before

@Aspect
class AspectUtils {

    @Around("""execution (* _18_AspectsKt.extractWords(..))
                  || execution (* _18_AspectsKt.frequencies(..))
                  || execution (* _18_AspectsKt.sort(..))""")
    fun profile(joinPoint: ProceedingJoinPoint): Any? {
        val start = System.currentTimeMillis()
        val result = joinPoint.proceed()
        println("  Call took ${System.currentTimeMillis() - start} ms")
        return result
    }

    @Before("""call (* _18_AspectsKt.extractWords(..))
                  || call (* _18_AspectsKt.frequencies(..))
                  || call (* _18_AspectsKt.sort(..))""")
    fun traceBefore(joinPoint: JoinPoint) {
        println("Start ${joinPoint.signature}")
    }

    @After("""call (* _18_AspectsKt.extractWords(..))
                  || call (* _18_AspectsKt.frequencies(..))
                  || call (* _18_AspectsKt.sort(..))""")
    fun traceAfter(joinPoint: JoinPoint) {
        println("End ${joinPoint.signature}")
    }
}