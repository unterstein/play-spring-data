name := """play-spring-data"""

version := "1.0-SNAPSHOT"

playJavaSettings

libraryDependencies ++= Seq(
    javaCore,
    "javax.inject" % "javax.inject" % "1",
    "org.mockito" % "mockito-core" % "1.9.5" % "test",
    "org.springframework.data" % "spring-data-elasticsearch" % "1.3.1.RELEASE",
    "org.elasticsearch" % "elasticsearch" % "1.7.3"
)