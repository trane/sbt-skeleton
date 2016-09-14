import ReleaseTransformations._
import ScoverageSbtPlugin._
import scala.xml.transform.{RewriteRule, RuleTransformer}
import com.typesafe.sbt.pgp.PgpKeys.publishSigned
import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import org.scalastyle.sbt.ScalastylePlugin.{scalastyleConfig, scalastyle}

/**
 * Default build settings
 */
lazy val buildSettings = Seq(
  organization := "com.dominodatalab",
  scalaVersion := "2.11.8"// uncomment if you need cross scala versions, crossScalaVersions := Seq("2.10.6", "2.11.8")
)

/**
 * Common settings to work across multiple projects
 */
lazy val commonSettings = Seq(
  incOptions := incOptions.value.withLogRecompileOnMacro(false),
  scalacOptions ++= commonScalacOptions,

  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),
  // by default ScalaTest will run suites in Parallel
  // fork in test := true,
  // parallelExecution in Test := false,
  scalacOptions in (Compile, doc) := (scalacOptions in (Compile, doc)).value.filter(_ != "-Xfatal-warnings"),

  // workaround for https://github.com/scalastyle/scalastyle-sbt-plugin/issues/47
  (scalastyleSources in Compile) <++= unmanagedSourceDirectories in Compile,

  testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDF"), // http://www.scalatest.org/user_guide/using_scalatest_with_sbt
  logBuffered in Test := false // use ScalaTests nicer output buffering
) ++ warnUnusedImport

/**
 * Tagging releases for automation
 */
lazy val tagName = Def.setting{
   s"v${if (releaseUseGlobalVersion.value) (version in ThisBuild).value else version.value}"
 }

/**
 * Code coverage
 */
lazy val scoverageSettings = Seq(
  ScoverageKeys.coverageMinimum := 80,
  ScoverageKeys.coverageFailOnMinimum := false,
  ScoverageKeys.coverageHighlighting := scalaBinaryVersion.value != "2.10"
  // uncomment and add your excluded packages if needed
  // ScoverageKeys.coverageExcludedPackages := "domino\\.bench\\..*",
)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

/**
 * insert your publish settings
lazy val publishSettings = Seq(
  homepage := Some(url("https://github.com/cerebrotech/PROJECT.git")),
) ++ releaseSettings
*/

lazy val codeFormattingSettings = SbtScalariform.scalariformSettings ++ Seq(
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference(DoubleIndentClassDeclaration, true)
    .setPreference(SpacesAroundMultiImports, false),
  excludeFilter in SbtScalariform.autoImport.scalariformFormat := HiddenFileFilter || targetFileFilter
)
lazy val targetFileFilter = new FileFilter {
  def accept(f: File) = ".*/target/.*".r.pattern.matcher(f.getAbsolutePath).matches
}

// Due to a bug in Scalastyle plugin, we need to use `in scalastyle` instead of `in Test` to override test config.
// // (see https://github.com/scalastyle/scalastyle-sbt-plugin/issues/44)
// // This should be fixed by https://github.com/scalastyle/scalastyle-sbt-plugin/pull/45.
lazy val scalastyleSettings = scalastyleConfig in scalastyle := file("scalastyle-test-config.xml")
lazy val codeStyleSettings = codeFormattingSettings ++ scalastyleSettings

lazy val licenseSettings = Defaults.coreDefaultSettings

lazy val dominoSettings = buildSettings ++ commonSettings ++ scoverageSettings ++ codeStyleSettings

/**
 * Test dependencies
 *
 * ScalaTest - a well documented, non-surprising testing framework with great community support
 * Scalactic - ScalaTest's sister library focued on quality through types
 *
 */
lazy val testingDependencies = Seq(
  libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.0",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test")

/**
 * Common compiler options
 * For details: scalac -X, scalac -Y
 */
lazy val commonScalacOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:experimental.macros",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yinline-warnings",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-target:jvm-1.8"
)

/**
 * Warn on unused imports only when applicable
 * Note: only applies to scala >= 2.11
 */
lazy val warnUnusedImport = Seq(
  scalacOptions ++= Seq("-Ywarn-unused-import"),
  scalacOptions in (Compile, console) ~= {_.filterNot("-Ywarn-unused-import" == _)},
  scalacOptions in (Test, console) <<= (scalacOptions in (Compile, console))
)

lazy val tests = project
  .settings(moduleName := "tests")
  .settings(dominoSettings)
  .settings(noPublishSettings)
  .settings(testingDependencies)

lazy val bench = project
  .settings(moduleName := "bench")
  .settings(dominoSettings)
  .enablePlugins(JmhPlugin)

lazy val license = project.in(file("license"))
  .settings(licenseSettings:_*)
  .dependsOn(
    domino % "compile->compile"
  )

lazy val domino = project.in(file("."))
  .settings(moduleName := "domino")
  .settings(noPublishSettings)
  .settings(dominoSettings)
  .dependsOn(tests % "test-internal -> test", bench % "compile-internal;test-internal -> test")

// change "domino/test" to the name of your project, e.g. projectname/test
addCommandAlias("validate", ";clean;scalastyle;domino/test")

lazy val dominoReleaseProcess = Seq(
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    releaseStepCommand("validate"),
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    ReleaseStep(action = Command.process("sonatypeReleaseAll", _), enableCrossBuild = false),
    pushChanges)
)
