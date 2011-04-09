import sbt._
import reaktor.scct.ScctProject

class GoCompilerProject(info: ProjectInfo) extends DefaultProject(info) with reaktor.scct.ScctProject {
  val kiama = "com.googlecode" %% "kiama" % "[1.0.2,)" withSources()
  val scalacheck = "org.scala-tools.testing" %% "scalacheck" % "[1.8,)" withSources() withJavadoc()
  val scalatest = "org.scalatest" % "scalatest" % "[1.3,)"
}
