package sri.sbt.platform

import ConfigBuilder._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys.{defaultConfiguration, ivyConfigurations}
import sbt.{AutoPlugin, Global, config, overrideConfigs}
import sbt._
import sbt.Keys._

object SriPlatformPlugin extends AutoPlugin {
  override lazy val requires = ScalaJSPlugin
  object autoImport {
    lazy val Platform = config("platform")
    lazy val PlatformIOS = config(IOS) extend (Platform)
    lazy val PlatformAndroid = config(ANDROID) extend (Platform)
    lazy val PlatformWeb = config(WEB) extend (Platform)
    lazy val CustomCompile = config("compile") extend (PlatformIOS, PlatformAndroid, Platform)
  }

  import autoImport._

  override def projectSettings: Seq[_root_.sbt.Def.Setting[_]] =
    Seq(
      defaultConfiguration := Some(Platform),
      scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)),
      ivyConfigurations := overrideConfigs(PlatformIOS,
                                           PlatformAndroid,
                                           PlatformWeb,
                                           Platform,
                                           CustomCompile)(
        ivyConfigurations.value),
      scalaJSUseMainModuleInitializer := true
    ) ++ buildConfig(PlatformAndroid) ++ buildConfig(PlatformIOS) ++ buildConfig(PlatformWeb)
}
