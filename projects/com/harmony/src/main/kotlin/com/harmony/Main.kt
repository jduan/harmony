package com.harmony

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
            if (args.size != 1) {
                throw RuntimeException("ERROR: expect one argument but got ${args.size}")
            }
            val (groupId, artifactId, version) = args[0].split(":")
            val coordinate = MavenCoordinate(groupId, artifactId, version)
            Harmony().run(coordinate)
        }
    }
}
