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
    lazy val Common  = config("common")
    lazy val IOS     = config(PlatformIOS) extend (Common)
    lazy val Android = config(PlatformANDROID) extend (Common)
    lazy val Web     = config(PlatformWEB) extend (Common)
    lazy val Expo    = config(PlatformWEB) extend (Common)
    lazy val CustomCompile = config("compile") extend (IOS, Android, Expo, Common)
  }

  import autoImport._

  override def projectSettings: Seq[_root_.sbt.Def.Setting[_]] =
    Seq(
      defaultConfiguration := Some(Common),
      scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)),
      ivyConfigurations := overrideConfigs(IOS,
                                           Android,
                                           Web,
                                           Expo,
                                           Common,
                                           CustomCompile)(
        ivyConfigurations.value),
      scalaJSUseMainModuleInitializer := true
    ) ++ buildConfig(Android) ++ buildConfig(IOS) ++ buildConfig(Web) ++ buildConfig(Expo)
}
