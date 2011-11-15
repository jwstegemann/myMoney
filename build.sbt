name := "myMoney"

version := "1.0-SNAPSHOT"

scalaVersion := "2.9.1"

seq(webSettings :_*)

// jetty
libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "8.0.4.v20111024" % "container"

// spray
resolvers += "Akka Repository" at "http://akka.io/repository/"

libraryDependencies ++= Seq(
	"se.scalablesolutions.akka" % "akka-actor" % "1.2" % "compile",
	"se.scalablesolutions.akka" % "akka-slf4j" % "1.2",
	"org.slf4j" % "slf4j-api" % "1.6.1" % "container",
	"ch.qos.logback" % "logback-classic" % "0.9.29" % "container",
	"cc.spray" % "spray-server" % "0.8.0-RC3" % "compile",
	"cc.spray.json" %% "spray-json" % "1.0.1" % "compile",
	"org.glassfish" % "javax.servlet" % "3.0" % "provided",
	"com.mongodb.casbah" % "casbah_2.9.0-1" % "2.1.5.0",
	"org.scala-tools.time" % "time_2.9.0-1" % "0.5"
)
