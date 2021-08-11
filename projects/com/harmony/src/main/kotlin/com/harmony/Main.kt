package com.harmony

import java.io.File

class Main {
    object Main {
        /**
         * This program takes in a maven package identifier (groupId:artifactId:version) and
         * vendors in the source code of that package as well as all of its transitive dependencies.
         *
         * For example:
         *   ./gradlew :projects:com:harmony:run --args="com.google.guava:guava:24.1-jre"
         */
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size != 2) {
                throw RuntimeException("ERROR: expect two arguments but got ${args.size}")
            }
            val library = args[0]
            val sourceDir = File(args[1])
            val (groupId, artifactId, version) = library.split(":")
            val coordinate = MavenCoordinate(groupId, artifactId, version)
            Harmony(coordinate, sourceDir).run()
        }
    }
}
