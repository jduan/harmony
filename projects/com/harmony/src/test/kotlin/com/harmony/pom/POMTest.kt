package com.harmony.pom

import junit.framework.TestCase.assertEquals
import org.junit.Test

class POMTest {
    @Test
    fun test() {
        val pom = POM.loadFromStream(POMTest::class.java.getResourceAsStream("/guava_pom.xml"))
        assertEquals(
            Dependency("com.google.guava", "failureaccess", "1.0.1"),
            pom.dependencies[0]
        )
        assertEquals(
            Dependency("com.google.guava", "listenablefuture", "9999.0-empty-to-avoid-conflict-with-guava"),
            pom.dependencies[1]
        )
        assertEquals(
            Dependency("com.google.code.findbugs", "jsr305", null),
            pom.dependencies[2]
        )
        assertEquals(
            Dependency("org.checkerframework", "checker-qual", null),
            pom.dependencies[3]
        )
        assertEquals(
            Dependency("com.google.errorprone", "error_prone_annotations", null),
            pom.dependencies[4]
        )
        assertEquals(
            Dependency("com.google.j2objc", "j2objc-annotations", null),
            pom.dependencies[5]
        )
    }
}
