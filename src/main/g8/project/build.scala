import sbt._
import Keys._
import sbtandroid.AndroidPlugin._

object General {

  // ======================================================================
  // 基本的な設定
  // ======================================================================
  val baseSettings: Seq[Project.Setting[_]] = Seq(
    // アプリの名前
    name := "$name$",
    // アプリのバージョン
    version := "$version$",
    versionCode := $versionCode$,
    // アプリのビルドに使用する Scala のバージョン
    scalaVersion := "$scalaVersion$",
    // ビルド対象のプラットフォーム（targetSdkVersion）
    platformName := "android-$targetSdkVersion$",
    // 署名に使用するエイリアス
    keyalias := "$keyalias$",
    // ProGuard を使用するか否か（true を強く推奨）
    useProguard := true
  )

  // ======================================================================
  // 依存関係の解決に使用するリポジトリ
  // ======================================================================
  val resolvers = Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"
  )

  // ======================================================================
  // 依存関係
  // ======================================================================
  val libraryDependencies = Seq(
    
    // Android Support Library v4 (jar)
    "android.support" % "compatibility-v4" % "19.+",
    
    // Android Support Library v7 (jar & apklib)
    "android.support" % "compatibility-v7-appcompat" % "19.+",
    apklib("android.support" % "compatibility-v7-appcompat" % "19.+")
  
  )

  // ======================================================================
  // Scala コンパイラ起動オプション
  // ======================================================================
  val scalacOptions = Seq("-feature")

  // ======================================================================
  // ProGuard 起動オプション
  // ======================================================================
  val proguardOptions = Seq(
    
    // Scala のバグ？に対応するために必要……な場合もあるらしい。
    // Issue -> https://issues.scala-lang.org/browse/SI-5397
    // ビルドしたアプリケーションがうまく動かない場合はアンコメントを試す
    //"-keep class scala.collection.SeqLike { public protected *;}",
    
    // Android Support Library v4 関連
    "-keep class android.support.v4.app.** { *; }",
    "-keep interface android.support.v4.app.** { *; }",
    
    // Android Support Library v7 関連
    "-keep class android.support.v7.app.** { *; }",
    "-keep interface android.support.v7.app.** { *; }",
    "-keep class android.support.v7.appcompat.** { *; }",
    "-keep interface android.support.v7.appcompat.** { *; }"
  
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
