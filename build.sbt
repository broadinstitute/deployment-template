import Settings.rootSettings
import Testing._

lazy val root = project.in(file("."))
  .settings(rootSettings:_*)
  .withTestSettings

