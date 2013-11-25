import sbt._
import Keys._
import sbtandroid.AndroidPlugin._

object General {

  val baseSettings: Seq[Project.Setting[_]] = Seq(
    name            := "$name$",
    version         := "$version$",
    versionCode     := 0,
    scalaVersion    := "$scalaVersion$",
    platformName    := "android-$targetSdkVersion$",
    keyalias        := "$keyalias$",
    useProguard     := true
  )

  val resolvers = Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"
  )

  val libraryDependencies = Seq(
    "android.support" % "compatibility-v4" % "19",
    "android.support" % "compatibility-v7-appcompat" % "19",
    apklib("android.support" % "compatibility-v7-appcompat" % "19")
  )

  val scalacOptions = Seq("-feature")

  val proguardOptions = Seq(
    "-keep class android.support.v4.app.** { *; }",
    "-keep interface android.support.v4.app.** { *; }",
    "-keep class android.support.v7.app.** { *; }",
    "-keep interface android.support.v7.app.** { *; }",
    "-keep class android.support.v7.appcompat.** { *; }",
    "-keep interface android.support.v7.appcompat.** { *; }",
    "-keep class com.android.vending.billing.**",
    /*
       Since SI-5379 is not fixed yet, you need a below line.
       See https://issues.scala-lang.org/browse/SI-5397 for more detail.
     */
    "-keep class scala.collection.SeqLike { public protected *;}"
  )

}

object AndroidBuild extends Build {

  lazy val main = AndroidProject(
    id       = "$name;format="normalize"$",
    base     = file("."),
    settings = General.baseSettings ++ Seq(
      resolvers           ++= General.resolvers,
      libraryDependencies ++= General.libraryDependencies,
      scalacOptions       ++= General.scalacOptions,
      proguardOptions     :=  General.proguardOptions
    )
  )

}
