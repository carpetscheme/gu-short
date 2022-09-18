ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.carpetscheme"
ThisBuild / organizationName := "carpetscheme"

lazy val root = (project in file("."))
  .settings(
    name := "gu-short",
    libraryDependencies ++= Seq(
      "com.lihaoyi"   %% "cask"                       % "0.8.3",
      "com.lihaoyi"   %% "scalatags"                  % "0.11.1",
      "com.gu"        %% "content-api-client-default" % "19.0.3",
      "org.jsoup"      % "jsoup"                      % "1.15.2",
      "org.typelevel" %% "cats-core"                  % "2.7.0",
      "be.doeraene"   %% "url-dsl"                    % "0.4.0",
      "ch.qos.logback" % "logback-classic"            % "1.2.11"
    )
  )
