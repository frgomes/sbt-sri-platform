package sri.sbt.platform

import sbt.Keys._
import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin

object ConfigBuilder {

  final val PlatformIOS     = "ios"
  final val PlatformANDROID = "android"
  final val PlatformWEB     = "web"
  final val PlatformEXPO    = "expo"

  final val SJS_OUTPUT_PATH_ANDROID = "assets/js/scalajs-output-android.js"
  final val SJS_OUTPUT_PATH_IOS     = "assets/js/scalajs-output-ios.js"
  final val SJS_OUTPUT_PATH_WEB     = "assets/js/scalajs-output-web.js"
  final val SJS_OUTPUT_PATH_EXPO    = "assets/js/scalajs-output-expo.js"
  final val SJS_OUTPUT_PATH_UNKNOWN = "assets/scalajs-output-unknown-platform.js"

  final val SJS_INDEX_ANDROID = "index.android.js"
  final val SJS_INDEX_IOS     = "index.ios.js"
  final val SJS_INDEX_WEB     = "index.web.js"
  final val SJS_INDEX_EXPO    = "index.expo.js"
  final val SJS_INDEX_UNKNOWN = "index.unknown.js"

  val dev =
    Def.taskKey[Unit]("Generate mobile output file for fastOptJS")

  val prod =
    Def.taskKey[Unit]("Generate mobile output file for fullOptJS")

  @inline
  def getEntryFileName(config: Configuration): String =
    config.name match {
      case PlatformANDROID => SJS_INDEX_ANDROID
      case PlatformIOS     => SJS_INDEX_IOS
      case PlatformWEB     => SJS_INDEX_WEB
      case PlatformEXPO    => SJS_INDEX_EXPO
      case _               => SJS_INDEX_UNKNOWN
    }

  @inline
  def getArtifactPath(config: Configuration): String =
    config.name match {
      case PlatformANDROID => SJS_OUTPUT_PATH_ANDROID
      case PlatformIOS     => SJS_OUTPUT_PATH_IOS
      case PlatformWEB     => SJS_OUTPUT_PATH_WEB
      case PlatformEXPO    => SJS_OUTPUT_PATH_EXPO
      case _               => SJS_OUTPUT_PATH_UNKNOWN
    }

  var isServerStarted: Boolean = false

  val shell: Seq[String] =
    if (sys.props("os.name").contains("Windows")) Seq("cmd", "/c")
    else Seq("bash", "-c")

  val npmStart: Seq[String] =
    shell :+ "react-native start server --transformer scalajsTransformer.js"

  def buildConfig(config: Configuration) = {
    val aPath = getArtifactPath(config)
    val entryFile = getEntryFileName(config)
    inConfig(config)(
      Defaults.compileSettings ++
        ScalaJSPlugin.compileConfigSettings ++ Seq(
        discoveredMainClasses := (discoveredMainClasses in Compile).value,
        mainClass := (mainClass in Compile).value,
        console := (console in Compile).value,
        products := (products in Compile).value,
        classDirectory := (classDirectory in Compile).value,
        artifactPath in fastOptJS := baseDirectory.value / aPath,
        artifactPath in fullOptJS := baseDirectory.value / aPath,
        dev := {
          val indexFile = baseDirectory.value / entryFile
          IO.touch(indexFile, setModified = false)
          val indexContent = IO.read(indexFile)
          (fastOptJS in config).value.data
          val launcher = s"""require("./$aPath");"""
          if (!indexContent.contains(launcher)) IO.append(indexFile, launcher)
        },
        prod := {
          val indexFile = baseDirectory.value / entryFile
          IO.touch(indexFile, setModified = false)
          val indexContent = IO.read(indexFile)
          (fullOptJS in config).value.data
          val launcher = s"""require("./$aPath");"""
          if (!indexContent.contains(launcher)) IO.append(indexFile, launcher)
        }
      ))
  }
}
