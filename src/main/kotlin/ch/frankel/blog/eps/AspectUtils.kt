package ch.frankel.blog.eps

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect

@Aspect
class AspectUtils {

    @Around("""execution (* _18_AspectsKt.extractWords(..))
                  || execution (* _18_AspectsKt.frequencies(..))
                  || execution (* _18_AspectsKt.sort(..))""")
    fun profile(joinPoint: ProceedingJoinPoint): Any? {
        val start = System.currentTimeMillis()
        val result = joinPoint.proceed()
        println("Call to ${joinPoint.signature} took ${System.currentTimeMillis() - start} ms")
        return result
    }
}