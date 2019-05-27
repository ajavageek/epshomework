package ch.frankel.blog.eps

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before

@Aspect
class AspectUtils {

    @Around("execution (* ch.frankel.blog.eps..*(..))")
    fun profile(joinPoint: ProceedingJoinPoint): Any? {
        val start = System.currentTimeMillis()
        val result = joinPoint.proceed()
        println("  Call took ${System.currentTimeMillis() - start} ms")
        return result
    }

    @Before("call (* ch.frankel.blog.eps..*(..))")
    fun traceBefore(joinPoint: JoinPoint) {
        println("Start ${joinPoint.signature}")
    }

    @After("call (* ch.frankel.blog.eps..*(..))")
    fun traceAfter(joinPoint: JoinPoint) {
        println("End ${joinPoint.signature}")
    }
}